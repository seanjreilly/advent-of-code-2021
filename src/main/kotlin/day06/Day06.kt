package day06

import utils.readInput

fun main() {
    val input = readInput("Day06").single()
    println(part1(input))
    println(part2(input))
}

fun part1(input: String): Long {
    return countFish(parseInitialFishCount(input), 80)
}

fun part2(input: String): Long {
    return countFish(parseInitialFishCount(input), 256)
}

private fun countFish(count: FishCount, generationsRemaining: Int): Long {
    if (generationsRemaining == 0) {
        return totalFish(count)
    }
    return countFish(nextGeneration(count), generationsRemaining - 1)
}

internal fun nextGeneration(fishCount: FishCount): FishCount {
    val nextCounts = fishCount.counts.drop(1).toMutableList() // creates a copy shifted left one
    nextCounts.add(fishCount.counts[0]) //new fish created with timer 8
    nextCounts[6] += fishCount.counts[0] //existing fish move to timer 6
    return FishCount(nextCounts)
}

internal fun parseInitialFishCount(input: String): FishCount {
    val initialCounts: Map<Int, Int> = input
        .split(',')
        .map(String::toInt)
        //count the number of fish with each timer state
        .groupingBy { it }.eachCount()
        .withDefault { 0 }

    return FishCount((0..8).map { initialCounts[it] ?: 0 }.map(Int::toLong).toList()) //there are zero fish with any timer state not mentioned in the input
}

internal fun totalFish(fishCount: FishCount) = fishCount.counts.sum()

class FishCount(internal val counts: List<Long>) {
    init {
        assert(counts.size == 9)
    }
}