package day23

import utils.readInput
import java.util.*

fun main() {
    val input = readInput("Day23")
    println(part1(input))
    println(part2(input))
}

fun part1(input: List<String>): Int {
    val startingSpaceMap = parsePart1(input)
    return findCostOfOrganisingAmphipods(startingSpaceMap)
}

fun part2(input: List<String>): Int {
    val startingSpaceMap = parsePart2(input)
    return findCostOfOrganisingAmphipods(startingSpaceMap)
}

private fun findCostOfOrganisingAmphipods(startingSpaceMap: SpaceMap): Int {
    val visitedSpaceMaps = mutableSetOf<SpaceMap>()
    val tentativeCosts = mapOf(startingSpaceMap to 0).toMutableMap()

    val unvisitedSpaceMaps = PriorityQueue<Pair<SpaceMap, Int>>(compareBy { it.second })
    unvisitedSpaceMaps += Pair(startingSpaceMap, 0)

    while (unvisitedSpaceMaps.isNotEmpty()) {
        val currentSpaceMap = unvisitedSpaceMaps.remove().first

        //do an extra filter to remove the duplicate entries from the priority queue (see below)
        if (currentSpaceMap in visitedSpaceMaps) {
            continue
        }
        visitedSpaceMaps += currentSpaceMap

        if (currentSpaceMap.isFinished()) {
            return tentativeCosts[currentSpaceMap]!!
        }

        currentSpaceMap.nextMoves()
            .filter { it.first !in visitedSpaceMaps }
            .forEach { (spaceMap, cost) ->

                val currentCostOfConfiguration = tentativeCosts[spaceMap]
                val altCost = tentativeCosts[currentSpaceMap]!! + cost
                if (currentCostOfConfiguration == null || altCost < currentCostOfConfiguration) { //might be one we haven't seen before
                    tentativeCosts[spaceMap] = altCost
                    //don't remove the old entry (slow), just leave a duplicate entry
                    unvisitedSpaceMaps.add(Pair(spaceMap, altCost))
                }
            }
    }

    throw RuntimeException("couldn't find a path to the desired configuration")
}

internal enum class AmphipodType(val movementCost: Int) {
    Amber(1),
    Bronze(10),
    Copper(100),
    Desert(1000);
}

private val parseRegex = """.{1,3}?(\w).(\w).(\w).(\w).{1,3}""".toRegex()
private fun parseAmphipodType(firstLetter:String): AmphipodType = AmphipodType.values().find { it.name.startsWith(firstLetter) }!!
internal fun parsePart1(input: List<String>): SpaceMap {
    val (a1, b1, c1, d1) = parseRegex.matchEntire(input[2])!!.destructured.toList().map(::parseAmphipodType)
    val (a2, b2, c2, d2) = parseRegex.matchEntire(input[3])!!.destructured.toList().map(::parseAmphipodType)

    val positions = arrayOfNulls<AmphipodType>(19)
    positions[0] = a1
    positions[1] = a2
    positions[2] = b1
    positions[3] = b2
    positions[4] = c1
    positions[5] = c2
    positions[6] = d1
    positions[7] = d2

    return SpaceMap(positions, Part1Configuration)
}

internal fun parsePart2(input: List<String>): SpaceMap {
    val (a1, b1, c1, d1) = parseRegex.matchEntire(input[2])!!.destructured.toList().map(::parseAmphipodType)
    val (a2, b2, c2, d2) = parseRegex.matchEntire(input[3])!!.destructured.toList().map(::parseAmphipodType)

    val positions = arrayOfNulls<AmphipodType>(27)
    positions[0] = a1
    positions[1] = AmphipodType.Desert
    positions[2] = AmphipodType.Desert
    positions[3] = a2
    positions[4] = b1
    positions[5] = AmphipodType.Copper
    positions[6] = AmphipodType.Bronze
    positions[7] = b2
    positions[8] = c1
    positions[9] = AmphipodType.Bronze
    positions[10] = AmphipodType.Amber
    positions[11] = c2
    positions[12] = d1
    positions[13] = AmphipodType.Amber
    positions[14] = AmphipodType.Copper
    positions[15] = d2

    return SpaceMap(positions, Part2Configuration)
}

internal class SpaceMap(val positions: Array<AmphipodType?>, internal val configuration: Configuration) {

    fun isFinished(): Boolean = positions.contentEquals(configuration.finishedPositions)

    fun nextMoves(): List<Pair<SpaceMap, Int>> {
        return positions
            .mapIndexed{ index, it -> Pair(index, it) }
            .filter { it.second != null }
            .map { Pair(it.first, it.second!!)}
            .flatMap { (index, amphipod) -> configuration.transitions[index]!!.map { Triple(index, amphipod, it) } }
            .filter { (_, _, transition) -> positions[transition.destination] == null} //destination can't be occupied
            .filter { (_, _, transition) -> transition.intermediateSpaces.all { positions[it] == null } } //no intermediate spaces can be occupied
            .filter { (_, amphipod, transition) -> configuration.isLegalRoomTransition(transition.destination, amphipod, positions) }
            .map { (index, amphipod, transition) ->
                val newPositions = positions.clone()
                newPositions[index] = null
                newPositions[transition.destination] = amphipod
                Pair(SpaceMap(newPositions, configuration), transition.distance * amphipod.movementCost)
            }
    }

    //region override equals and hashcode so data class works as expected

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as SpaceMap

        if (!positions.contentEquals(other.positions)) return false

        return true
    }

    override fun hashCode(): Int {
        return positions.contentHashCode()
    }

    //endregion

}

internal object Part1Configuration : Configuration() {
    override val transitions: Map<Int, Set<Transition>>

    override val neighbours: Map<Int, List<Int>> = mapOf(
        0 to listOf(1, 10), //top of A
        1 to listOf(0), // bottom of A
        2 to listOf(3, 12), //top of B
        3 to listOf(2), //bottom of B
        4 to listOf(5, 14), //top of C
        5 to listOf(4), //bottom of C
        6 to listOf(7, 16), //top of D
        7 to listOf(6), //bottom of D
        8 to listOf(9), //hallway
        9 to listOf(8, 10), //hallway
        10 to listOf(9, 11, 0), //hallway, can't stop
        11 to listOf(10, 12), //hallway
        12 to listOf(11, 13, 2), //hallway, can't stop
        13 to listOf(12, 14), //hallway
        14 to listOf(13, 15, 4), //hallway, can't stop
        15 to listOf(14, 16), //hallway
        16 to listOf(15, 17, 6), //hallway, can't stop
        17 to listOf(16, 18), //hallway
        18 to listOf(17), //hallway
    )

    override val finishedPositions: Array<AmphipodType?> = arrayOf(
        AmphipodType.Amber, AmphipodType.Amber,
        AmphipodType.Bronze, AmphipodType.Bronze,
        AmphipodType.Copper, AmphipodType.Copper,
        AmphipodType.Desert, AmphipodType.Desert,
        null, null,
        null, null, null, null, null, null, null,
        null, null
    )

    init {
        val rooms = (0..7).toList()
        val visitableHallwaySpaces = listOf(8,9,11,13,15,17,18)

        val tmp = mutableMapOf<Int, MutableSet<Transition>>()
        (0..18).forEach { tmp[it] = mutableSetOf() }

        visitableHallwaySpaces.forEach { sourceSpace  ->
            rooms.forEach { destinationSpace ->
                tmp[sourceSpace]!!.add(findTransition(sourceSpace, destinationSpace))
            }
        }

        rooms.forEach { sourceSpace ->
            rooms.filter { it != sourceSpace }.forEach { destinationSpace ->
                tmp[sourceSpace]!!.add(findTransition(sourceSpace, destinationSpace))
            }
            visitableHallwaySpaces.forEach {destinationSpace ->
                tmp[sourceSpace]!!.add(findTransition(sourceSpace, destinationSpace))
            }
        }

        transitions = tmp
    }

    private val legalRoomIds = mapOf(
        AmphipodType.Amber to setOf(0,1),
        AmphipodType.Bronze to setOf(2,3),
        AmphipodType.Copper to setOf(4,5),
        AmphipodType.Desert to setOf(6,7)
    )

    override fun isLegalRoomTransition(destination: Int, amphipod: AmphipodType, positions: Array<AmphipodType?>) : Boolean {

        if (destination > 7) {
            return true // we don't care about hallways
        }

        //we already know the destination is empty, or we wouldn't have gotten this far. just check the other space in the room
        val otherSpaceInRoom = if (destination % 2 == 1) { destination - 1 } else { destination + 1 }
        if (positions[otherSpaceInRoom] != null && positions[otherSpaceInRoom] != amphipod) {
            return false //different type so one of them is wrong
        }
        return destination in legalRoomIds[amphipod]!!
    }
}

internal object Part2Configuration : Configuration() {
    override val transitions: Map<Int, Set<Transition>>

    override val neighbours: Map<Int, List<Int>> = mapOf(
        0 to listOf(1, 18), //top of A
        1 to listOf(0, 2), // interior of A
        2 to listOf(1, 3), //interior of A
        3 to listOf(2), // bottom of A
        4 to listOf(5, 20), //top of B
        5 to listOf(4,6), //interior of B
        6 to listOf(5,7), //interior of B
        7 to listOf(6), //bottom of B
        8 to listOf(9, 22), //top of C
        9 to listOf(8, 10), //interior of C
        10 to listOf(9, 11), //interior of C
        11 to listOf(10), //bottom of C
        12 to listOf(13, 24), //top of D
        13 to listOf(12, 14), //interior of D
        14 to listOf(13, 15), //interior of D
        15 to listOf(14), //bottom of D
        16 to listOf(17), //hallway
        17 to listOf(16, 18), //hallway
        18 to listOf(17, 19, 0), //hallway, can't stop
        19 to listOf(18, 20), //hallway
        20 to listOf(19, 21, 4), //hallway, can't stop
        21 to listOf(20, 22), //hallway
        22 to listOf(21, 23, 8), //hallway, can't stop
        23 to listOf(22, 24), //hallway
        24 to listOf(23, 25, 12), //hallway, can't stop
        25 to listOf(24, 26), //hallway
        26 to listOf(25), //hallway
    )

    override val finishedPositions: Array<AmphipodType?> = arrayOf(
        AmphipodType.Amber, AmphipodType.Amber, AmphipodType.Amber, AmphipodType.Amber,
        AmphipodType.Bronze, AmphipodType.Bronze, AmphipodType.Bronze, AmphipodType.Bronze,
        AmphipodType.Copper, AmphipodType.Copper, AmphipodType.Copper, AmphipodType.Copper,
        AmphipodType.Desert, AmphipodType.Desert, AmphipodType.Desert, AmphipodType.Desert,
        null, null,
        null, null, null, null, null, null, null,
        null, null
    )

    init {
        val rooms = (0..15).toList()
        val visitableHallwaySpaces = listOf(16,17,19,21,23,25,26)

        val tmp = mutableMapOf<Int, MutableSet<Transition>>()
        (0..26).forEach { tmp[it] = mutableSetOf() }

        visitableHallwaySpaces.forEach { sourceSpace  ->
            rooms.forEach { destinationSpace ->
                tmp[sourceSpace]!!.add(findTransition(sourceSpace, destinationSpace))
            }
        }

        rooms.forEach { sourceSpace ->
            rooms.filter { it != sourceSpace }.forEach { destinationSpace ->
                tmp[sourceSpace]!!.add(findTransition(sourceSpace, destinationSpace))
            }
            visitableHallwaySpaces.forEach {destinationSpace ->
                tmp[sourceSpace]!!.add(findTransition(sourceSpace, destinationSpace))
            }
        }

        transitions = tmp
    }

    private val legalRoomIds = mapOf(
        AmphipodType.Amber to setOf(0,1,2,3),
        AmphipodType.Bronze to setOf(4,5,6,7),
        AmphipodType.Copper to setOf(8,9,10,11),
        AmphipodType.Desert to setOf(12,13,14,15)
    )

    override fun isLegalRoomTransition(destination: Int, amphipod: AmphipodType, positions: Array<AmphipodType?>) : Boolean {
        if (destination > 15) {
            return true // we don't care about hallways
        }

        //we already know the destination is empty, or we wouldn't have gotten this far. just check the other spaces in the room
        val otherPositionsInRoom = listOf(
            (destination / 4),
            (destination /4) + 1,
            (destination / 4) + 2,
            (destination /4) + 3
        ).filterIndexed{ index, _ -> index != destination % 4}
        otherPositionsInRoom.forEach { otherPositionInRoom ->
            if (positions[otherPositionInRoom] != null && positions[otherPositionInRoom] != amphipod) {
                return false //there are two different types so we know one of them is wrong
            }
        }
        return destination in legalRoomIds[amphipod]!!
    }
}

internal abstract class Configuration {
    internal abstract val finishedPositions: Array<AmphipodType?>
    internal abstract val transitions:Map<Int, Set<Transition>>
    protected abstract val neighbours: Map<Int, List<Int>>
    abstract fun isLegalRoomTransition(destination: Int, amphipod: AmphipodType, positions: Array<AmphipodType?>) : Boolean

    internal data class Transition(val intermediateSpaces: Set<Int>, val destination: Int) {
        val distance: Int = intermediateSpaces.size + 1
    }

    protected fun findTransition(from:Int, to:Int) : Transition {
        val path = findPath(listOf(from), to)!!
        return Transition(path.drop(1).dropLast(1).toSet(), path.last())
    }

    private fun findPath(path:List<Int>, destination: Int): List<Int>? {
        if (path.last() == destination) { return path }
        val nextSteps = neighbours[path.last()]!!
            .filter { it !in path }

        if (nextSteps.isEmpty()) { return null }

        return nextSteps
            .map { path + it }
            .firstNotNullOfOrNull { findPath(it, destination) }
    }
}