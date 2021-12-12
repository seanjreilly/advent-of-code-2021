package day12

import utils.readInput

fun main() {
    val input = readInput("Day12")
    println(part1(input))
    println(part2(input))
}

fun part1(input: List<String>): Int {
    return findDistinctPaths(parseGraph(input), Path::neverVisitsASmallCaveMoreThanOnce).size
}

fun part2(input: List<String>): Int {
    return findDistinctPaths(parseGraph(input), Path::canVisitASingleSmallCaveTwice).size
}

internal fun parseGraph(input: List<String>): Graph {
    return input
        .map { it.split('-') }
        .flatMap { listOf(it, it.reversed()) } //if a links to b, b links to a
        .groupBy({ it[0] }, { it[1] })
        .mapValues { it.value.toSet()  }
}

internal typealias Graph = Map<String, Set<String>> //key is source vertex, value is set of vertices connected to source
internal typealias Path = List<String>

internal fun isSmallCave(cave: String): Boolean = cave.first().isLowerCase()

internal fun Path.neverVisitsASmallCaveMoreThanOnce() : Boolean {
    return this.filter { isSmallCave(it) }
        .groupingBy { it }
        .eachCount()
        .filter { it.value > 1 }
        .isEmpty()
}

internal fun Path.canVisitASingleSmallCaveTwice() : Boolean {
    //"start" and "end" can still only be visited once, no matter what
    if (this.count { it == "start" } > 1 || this.count { it == "end" } > 1) {
        return false
    }

    val smallCaveVisitCount = this.filter { isSmallCave(it) }
        .groupingBy { it }
        .eachCount()

    //contains only a single small cave visited more than once and never visits a small cave more than twice
    return smallCaveVisitCount.count { it.value > 1 } < 2 && smallCaveVisitCount.count { it.value > 2 } == 0
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

        //complete paths end with "end" — add them to the result, and the rest to the queue
        val (completePaths, incompletePaths) = nextPathsToConsider.partition { it.last() == "end" }
        completePaths.forEach(completedPaths::add)
        incompletePaths.forEach(queue::add)
    } while (queue.isNotEmpty())

    return completedPaths
}