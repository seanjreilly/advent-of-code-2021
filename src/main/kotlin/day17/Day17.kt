    package day17

import utils.readInput
import kotlin.math.sign

    fun main() {
    val input = readInput("Day17")
    println(part1(input.first()))
    println(part2(input))
}

fun part1(input: String): Long {
    val target = TargetArea(input)

    //min possible x = min value where findFarthestX is past the edge of the target
    val minPossibleXVelocity: Int
    var candidateMinX = 0
    while (findFarthestX(candidateMinX) < target.targetXPosition.first) {
        candidateMinX++
    }
    minPossibleXVelocity = candidateMinX

    //min possible x should always give the max possible y
    //brute force y to a reasonable level
    val result = (0..1000)
        .map { candidateY -> Pair(minPossibleXVelocity, candidateY) }
        .filter { willHitTarget(target, it.first, it.second) }
        .map { Triple(it.first, it.second, findMaximumY(it.second)) }
        .maxByOrNull { it.third }!!

    return result.third
}

fun part2(input: List<String>): Int {
    return input.size
}

internal data class Point(val x: Int, val y: Int)

internal data class TargetArea(val targetXPosition: IntRange, val targetYPosition: IntRange) {
    fun contains(point: Point): Boolean {
        return point.x in targetXPosition && point.y in targetYPosition
    }
}

@Suppress("TestFunctionName") //fake constructor
internal fun TargetArea(input: String) : TargetArea {
    val (x1, x2, y1, y2) = inputRegex.matchEntire(input)!!.destructured
    val xRange = x1.toInt()..x2.toInt()
    val yRange = y1.toInt()..y2.toInt()
    return TargetArea(xRange, yRange)
}
private val inputRegex = """target area: x=(-?\d+)\.\.(-?\d+), y=(-?\d+)\.\.(-?\d+)""".toRegex()


internal fun willHitTarget(target: TargetArea, initialXVelocity: Int, initialYVelocity: Int): Boolean {
    var xPosition = 0
    var yPosition = 0
    var xVelocity = initialXVelocity
    var yVelocity = initialYVelocity

    //once the probe is below the target window and moving down, if it hasn't hit the target it never will
    while (yPosition >= target.targetYPosition.first || yVelocity > 0) {
        xPosition += xVelocity
        yPosition += yVelocity
        xVelocity -= xVelocity.sign
        yVelocity--

        if (target.contains(Point(xPosition, yPosition))) {
            return true
        }
    }
    return false
}

internal fun findMaximumY(initialY: Int): Long {
    return when {
        initialY <= 0 -> initialY.toLong()
        else -> (1L..initialY.toLong()).sum()
    }
}
internal fun findFarthestX(initialX: Int): Int {
    return when {
        initialX == 0 -> 0
        initialX > 0 -> (1..initialX).sum()
        else  -> (initialX.. -1).sum()
    }
}