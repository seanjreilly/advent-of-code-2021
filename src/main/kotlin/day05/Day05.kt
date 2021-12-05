package day05

import readInput

fun main() {
    val input = readInput("Day05")
    println(part1(input))
    println(part2(input))
}

fun part1(input: List<String>): Int {
    return input
        .filter { !it.isBlank() }
        .map { parseLine(it) }
        .filter { it.isHorizontal || it.isVertical }
        .flatMap { it.getPoints() }
        .groupingBy { it }.eachCount().filter { it.value > 1 }.size
}

fun part2(input: List<String>): Int {
    return input
        .filter { !it.isBlank() }
        .map { parseLine(it) }
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
        if (isHorizontal) {
            return range(start.x, end.x).map { Point(it, start.y) }.toSet()
        }
        if (isVertical) {
            return range(start.y, end.y).map { Point(start.x, it) }.toSet()
        }
        //we only need to support 45 degrees
        return range(start.x, end.x).zip(range(start.y, end.y)).map { (x,y) -> Point(x,y) }.toSet()
    }

    val isHorizontal = start.y == end.y
    val isVertical = start.x == end.x

    private fun range(i: Int, j: Int): IntProgression {
        return if (i < j) { i .. j } else { i downTo j }
    }
}