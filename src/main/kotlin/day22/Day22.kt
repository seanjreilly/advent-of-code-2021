package day22

import utils.readInput
import java.lang.IllegalArgumentException
import java.util.*
import kotlin.math.max
import kotlin.math.min

fun main() {
    val input = readInput("Day22")
    println(part1(input))
    println(part2(input))
}

fun part1(input: List<String>): Long {
    val validRange = -50..50
    operator fun IntRange.contains(other:IntRange): Boolean {
        return other.first in this && other.last in this
    }
    val instructions = parse(input)
        .filter { it.second.xRange in validRange }
        .filter { it.second.yRange in validRange }
        .filter { it.second.zRange in validRange }

    val result = processInstructions(instructions)
    return result.sumOf { it.size }
}

fun part2(input: List<String>): Int {
    return input.size
}

internal fun processInstructions(instructions: List<Pair<Boolean, Cuboid>>): MutableList<Cuboid> {
    val result = mutableListOf<Cuboid>()
    val queue = LinkedList(instructions)
    while (!queue.isEmpty()) {
        val (operation, cuboid) = queue.remove()
        var i = 0
        while (i < result.size) {
            val otherCuboid = result[i]
            if (cuboid.contains(otherCuboid)) {
                //remove other cuboid from result, continue with this one
                result.removeAt(i)
                continue
            }
            if (cuboid.intersects(otherCuboid)) {
                //replace other cuboid with difference (the parts of other that don't intersect this cuboid), continue with this one
                result.removeAt(i)
                val intersection = otherCuboid - cuboid
                intersection.forEachIndexed { intersectionIndex, newCuboid ->
                    result.add(
                        i + intersectionIndex,
                        newCuboid
                    )
                }
                i += (intersection.size)
                continue
            }
            i++
        }
        if (operation) {
            result.add(cuboid) //only need to store "ons"
        }
    }
    return result
}

internal data class Cuboid(val xRange: IntRange, val yRange: IntRange, val zRange: IntRange) {
    val size = when {
        xRange.isEmpty() -> 0
        yRange.isEmpty() -> 0
        zRange.isEmpty() -> 0
        else -> (xRange.last - xRange.first + 1).toLong() * (yRange.last - yRange.first + 1).toLong() * (zRange.last - zRange.first + 1).toLong()
    }

    fun contains(other: Cuboid): Boolean {
        return xRange.contains(other.xRange.first) && xRange.contains(other.xRange.last) &&
            yRange.contains(other.yRange.first) && yRange.contains(other.yRange.last) &&
            zRange.contains(other.zRange.first) && zRange.contains(other.zRange.last)
    }

    fun intersects(other: Cuboid): Boolean {
        fun IntRange.overlaps(other: IntRange) : Boolean {
            return contains(other.first) || contains(other.last) || other.contains(first) || other.contains(last)
        }

        return xRange.overlaps(other.xRange) && yRange.overlaps(other.yRange) && zRange.overlaps(other.zRange)
    }

    operator fun minus(other: Cuboid): List<Cuboid> {
        if (other.contains(this)) {
            return emptyList()
        }
        if (!intersects(other)) {
            return listOf(this)
        }

        var currentXRange = xRange
        var currentYRange = yRange
        val currentZRange = zRange
        val fragments = mutableListOf<Cuboid>()
        fragments += Cuboid(currentXRange.first until other.xRange.first, currentYRange, currentZRange)
        fragments += Cuboid(other.xRange.last + 1 .. currentXRange.last, currentYRange, currentZRange)

        currentXRange = max(currentXRange.first, other.xRange.first) .. min(currentXRange.last, other.xRange.last)

        fragments += Cuboid(currentXRange, currentYRange.first until other.yRange.first, currentZRange)
        fragments += Cuboid(currentXRange, other.yRange.last + 1 .. currentYRange.last, currentZRange)

        currentYRange = max(currentYRange.first, other.yRange.first) .. min(currentYRange.last, other.yRange.last)

        fragments += Cuboid(currentXRange, currentYRange, currentZRange.first until other.zRange.first)
        fragments += Cuboid(currentXRange, currentYRange, other.zRange.last + 1 .. currentZRange.last)

        return fragments.filter { it.size > 0 }
    }
}

private val parseRegex = """(on|off) x=(-?\d+)..(-?\d+),y=(-?\d+)..(-?\d+),z=(-?\d+)..(-?\d+)""".toRegex()
internal fun parse(input: List<String>): List<Pair<Boolean, Cuboid>> {
    return input.map { line ->
        val matches = parseRegex.matchEntire(line)!!.groupValues.drop(1)
        val operation = when(matches.first()) {
            "on" -> true
            "off" -> false
            else -> throw IllegalArgumentException("unexpected command '${matches.first()}' supported commands are 'on' and 'off'")
        }

        val (xRange, yRange, zRange) = matches.drop(1)
            .map (String::toInt)
            .windowed(2, 2)
            .map { it[0]..it[1] }

        Pair(operation, Cuboid(xRange, yRange, zRange))
    }
}