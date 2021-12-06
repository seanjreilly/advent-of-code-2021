package day06

import readInput

fun main() {
    val input = readInput("Day06").single()
    println(part1(input))
    println(part2(input))
}

fun part1(input: String): Int {
    var count = parseFishCount(input)
    (1..80).forEach { _ -> count = count.nextDay() }
    return count.totalFish()
}

fun part2(input: String): Int {
    return input.length
}

internal fun parseFishCount(input: String): FishCount {
    val initialCounts: Map<Int, Int> = input
        .split(',')
        .map(String::toInt)
        //count the number of fish with each timer state
        .groupingBy { it }.eachCount()
        .withDefault { 0 }

    return FishCount((0..8).map { initialCounts[it] ?: 0 }.toList()) //there are zero fish with any timer state not mentioned in the input
}

class FishCount(internal val counts: List<Int>) {
    internal constructor(zero: Int, one: Int, two: Int, three: Int, four: Int, five: Int, six: Int, seven: Int, eight: Int) : this(
        listOf(zero, one, two, three, four, five, six, seven, eight)
    )

    fun nextDay(): FishCount {
        val nextCounts = counts.drop(1).toMutableList() // creates a copy shifted left one
        nextCounts.add(counts[0]) //new fish created with timer 8
        nextCounts[6] += counts[0] //existing fish move to timer 6
        return FishCount(nextCounts)
    }

    fun totalFish(): Int = counts.sum()
}