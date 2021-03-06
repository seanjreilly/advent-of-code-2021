package day02
import utils.readInput

fun main() {
    val input = readInput("Day02")
    println(part1(input))
    println(part2(input))
}

@Suppress("EnumEntryName")
enum class Direction {
    up,
    down,
    forward
}

fun part1(input: List<String>): Int {
    var depth = 0
    var horizontalPosition = 0

    val directionActions: Map<Direction, (Int) -> Unit> = mapOf(
        Direction.up to { value -> depth -= value },
        Direction.down to { value -> depth += value },
        Direction.forward to { value -> horizontalPosition += value }
    )

    parseAndProcessCommands(input, directionActions)
    return depth * horizontalPosition
}

fun part2(input: List<String>): Int {
    var depth = 0
    var horizontalPosition = 0
    var aim = 0

    val directionActions: Map<Direction, (Int) -> Unit> = mapOf(
        Direction.up to { value -> aim -= value },
        Direction.down to { value -> aim += value },
        Direction.forward to { value -> horizontalPosition += value; depth += (aim * value) }
    )

    parseAndProcessCommands(input, directionActions)
    return depth * horizontalPosition
}

private fun parseAndProcessCommands(input: List<String>, directionActions: Map<Direction, (Int) -> Unit>) {
    input
        .filter(String::isNotBlank)
        .map(::parse)
        .forEach { (direction, distance) -> directionActions[direction]!!.invoke(distance) }
}

private val parseRegex = """\s*(\w+)\s+(\d+)\s*""".toRegex()

internal fun parse(input: String): Pair<Direction, Int> {
    val match = requireNotNull(parseRegex.matchEntire(input)) { "invalid line '${input}'" }
    val (rawDirection, value) = match.destructured
    return Pair(Direction.valueOf(rawDirection), value.toInt())
}