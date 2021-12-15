package day15

import utils.gridmap.GridMap
import utils.gridmap.Point
import utils.readInput

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
    return input.size
}

internal class RiskMap(data: Array<Array<Risk>>) : GridMap<Risk>(data, Point::getCardinalNeighbours) {
    constructor(input: List<String>) : this(input.map { it.toCharArray().map(Char::digitToInt).toTypedArray() }.toTypedArray())

    fun findLowestRiskPath(source: Point, destination: Point): Path {
        //Djikstra's algorithm
        val unvisitedPoints = this.toMutableSet()
        val tentativeDistances = this.associateWith { Int.MAX_VALUE }.toMutableMap()
        tentativeDistances[source] = 0
        val cheapestNeighbour = mutableMapOf<Point, Point>()

        while (unvisitedPoints.isNotEmpty()) {
            val currentPoint = unvisitedPoints.minByOrNull { tentativeDistances[it]!! }!!
            unvisitedPoints.remove(currentPoint)

            if (currentPoint == destination) {
                break
            }

            getNeighbours(currentPoint)
                .filter { it in unvisitedPoints }
                .forEach { point ->
                    val altDistance = tentativeDistances[currentPoint]!! + this[point]
                    if (altDistance < tentativeDistances[point]!!) {
                        tentativeDistances[point] = altDistance
                        cheapestNeighbour[point] = currentPoint
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
}

internal typealias CumulativeRisk = Int
internal typealias Risk = Int
internal typealias Path = List<Pair<Point, CumulativeRisk>>