package day23

import utils.readInput
import java.util.*

fun main() {
    val input = readInput("Day23")
    println(part1(input))
    println(part2(input))
}

fun part1(input: List<String>): Int {
    val startingConfiguration = parse(input)

    val visitedConfigurations = mutableSetOf<Configuration>()
    val tentativeDistances = mapOf(startingConfiguration to 0).toMutableMap()

    val unvisitedConfigurations = PriorityQueue<Pair<Configuration, Int>>(compareBy { it.second })
    unvisitedConfigurations += Pair(startingConfiguration, 0)



    while (unvisitedConfigurations.isNotEmpty()) {
        val currentConfiguration = unvisitedConfigurations.remove().first

        //do an extra filter to remove the duplicate entries from the priority queue (see below)
        if (currentConfiguration in visitedConfigurations) {
            continue
        }
        visitedConfigurations += currentConfiguration

        if (currentConfiguration.isFinished()) {
            return tentativeDistances[currentConfiguration]!!
        }

        currentConfiguration.nextMoves()
            .filter { it.first !in visitedConfigurations }
            .forEach { (configuration, cost) ->
                if (configuration !in tentativeDistances.keys) {
                    tentativeDistances[configuration] = Int.MAX_VALUE
                }

                val currentCostOfConfiguration = tentativeDistances[configuration]!!
                val altCost = tentativeDistances[currentConfiguration]!! + cost
                if (altCost < currentCostOfConfiguration) {
                    tentativeDistances[configuration] = altCost
                    unvisitedConfigurations.add(Pair(configuration, altCost)) //don't remove the old entry (slow), just leave a duplicate entry
                }
            }
    }

    throw RuntimeException("couldn't find a path to the desired configuration")
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

private val parseRegex = """.{1,3}?(\w).(\w).(\w).(\w).{1,3}""".toRegex()
internal fun parse(input: List<String>): Configuration {
    fun parseType(firstLetter:String): AmphipodType = AmphipodType.values().find { it.name.startsWith(firstLetter) }!!

    val (a1, b1, c1, d1) = parseRegex.matchEntire(input[2])!!.destructured
    val (a2, b2, c2, d2) = parseRegex.matchEntire(input[3])!!.destructured

    val positions = arrayOfNulls<AmphipodType>(19)
    positions[0] = parseType(a1)
    positions[1] = parseType(a2)
    positions[2] = parseType(b1)
    positions[3] = parseType(b2)
    positions[4] = parseType(c1)
    positions[5] = parseType(c2)
    positions[6] = parseType(d1)
    positions[7] = parseType(d2)

    return Configuration(positions)
}

internal data class Configuration(val positions: Array<AmphipodType?>) {
    fun isFinished(): Boolean {
        return positions.contentEquals(FINISHED_POSITIONS)
    }

    fun nextMoves(): List<Pair<Configuration, Int>> {
        return positions
            .mapIndexed{ index, it -> Pair(index, it) }
            .filter { it.second != null }
            .map { Pair(it.first, it.second!!)}
            .flatMap { (index, amphipod) -> transitions[index]!!.map { Triple(index, amphipod, it) } }
            .filter { (_, _, transition) -> positions[transition.destination] == null} //destination can't be occupied
            .filter { (_, _, transition) -> transition.intermediateSpaces.all { positions[it] == null } } //no intermediate spaces can be occupied
            .filter { (_, amphipod, transition) -> isLegalRoomTransition(transition.destination, amphipod)}
            .map { (index, amphipod, transition) ->
                val newPositions = positions.clone()
                newPositions[index] = null
                newPositions[transition.destination] = amphipod
                Pair(Configuration(newPositions), transition.distance * amphipod.movementCost)
            }
    }

    private fun isLegalRoomTransition(destination: Int, amphipod: AmphipodType) : Boolean {
        val legalRoomIds = mapOf(
            AmphipodType.Amber to setOf(0,1),
            AmphipodType.Bronze to setOf(2,3),
            AmphipodType.Copper to setOf(4,5),
            AmphipodType.Desert to setOf(6,7)
        )

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

    //region override equals and hashcode so data class works as expected

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Configuration

        if (!positions.contentEquals(other.positions)) return false

        return true
    }

    override fun hashCode(): Int {
        return positions.contentHashCode()
    }

    //endregion

    companion object {
        //positions 0-7 rooms
        //positions 8-18 hallway
        //entrance to a room is first top position + 10
        //can't stop on positions with three neighbours
        private val neighbours = mapOf(
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

        private val transitions:Map<Int, Set<Transition>>
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

        internal data class Transition(val intermediateSpaces: Set<Int>, val destination: Int) {
            val distance: Int = intermediateSpaces.size + 1
        }

        private fun findTransition(from:Int, to:Int) : Transition {
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

        private val FINISHED_POSITIONS = arrayOf(
            AmphipodType.Amber, AmphipodType.Amber,
            AmphipodType.Bronze, AmphipodType.Bronze,
            AmphipodType.Copper, AmphipodType.Copper,
            AmphipodType.Desert, AmphipodType.Desert,
            null, null,
            null, null, null, null, null, null, null,
            null, null
        )
    }
}