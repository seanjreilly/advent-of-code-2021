package day25

import day25.Cucumber.*
import utils.gridmap.Point
import utils.readInput

fun main() {
    val input = readInput("Day25")
    println(part1(input))
    //no part 2 today
}

fun part1(input: List<String>): Int {
    var grid = parse(input)
    var steps = 0
    do {
        val result = grid.step()
        grid = result.first
        steps++
    } while (result.second > 0)
    return steps
}

internal fun parse(input: List<String>): CucumberGrid {
    val height = input.size
    val width = input.first().length
    val points = mutableMapOf<Point, Cucumber>()
    input.forEachIndexed { y, line ->
        require(line.length == width) { "input must be rectangular" }
        line.forEachIndexed { x, char ->
            val cucumber = when (char) {
                '>' -> EAST_FACING
                'v' -> SOUTH_FACING
                '.' -> null
                else -> { throw IllegalArgumentException("unexpected character '${char}'") }
            }
            cucumber?.let { points[Point(x,y)] = it }
        }
    }
    return CucumberGrid(points, width, height)
}

internal data class CucumberGrid(private val points: Map<Point, Cucumber>, val width:Int, val height: Int) : Iterable<Map.Entry<Point, Cucumber>> {
    override fun iterator(): Iterator<Map.Entry<Point, Cucumber>> {
        return points.iterator()
    }

    operator fun get(point: Point): Cucumber? {
        return points[point]
    }

    fun step(): Pair<CucumberGrid, Int> {
        var cucumbersMoved = 0

        fun partStep(previousGrid: Map<Point, Cucumber>, expectedDirection: Cucumber): Map<Point, Cucumber> {
            return previousGrid.asIterable().associate {
                if (it.value == expectedDirection) {
                    val destination = destination(it)
                    val newPoint = if (previousGrid[destination] == null) { cucumbersMoved++; destination } else it.key
                    Pair(newPoint, it.value)
                } else {
                    Pair(it.key, it.value)
                }
            }
        }

        val pointsAfterPartA = partStep(points, EAST_FACING)
        val pointsAfterPartB = partStep(pointsAfterPartA, SOUTH_FACING)
        return Pair(CucumberGrid(pointsAfterPartB, width, height), cucumbersMoved)
    }

    private fun destination(entry: Map.Entry<Point, Cucumber>): Point {
        return when (entry.value) {
            EAST_FACING -> Point((entry.key.x + 1) % width, entry.key.y)
            SOUTH_FACING -> Point(entry.key.x, (entry.key.y + 1) % height)
        }
    }
}

internal enum class Cucumber { EAST_FACING, SOUTH_FACING }