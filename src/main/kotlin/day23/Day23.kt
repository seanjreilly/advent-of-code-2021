package day23

import utils.readInput

fun main() {
    val input = readInput("Day23")
    println(part1(input))
    println(part2(input))
}

fun part1(input: List<String>): Int {
    return input.size
}

fun part2(input: List<String>): Int {
    return input.size
}

internal enum class AmphipodType(val movementCost: Int) {
    Amber(1),
    Bronze(10),
    Copper(100),
    Desert(1000);
}

internal data class Amphipod(val id: Int, val type: AmphipodType)

internal sealed class Space {
    abstract val id: Int
    lateinit var moves: Set<PotentialMove>
}
internal data class RoomSpace(override val id: Int, val type: AmphipodType) : Space()
internal data class HallwaySpace(override val id: Int): Space()

internal data class PotentialMove(val source: Space, val intermediateSpaces: Set<Space>, val destination: Space) {
    init {
        require(source != destination) { "cannot build a route from a space to itself" }
        if (source is RoomSpace) {
            if (destination is RoomSpace) {
                require(source.type != destination.type) { "cannot build a route from a room to another room of the same type" }
            } else {
                require(intermediateSpaces.count { it is HallwaySpace } > 0) { "amphipods only stop in hallway spaces that don't block rooms" }
            }
        }
        if (source is HallwaySpace) {
            require(destination !is HallwaySpace) { "amphipods only stop in rooms once they are in a hallway" }
        }
    }

    val distance: Int = intermediateSpaces.size + 1
}

// a big ugly map of spaces and how they connect to each other, directly and transitively
// encodes rules like amphipods don't move from hallway to hallway
internal object SpaceMap {
    val hallwaySpaces: List<HallwaySpace> = listOf(
        HallwaySpace(1), //leftmost space
        HallwaySpace(2), //left wing
        HallwaySpace(4), //between room a and room b
        HallwaySpace(6), //between room b and room c
        HallwaySpace(8), //between room c and room d
        HallwaySpace(10), //right wing
        HallwaySpace(11), //rightmost space
    )
    //the hallway spaces where amphipods won't stop
    val intermediateHallwaySpaces: List<HallwaySpace> = listOf(
        HallwaySpace(3), //above room a
        HallwaySpace(5), //above room b
        HallwaySpace(7), //above room c
        HallwaySpace(9), //above room d
    )
    val roomSpaces: List<RoomSpace> = listOf(
        RoomSpace(12, AmphipodType.Amber), //top room
        RoomSpace(13, AmphipodType.Amber), //bottom room
        RoomSpace(14, AmphipodType.Bronze), //top room
        RoomSpace(15, AmphipodType.Bronze), //bottom room
        RoomSpace(16, AmphipodType.Copper), //top room
        RoomSpace(17, AmphipodType.Copper), //bottom room
        RoomSpace(18, AmphipodType.Desert), //top room
        RoomSpace(19, AmphipodType.Desert), //bottom room
    )

    val roomSpacesByType = roomSpaces
        .groupBy { it.type }
        .mapValues { it.value.toSet() }

    //build a mini-graph to build the total routes between the points we need
    private val neighbours = mapOf(
        1 to listOf(2),
        2 to listOf(1, 3),
        3 to listOf(2, 4, 12),
        4 to listOf(3, 5),
        5 to listOf(4, 6, 14),
        6 to listOf(5, 7),
        7 to listOf(6, 8, 16),
        8 to listOf(7, 9),
        9 to listOf(8, 10, 18),
        10 to listOf(9, 11),
        11 to listOf(10),
        12 to listOf(3, 13),
        13 to listOf(12),
        14 to listOf(5, 15),
        15 to listOf(14),
        16 to listOf(7, 17),
        17 to listOf(16),
        18 to listOf(9, 19),
        19 to listOf(18),
    )

    private fun findRouteFrom(path:List<Int>, destination: Int): List<Int>? {
        if (path.last() == destination) { return path }
        val nextSteps = neighbours[path.last()]!!
            .filter { it !in path }

        if (nextSteps.isEmpty()) { return null }

        return nextSteps
            .map { path + it }
            .firstNotNullOfOrNull { findRouteFrom(it, destination) }
    }

    init {
        intermediateHallwaySpaces.forEach { it.moves = emptySet() }

        val allSpaces = (roomSpaces + hallwaySpaces + intermediateHallwaySpaces).associateBy { it.id }

        fun buildMove(from: Space, to:Space): PotentialMove {
            val path = findRouteFrom(listOf(from.id), to.id)!!
            val intermediates = path.drop(1).dropLast(1).map { allSpaces[it]!! }.toSet()
            return PotentialMove(from, intermediates, to)
        }

        hallwaySpaces.forEach { space ->
            space.moves = roomSpaces.map { buildMove(space, it) }.toSet()
        }

        roomSpaces.forEach { space ->
            val routesToHallways = hallwaySpaces.map { buildMove(space, it) }
            val routesToOtherRooms = roomSpaces.filter { it.type != space.type }.map { buildMove(space, it) }
            space.moves = (routesToHallways + routesToOtherRooms).toSet()
        }
    }
}

internal data class Position(val locations:Map<Amphipod, Space>) {
    internal val reverseMap = locations.entries.map { it.value to it.key }.toMap()

    fun nextMoves(): List<Pair<Position, Int>> {
        return locations.flatMap { (amphipod, space) -> //for each amphipod
            space
                .moves //all moves reachable from the amphipod's location
                .filter { !isBlocked(it) } //that aren't occupied or blocked
                .filter { canGo(amphipod, it.destination) } //that the amphipod wants to visit
                .map {
                    val newLocations = locations.toMutableMap()
                    newLocations[amphipod] = it.destination
                    Pair(Position(newLocations), it.distance * amphipod.type.movementCost)
                }
        }
    }

    fun canGo(amphipod: Amphipod, destination: Space) : Boolean {
        if (destination !is RoomSpace) {
            return true
        }
        if (amphipod.type != destination.type) {
            return false
        }
        val amphipodsInRoom = reverseMap.keys.intersect(SpaceMap.roomSpacesByType[destination.type]!!).map { reverseMap[it]!! }
        if (amphipodsInRoom.count { it.type != destination.type } > 0) {
            return false //bad amphipods in the room
        }
        return true
    }

    fun isBlocked(move: PotentialMove): Boolean {
        return move.destination in reverseMap.keys || reverseMap.keys.intersect(move.intermediateSpaces).isNotEmpty()
    }

    fun isFinished(): Boolean {
        SpaceMap.roomSpacesByType
            .flatMap { (key, value) -> value.map { Pair(key, it) } }
            .forEach { (type, space) ->
                val occupant = reverseMap[space]
                if (occupant == null || occupant.type != type) {
                    return false
                }
            }
        return true
    }
}

private val parseRegex = """.{1,3}?(\w).(\w).(\w).(\w).{1,3}""".toRegex()
internal fun parse(input: List<String>): Position {
    fun parseType(firstLetter:String): AmphipodType {
        return AmphipodType.values().find { it.name.startsWith(firstLetter) }!!
    }
    val (a1, b1, c1, d1) = parseRegex.matchEntire(input[2])!!.destructured
    val (a2, b2, c2, d2) = parseRegex.matchEntire(input[3])!!.destructured

    return Position(mapOf(
        Amphipod(1, parseType(a1)) to SpaceMap.roomSpaces[0],
        Amphipod(3, parseType(b1)) to SpaceMap.roomSpaces[2],
        Amphipod(5, parseType(c1)) to SpaceMap.roomSpaces[4],
        Amphipod(7, parseType(d1)) to SpaceMap.roomSpaces[6],
        Amphipod(2, parseType(a2)) to SpaceMap.roomSpaces[1],
        Amphipod(4, parseType(b2)) to SpaceMap.roomSpaces[3],
        Amphipod(6, parseType(c2)) to SpaceMap.roomSpaces[5],
        Amphipod(8, parseType(d2)) to SpaceMap.roomSpaces[7],
    ))
}