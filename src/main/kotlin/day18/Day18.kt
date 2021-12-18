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

internal data class PairNumber(internal var left: SnailfishNumber, internal var right: SnailfishNumber) : SnailfishNumber() {
    fun split(): Boolean {
        if (left is RegularNumber) {
            val newLeft = (left as RegularNumber).split()
            left = newLeft ?: left
            if (newLeft != null) {
                return true
            }
        } else {
            val result = (left as PairNumber).split()
            if (result) {
                return true
            }
        }
        if (right is RegularNumber) {
            val newRight = (right as RegularNumber).split()
            right = newRight ?: right
            if (newRight != null) {
                return true
            }
        } else {
            val result = (right as PairNumber).split()
            if (result) {
                return true
            }
        }
        return false
    }
}