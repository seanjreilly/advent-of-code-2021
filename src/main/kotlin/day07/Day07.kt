package day07

import readInput
import kotlin.math.abs

fun main() {
    val input = readInput("Day07").first()
    println(part1(input))
    println(part2(input))
}

fun part1(input: String): Int {
    val startingHorizontalPositions = input.split(",").map(String::toInt)
    val costOfPositionChange = {proposedFinalPosition: Int, startingHorizontalPosition: Int  ->
        //cost is just 1 per position changes
        abs(proposedFinalPosition - startingHorizontalPosition)
    }
    return calculateMinFuelCost(startingHorizontalPositions, costOfPositionChange)
}

fun part2(input: String): Int {
    val startingHorizontalPositions = input.split(",").map(String::toInt)
    val costOfPositionChange = {proposedFinalPosition: Int, startingHorizontalPosition: Int  ->
        //cost is the sum of 0..positionChange
        val positionChange = abs(proposedFinalPosition - startingHorizontalPosition)
        (positionChange * (positionChange + 1)) / 2 //thank you, Mr Gauss!
    }
    return calculateMinFuelCost(startingHorizontalPositions, costOfPositionChange)
}

private fun calculateMinFuelCost(startingHorizontalPositions: List<Int>, costOfPositionChange: (Int, Int) -> Int): Int {
    //bangs are ok because the list is not empty
    val minHorizontalPosition = startingHorizontalPositions.minOrNull()!!
    val maxHorizontalPosition = startingHorizontalPositions.maxOrNull()!!
    val deDupedStartingHorizontalPositions = startingHorizontalPositions
        .groupingBy { it }
        .eachCount() //key is position, value is count

    val result = (minHorizontalPosition..maxHorizontalPosition).map { proposedFinalPosition ->
        deDupedStartingHorizontalPositions.map { (startingPosition, numberOfCrabs) ->
            costOfPositionChange(proposedFinalPosition, startingPosition) * numberOfCrabs
        }.sum()
    }.minOrNull()!!
    return result
}
