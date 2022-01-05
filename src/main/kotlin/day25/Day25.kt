package day25

import utils.gridmap.Point
import utils.readInput

fun main() {
    val input = readInput("Day25")
    println(part1(input))
    println(part2(input))
}

fun part1(input: List<String>): Int {
    return input.size
}

fun part2(input: List<String>): Int {
    return input.size
}

internal fun parse(input: List<String>): CucumberGrid {
    val height = input.size
    val width = input.first().length
    val points = mutableMapOf<Point, Cucumber>()
    input.forEachIndexed { y, line ->
        require(line.length == width) { "input must be rectangular" }
        line.forEachIndexed { x, char ->
            val cucumber = when (char) {
                '>' -> Cucumber.EAST_FACING
                'v' -> Cucumber.SOUTH_FACING
                '.' -> null
                else -> { throw IllegalArgumentException("unexpected character '${char}'") }
            }
            cucumber?.let { points[Point(x,y)] = it }
        }
    }
    return CucumberGrid(points, width, height)
}

internal class CucumberGrid(private val points: Map<Point, Cucumber>, val width:Int, val height: Int) : Iterable<Map.Entry<Point, Cucumber>> {
    override fun iterator(): Iterator<Map.Entry<Point, Cucumber>> {
        return points.iterator()
    }

    operator fun get(point: Point): Cucumber? {
        return points[point]
    }
}

internal enum class Cucumber { EAST_FACING, SOUTH_FACING }