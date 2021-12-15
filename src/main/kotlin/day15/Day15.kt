package day15

import utils.gridmap.GridMap
import utils.gridmap.Point
import utils.readInput
import java.util.*

fun main() {
    val input = readInput("Day15")
    println(part1(input))
    println(part2(input))
}

fun part1(input: List<String>): CumulativeRisk {
    val map = RiskMap(input)
    val source = Point(0,0)
    val destination = Point(map.width, map.height).northWest()
    return map.findLowestRiskPath(source, destination).last().second
}

fun part2(input: List<String>): Int {
    val map = RiskMap(input).makeBiggerMap()
    val source = Point(0,0)
    val destination = Point(map.width, map.height).northWest()
    return map.findLowestRiskPath(source, destination).last().second
}

internal class RiskMap(data: Array<Array<Risk>>) : GridMap<Risk>(data, Point::getCardinalNeighbours) {
    constructor(input: List<String>) : this(input.map { it.toCharArray().map(Char::digitToInt).toTypedArray() }.toTypedArray())

    fun findLowestRiskPath(source: Point, destination: Point): Path {
        //Djikstra's algorithm
        val tentativeDistances = this.associateWith { Int.MAX_VALUE }.toMutableMap()
        tentativeDistances[source] = 0

        val unvisitedPoints = PriorityQueue<Pair<Point, Int>>(compareBy { it.second })
        tentativeDistances.forEach { (point, distance) ->
            unvisitedPoints.add(Pair(point, distance))
        }
        val cheapestNeighbour = mutableMapOf<Point, Point>()
        val visitedPoints = mutableSetOf<Point>()

        while (unvisitedPoints.isNotEmpty()) {
            val currentPoint = unvisitedPoints.remove().first

            //do an extra filter to remove the duplicate entries from the priority queue (see below)
            if (currentPoint in visitedPoints) {
                continue
            }

            visitedPoints += currentPoint

            if (currentPoint == destination) {
                break
            }

            getNeighbours(currentPoint)
                .filter { it !in visitedPoints }
                .forEach { point ->
                    val currentDistanceToPoint = tentativeDistances[point]!!
                    val altDistance = tentativeDistances[currentPoint]!! + this[point]
                    if (altDistance < currentDistanceToPoint) {
                        tentativeDistances[point] = altDistance
                        cheapestNeighbour[point] = currentPoint
                        unvisitedPoints.add(Pair(point, altDistance)) //don't remove the old point (slow), just leave a duplicate entry
                    }
                }
        }

        //unroll weights to find the cheapest path
        val shortestPath = emptyList<Pair<Point, CumulativeRisk>>().toMutableList()
        var currentPoint = destination
        do {
            shortestPath.add(Pair(currentPoint, tentativeDistances[currentPoint]!!))
            currentPoint = cheapestNeighbour[currentPoint]!!
        } while (currentPoint != source)
        shortestPath.add(Pair(source, 0))
        return shortestPath.reversed()
    }

    fun makeBiggerMap(): RiskMap {
        val newHeight = height * 5
        val newWidth = width * 5

        val newData = (0 until newHeight) .map { y ->
            Array(newWidth) { x ->
                val oldPoint = Point(x % width, y % height)
                val widthMultiple = x / width
                val heightMultiple = y / height
                var value:Int = this[oldPoint] + widthMultiple + heightMultiple
                while (value > 9) { value -= 9 }
                value
            }
        }.toTypedArray()

        return RiskMap(newData)
    }
}

internal typealias CumulativeRisk = Int
internal typealias Risk = Int
internal typealias Path = List<Pair<Point, CumulativeRisk>>