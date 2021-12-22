package day22

import utils.readInput
import java.lang.IllegalArgumentException

fun main() {
    val input = readInput("Day22")
    println(part1(input))
    println(part2(input))
}

fun part1(input: List<String>): Int {
    return input.size
}

fun part2(input: List<String>): Int {
    return input.size
}

internal data class Cuboid(val xRange: IntRange, val yRange: IntRange, val zRange: IntRange) {
    val size = when {
        xRange.isEmpty() -> 0
        yRange.isEmpty() -> 0
        zRange.isEmpty() -> 0
        else -> (xRange.last - xRange.first + 1) * (yRange.last - yRange.first + 1) * (zRange.last - zRange.first + 1)
    }

    fun contains(other: Cuboid): Boolean {
        return xRange.contains(other.xRange.first) && xRange.contains(other.xRange.last) &&
            yRange.contains(other.yRange.first) && yRange.contains(other.yRange.last) &&
            zRange.contains(other.zRange.first) && zRange.contains(other.zRange.last)
    }

    fun intersects(other: Cuboid): Boolean {
        if (other.contains(this)) {
            return true
        }
        return (xRange.contains(other.xRange.first) || xRange.contains(other.xRange.last())) &&
            (yRange.contains(other.yRange.first) || yRange.contains(other.yRange.last())) &&
            (zRange.contains(other.zRange.first) || zRange.contains(other.zRange.last()))
    }

    operator fun minus(other: Cuboid): List<Cuboid> {
        if (other.contains(this)) {
            return emptyList()
        }
        if (!intersects(other)) {
            return listOf(this)
        }
        val fragments = listOf(
            Cuboid(xRange, yRange, zRange.first until other.zRange.first), //work
            Cuboid(xRange, yRange, other.zRange.last + 1 .. zRange.last), //work

            Cuboid(xRange.first until other.xRange.first, other.yRange, zRange), //work
            Cuboid(other.xRange.last + 1 .. xRange.last, other.yRange, zRange), //work


            Cuboid(other.xRange, yRange.first until other.yRange.first, other.zRange),
            Cuboid(other.xRange, other.yRange.last + 1 .. yRange.last, other.zRange),
        )
        val result = fragments.filter { it.size > 0 }
        assert(result.sumOf { it.size } == this.size - other.size)
        return result
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