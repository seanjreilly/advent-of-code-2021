package day09

import utils.readInput
import utils.gridmap.GridMap
import utils.gridmap.Point

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

internal class HeightMap(data: Array<Array<Int>>) : GridMap<Int>(data, Point::getCardinalNeighbours) {

    internal constructor(rawData: List<String>) : this(rawData.map { it.toCharArray().map { it.digitToInt() }.toTypedArray() }.toTypedArray())

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

}

internal data class Basin(val lowPoint: Point, val members: Collection<Point>)