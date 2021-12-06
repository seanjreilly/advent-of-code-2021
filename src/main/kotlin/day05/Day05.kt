package day05

import readInput
import kotlin.math.max

fun main() {
    val input = readInput("Day05")
    println(part1(input))
    println(part2(input))
}

fun part1(input: List<String>): Int {
    return countOverlaps(input, false)
}

fun part2(input: List<String>): Int {
    return countOverlaps(input, true)
}

private fun countOverlaps(input: List<String>, allowDiagonals: Boolean): Int {
    return input
        .filter(String::isNotBlank)
        .map { parseLine(it) }
        .filter { if (allowDiagonals) { true } else { !it.isDiagonal } }
        .flatMap { it.getPoints() }
        .groupingBy { it }.eachCount().filter { it.value > 1 }.size
}

private fun parseLine(line:String) : Line {
    val (x1, y1, x2, y2) = LINE_DEFINITION_REGEX.matchEntire(line)!!.destructured
    val start = Point(x1.toInt(), y1.toInt())
    val end = Point(x2.toInt(), y2.toInt())
    return Line(start,end)
}

val LINE_DEFINITION_REGEX = """(\d+),(\d+).+?(\d+),(\d+)""".toRegex()

internal data class Point(val x: Int, val y: Int)
internal data class Line(val start: Point, val end: Point) {
    fun getPoints() : Set<Point> {
        //we only need to support vertical, horizontal, and 45 degree diagonal, so we can use a zip if we pad the shorter range
        return list(start.x, end.x).zipWithExtension(list(start.y, end.y)).map { (x,y) -> Point(x,y) }.toSet()
    }

    val isDiagonal = start.y != end.y && start.x != end.x
}

private fun list(i: Int, j: Int): List<Int> {
    return (if (i < j) { i .. j } else { i downTo j }).toList()
}

private fun <T> List<T>.zipWithExtension(other: List<T>): List<Pair<T, T>> {
    val maxLen = max(this.size, other.size)
    return this.pad(maxLen).zip(other.pad(maxLen))
}

internal fun <T> List<T>.pad(expectedSize: Int): List<T> {
    return this.plus(List(expectedSize - size) { this.last() }) //negative number builds an empty list
}