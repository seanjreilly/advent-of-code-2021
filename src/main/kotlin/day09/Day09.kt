package day09

import readInput

fun main() {
    val input = readInput("Day09")
    println(part1(input))
    println(part2(input))
}

fun part1(input: List<String>): Int {
    //low points are points on the map with a height smaller than every neighbour
    //the risk level is 1 + the height of the point
    val heightMap = HeightMap(input)
    return heightMap
        .map { Pair(it, heightMap[it]) }
        .filter { (point, height) -> height < heightMap.getNeighbours(point).minOf { heightMap[it] } }
        .sumOf { (_, height) -> 1+ height }
}

fun part2(input: List<String>): Int {
    return input.size
}

internal class HeightMap(rawData: List<String>) : Iterable<Point> {
    operator fun get(point: Point): Int = data[point.y][point.x]
    fun getNeighbours(it: Point): Collection<Point> {
        return listOf(
            it.north(),
            it.south(),
            it.west(),
            it.east()
        ).filter { it.x in (0 until width) && it.y in (0 until height) }
    }

    val height = rawData.size
    val width = rawData.first().length
    val data:Array<IntArray> = rawData.map { it.toCharArray().map { it.digitToInt() }.toIntArray() }.toTypedArray()

    init {
        //ensure the map is rectangular
        assert(data.all { it.size == width }) {"every row must be the same size"}
    }

    override fun iterator() = iterator {
        for (x in 0 until width) {
            for (y in 0 until height) {
                yield(Point(x,y))
            }
        }
    }
}

internal data class Point(val x: Int, val y: Int) {
    internal fun north() = Point(x, y - 1)
    internal fun south() = Point(x, y + 1)
    internal fun east() = Point(x + 1, y)
    internal fun west() = Point(x - 1, y)
}