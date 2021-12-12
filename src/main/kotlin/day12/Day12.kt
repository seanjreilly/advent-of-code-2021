package day12

import utils.readInput

fun main() {
    val input = readInput("Day12")
    println(part1(input))
    println(part2(input))
}

fun part1(input: List<String>): Int {
    return findDistinctPaths(parseGraph(input)).size
}

fun part2(input: List<String>): Int {
    return input.size
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

/*
    Valid paths never visit any small cave more than once
 */
internal fun Path.isValid() : Boolean {
    return this.filter { isSmallCave(it) }
        .groupingBy { it }
        .eachCount()
        .filter { it.value > 1 }
        .isEmpty()
}

internal fun findDistinctPaths(graph: Graph): Set<Path> {
    val completedPaths = mutableSetOf<Path>()
    val queue = mutableListOf(listOf("start"))
    do {
        val currentPath = queue.removeAt(0)
        val nextVerticesToConsider = graph[currentPath.last()]!!
        val nextPathsToConsider = nextVerticesToConsider
            .map { currentPath + it }
            .filter { it.isValid() }

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