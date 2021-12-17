package day17

import utils.readInput
import kotlin.math.abs
import kotlin.math.sign

fun main() {
    val input = readInput("Day17")
    println(part1(input.first()))
    println(part2(input.first()))
}

fun part1(input: String): Long {
    val target = TargetArea(input)

    //minimum possible x give the highest possible y
    val minPossibleXVelocity: Int = calculateMinPossibleXVelocity(target)

    // every possible velocity encountered on the way up is encountered in reverse on the way down,
    // and therefore the probe will *always* hit y = 0, having moved -initalY to get there.
    // The next step it will move to a vertical position of -(initialY+1).
    // A maximum initialY velocity will hit the very bottom of the target envelope.
    // Therefore, initialY = abs(target.minY) - 1
    val initialYVelocity = abs(target.targetYPosition.first) - 1

    //ensure this combination actually hits the target
    assert(willHitTarget(target, minPossibleXVelocity, initialYVelocity))
    return findMaximumY(initialYVelocity)
}

fun part2(input: String): Int {
    val target = TargetArea(input)

    val minPossibleXVelocity = calculateMinPossibleXVelocity(target)
    val maxPossibleXVelocity = target.targetXPosition.last() //any higher, and the probe is guaranteed to shoot past in one step
    val minPossibleYVelocity = target.targetYPosition.first //any lower, and the probe is guaranteed to shoot below in one step
    //we know from part 1 that any velocity over abs(target.minY) - 1 will miss the window on the way down
    val maxPossibleYVelocity = abs(target.targetYPosition.first) - 1

    val potentialInputVelocities = (minPossibleXVelocity..maxPossibleXVelocity)
        .flatMap { x -> (minPossibleYVelocity..maxPossibleYVelocity).map { y -> Pair(x, y) } }

    return potentialInputVelocities.count { willHitTarget(target, it.first, it.second) }
}

/*
The minimum x velocity that can hit a target is always
the smallest value where firstFarthestX is within the target
 */
private fun calculateMinPossibleXVelocity(target: TargetArea): Int {
    val minPossibleXVelocity: Int
    var candidateMinX = 0
    while (findFarthestX(candidateMinX) !in target.targetXPosition) {
        candidateMinX++
    }
    minPossibleXVelocity = candidateMinX
    return minPossibleXVelocity
}

internal data class Point(val x: Int, val y: Int)

internal data class TargetArea(val targetXPosition: IntRange, val targetYPosition: IntRange) {
    fun contains(point: Point): Boolean {
        return point.x in targetXPosition && point.y in targetYPosition
    }
}

@Suppress("TestFunctionName") //fake constructor
internal fun TargetArea(input: String): TargetArea {
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
        else -> (initialX..-1).sum()
    }
}