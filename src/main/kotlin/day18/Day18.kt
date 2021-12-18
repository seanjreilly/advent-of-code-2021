package day18

import utils.readInput
import kotlin.math.ceil

fun main() {
    val input = readInput("Day18")
    println(part1(input))
    println(part2(input))
}

fun part1(input: List<String>): Int {
    return input.size
}

fun part2(input: List<String>): Int {
    return input.size
}

internal sealed class SnailfishNumber {}
internal data class RegularNumber(private val number: Int) : SnailfishNumber() {
    /*
        Returns a number that should replace this one (if result is not null)
     */
    fun split(): PairNumber? = when {
        number <= 9 -> null
        else -> PairNumber(RegularNumber(number / 2) , RegularNumber(ceil(number / (2.toDouble())).toInt()))
    }

    fun magnitude() : Int = number
}

internal data class PairNumber(internal val left: SnailfishNumber, internal val right: SnailfishNumber) : SnailfishNumber() {}