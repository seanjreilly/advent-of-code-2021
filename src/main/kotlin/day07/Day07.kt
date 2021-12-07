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
    val costOfPositionChange = {proposedFinalPosition: Int, startingHorizontalPosition: Int  -> abs(proposedFinalPosition - startingHorizontalPosition) }

    //bangs are ok because the list is not empty
    val minHorizontalPosition = startingHorizontalPositions.minOrNull()!!
    val maxHorizontalPosition = startingHorizontalPositions.maxOrNull()!!
    val deDupedStartingHorizontalPositions = startingHorizontalPositions.groupingBy { it }.eachCount() //key is position, value is count


    val result = (minHorizontalPosition..maxHorizontalPosition).map { proposedFinalPosition ->
        deDupedStartingHorizontalPositions.map { (startingPosition, numberOfCrabs) ->
            costOfPositionChange(proposedFinalPosition, startingPosition) * numberOfCrabs
        }.sum()
    }.minOrNull()!!
    return result
}

fun part2(input: String): Int {
    return input.length
}