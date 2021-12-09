package day09

import readInput

fun main() {
    val input = readInput("Day09")
    println(part1(input))
    println(part2(input))
}

fun part1(input: List<String>): Int {
    //the risk level is 1 + the height of the point
    return HeightMap(input).findLowPoints().sumOf { lowPoint -> 1 + HeightMap(input)[lowPoint] }
}

fun part2(input: List<String>): Int {
    return HeightMap(input)
        .findBasins()
        .map { it.members.size }
        .sortedDescending()
        .take(3)
        .reduce { acc,i -> acc * i }
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
    private val data:Array<IntArray> = rawData.map { it.toCharArray().map { it.digitToInt() }.toIntArray() }.toTypedArray()

    init {
        //ensure the map is rectangular
        assert(data.all { it.size == width }) {"every row must be the same size"}
    }

    //points are points on the map with a height smaller than every neighbour
    fun findLowPoints(): Set<Point> {
        return this.filter { this[it] < this.getNeighbours(it).minOf { this[it] } }.toSet()
    }

    fun findBasins(): Collection<Basin> {
        return findLowPoints().map { Basin(it, findBasinMembers(it)) }
    }

    private fun findBasinMembers(lowPoint: Point) : Set<Point> {
        val result = HashSet<Point>()

        //breadth first search, stopping at points with height 9
        val pointsConsidered = HashSet<Point>()
        val queue = mutableListOf(lowPoint) //starting point for the breadth first search
        do {
            val candidate = queue.removeAt(0)
            if (candidate in pointsConsidered) {
                continue //we've already taken a look at this candidate, so skip it
            }
            pointsConsidered += candidate
            if ( this[candidate] == 9) {
                continue //basins end at points of height 9, so skip this one
            }
            result += candidate
            queue.addAll(getNeighbours(candidate))

        } while (queue.isNotEmpty())

        return result
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

internal data class Basin(val lowPoint: Point, val members: Collection<Point>)