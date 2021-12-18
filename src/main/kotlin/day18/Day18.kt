package day18

import utils.readInput
import kotlin.math.ceil
import kotlin.reflect.KMutableProperty0

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
        fun splitOperation(field: KMutableProperty0<SnailfishNumber>) : Boolean {
            val fieldValue = field.get()
            if (fieldValue is RegularNumber) {
                val newValue = fieldValue.split()
                field.set(newValue ?: fieldValue)
                if (newValue != null) {
                    return true
                }
            } else {
                val result = (fieldValue as PairNumber).split()
                if (result) {
                    return true
                }
            }
            return false
        }

        return when {
            splitOperation(::left) -> true
            splitOperation(::right) -> true
            else -> false
        }
    }

    fun explode(): ExplosionResult? {
        return null
    }
}

/*
    vals are null if they have been handled
    total reference is null if no explosion happened
 */
internal data class ExplosionResult(val left: Int?, val right: Int?)