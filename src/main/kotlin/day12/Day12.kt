package day12

import utils.readInput
import kotlin.math.max

fun main() {
    val input = readInput("Day12")
    println(part1(input))
    println(part2(input))
}

fun part1(input: List<String>): Int {
    return findDistinctPaths(parseGraph(input), Path::neverVisitsASmallCaveMoreThanOnce).size
}

fun part2(input: List<String>): Int {
    return findDistinctPaths(parseGraph(input), Path::canVisitASingleCaveTwice).size
}

internal fun parseGraph(input: List<String>): Graph {
    return input
        .map { it.split('-') }
        .flatMap { listOf(listOf(it[0], it[1]), listOf(it[1], it[0])) } //if a links to b, b links to a
        .groupBy({ it[0] }, { it[1] })
        .mapValues { it.value.toSet()  }
}

internal typealias Graph = Map<String, Set<String>> //key is source vertex, value is set of vertices connected to source

internal fun isSmallCave(cave: String): Boolean {
    return cave.first().isLowerCase()
}

internal typealias Path = List<String>

internal fun Path.neverVisitsASmallCaveMoreThanOnce() : Boolean {
    return this.filter { isSmallCave(it) }
        .groupingBy { it }
        .eachCount()
        .filter { it.value > 1 }
        .isEmpty()
}

internal fun Path.canVisitASingleCaveTwice() : Boolean {
    //only contains "start" once and "end" once
    if (max(this.filter { it == "start" }.size, this.filter { it == "end" }.size) > 1) {
        return false
    }

    val smallCavesVisitedMoreThanOnce = this.filter { isSmallCave(it) }
        .groupingBy { it }
        .eachCount()
        .filter { it.value > 1 }

    //contains only a single cave visited more than once and never visits a cave more than twice
    return smallCavesVisitedMoreThanOnce.size < 2 && smallCavesVisitedMoreThanOnce.filter { it.value > 2 }.isEmpty()
}

internal fun findDistinctPaths(graph: Graph, validPathFunction: (Path) -> Boolean): Set<Path> {
    val completedPaths = mutableSetOf<Path>()
    val queue = mutableListOf(listOf("start"))
    do {
        val currentPath = queue.removeAt(0)
        val nextVerticesToConsider = graph[currentPath.last()]!!
        val nextPathsToConsider = nextVerticesToConsider
            .map { currentPath + it }
            .filter(validPathFunction)

        //add any complete paths to the result
        nextPathsToConsider
            .filter { it.last() == "end" }
            .forEach(completedPaths::add)

        //add any incomplete paths to the queue
        nextPathsToConsider
            .filter { it.last() != "end" }
            .forEach(queue::add)
    } while (queue.isNotEmpty())

    return completedPaths
}