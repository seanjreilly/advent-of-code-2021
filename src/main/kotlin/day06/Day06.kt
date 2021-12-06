package day06

import readInput

fun main() {
    val input = readInput("Day06").single()
    println(part1(input))
    println(part2(input))
}

fun part1(input: String): Long {
    var count = parseFishCount(input)
    (1..80).forEach { _ -> count = count.nextDay() }
    return count.totalFish()
}

fun part2(input: String): Long {
    var count = parseFishCount(input)
    (1..256).forEach { _ -> count = count.nextDay() }
    return count.totalFish()
}

internal fun parseFishCount(input: String): FishCount {
    val initialCounts: Map<Int, Int> = input
        .split(',')
        .map(String::toInt)
        //count the number of fish with each timer state
        .groupingBy { it }.eachCount()
        .withDefault { 0 }

    return FishCount((0..8).map { initialCounts[it] ?: 0 }.map(Int::toLong).toList()) //there are zero fish with any timer state not mentioned in the input
}

class FishCount(internal val counts: List<Long>) {
    internal constructor(zero: Int, one: Int, two: Int, three: Int, four: Int, five: Int, six: Int, seven: Int, eight: Int) : this(
        listOf(zero.toLong(), one.toLong(), two.toLong(), three.toLong(), four.toLong(), five.toLong(), six.toLong(), seven.toLong(), eight.toLong())
    )

    fun nextDay(): FishCount {
        val nextCounts = counts.drop(1).toMutableList() // creates a copy shifted left one
        nextCounts.add(counts[0]) //new fish created with timer 8
        nextCounts[6] += counts[0] //existing fish move to timer 6
        return FishCount(nextCounts)
    }

    fun totalFish(): Long = counts.sum()
}