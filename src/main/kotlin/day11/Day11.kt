package day11

import utils.gridmap.GridMap
import utils.gridmap.Point
import utils.readInput

fun main() {
    val input = readInput("Day11")
    println(part1(input))
    println(part2(input))
}

fun part1(input: List<String>): Int {
    return countTotalFlashes(input, 100)
}

fun part2(input: List<String>): Int {
    val map = OctopusMap(input)
    val octopusCount = map.count()
    var flashed: Int
    var step = 0
    do {
        flashed = map.step()
        step++
    } while (flashed < octopusCount)
    return step
}

internal fun countTotalFlashes(input: List<String>, stepsToRun: Int): Int {
    val map = OctopusMap(input)
    return (1..stepsToRun)
        .map { map.step() }
        .sum()
}

internal class OctopusMap(data: Array<Array<Octopus>>) : GridMap<Octopus>(data) {

    constructor(rawData: List<String>) : this(rawData.map { it.toCharArray().map { Octopus(it.digitToInt()) }.toTypedArray() }.toTypedArray())

    fun getNeighbours(point: Point): Collection<Point> {
        return point.getCardinalAndDiagonalNeighbours()
            .filter { isPointInMap(it) }
    }

    fun step(): Int {
        fun findNewlyFlashingOctopuses() = this.map { Pair(it, this[it]) }
            .filter { !it.second.hasFlashed }
            .filter {it.second.energyLevel > 9
            }

        //every octopus gains an energy level
        this.forEach { this[it].energyLevel++ }

        //find every octopus with enough energy to flash (that hasn't flashed yet)
        var flashingOctopuses = findNewlyFlashingOctopuses()
        while (flashingOctopuses.isNotEmpty()) {
            flashingOctopuses.forEach { (point, octopus) ->
                //mark the octopus as having flashed
                octopus.hasFlashed = true

                //give energy to any neighbours that haven't already flashed
                getNeighbours(point)
                    .map { this[it] }
                    .filter { !it.hasFlashed }
                    .forEach { it.energyLevel++ }
            }
            flashingOctopuses = findNewlyFlashingOctopuses()
        }

        val allOctopusesThatFlashedThisStep = this
            .map { this[it] }
            .filter { it.hasFlashed }
        allOctopusesThatFlashedThisStep.forEach { it.reset() }
        return allOctopusesThatFlashedThisStep.size
    }
}

internal data class Octopus(var energyLevel: Int) {
    var hasFlashed = false

    fun reset() {
        energyLevel = 0
        hasFlashed = false
    }
}