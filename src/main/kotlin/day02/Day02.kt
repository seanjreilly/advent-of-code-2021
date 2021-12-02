package day02
import readInput

fun main() {
    val input = readInput("Day02")
    val day02 = Day02()
    println(day02.part1(input))
    println(day02.part2(input))
}

class Day02 {

    var depth = 0
    var horizontalPosition = 0

    enum class Direction {
        up,
        down,
        forward
    }

    val directionActions: Map<Direction, (Int) -> Unit> = mapOf(
        Direction.up to {value -> depth -= value},
        Direction.down to {value -> depth += value},
        Direction.forward to {value -> horizontalPosition += value }
    )

    fun part1(input: List<String>): Int {
        input
            .filter(String::isNotBlank)
            .map(this::parse)
            .forEach {(direction, distance) -> directionActions[direction]!!.invoke(distance) }
        return depth * horizontalPosition
    }

    fun part2(input: List<String>): Int {
        return input.size
    }

    private val parseRegex = """\s*(\w+)\s+(\d+)\s*""".toRegex()

    fun parse(input: String): Pair<Direction, Int> {
        val match = requireNotNull(parseRegex.matchEntire(input)) { "invalid line '${input}'" }
        val (rawDirection, value) = match.destructured
        return Pair(Direction.valueOf(rawDirection), value.toInt())
    }
}