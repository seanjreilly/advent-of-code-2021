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

internal sealed class SnailfishNumber {
    abstract fun traverse(depth: Int) : List<Pair<SnailfishNumber, Depth>>
}
internal data class RegularNumber(internal var number: Int) : SnailfishNumber() {
    /*
        Returns a number that should replace this one (if result is not null)
     */
    fun split(): PairNumber? = when {
        number <= 9 -> null
        else -> PairNumber(RegularNumber(number / 2) , RegularNumber(ceil(number / (2.toDouble())).toInt()))
    }

    fun magnitude() : Int = number

    override fun traverse(depth: Int): List<Pair<SnailfishNumber, Depth>> {
        return listOf(Pair(this, depth))
    }
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

    fun explode(): Boolean {
        val preorderTraversal = traverse(0) //build a list of node + depths
        for (i in preorderTraversal.indices) {
            //traverse until we find a pair nested 4 deep
            val (node, depth) = preorderTraversal[i]
            if (node !is PairNumber || depth < 4) {
                continue
            }

            //add node's left value to left neighbour (if any)
            val left = node.left as RegularNumber
            val toTheLeft = preorderTraversal
                .slice(0 until i)
                .map { it.first }
                .filterIsInstance<RegularNumber>()
                .lastOrNull()
            toTheLeft?.let { it.number += left.number }

            //add node's right value to right neighbour (if any)
            val right = node.right as RegularNumber
            val toTheRight = preorderTraversal
                .slice(i+3 until preorderTraversal.size) //start at i + 3 because we also need to skip this node's two RegularNumber children
                .map { it.first }
                .filterIsInstance<RegularNumber>()
                .firstOrNull()
            toTheRight?.let { it.number += right.number }

            //find parent and replace this node with RegularNumber(0)
            val replacement = RegularNumber(0)
            val parent = preorderTraversal
                .slice(0 until i)
                .last {(it.first is PairNumber) && (it.second == (depth - 1)) }
                .first as PairNumber

            if (parent.left == node) {
                parent.left = replacement
            } else {
                parent.right = replacement
            }

            return true
        }
        return false
    }

    override fun traverse(depth:Int): List<Pair<SnailfishNumber, Depth>> {
        return listOf(Pair(this, depth)) + this.left.traverse(depth + 1) + this.right.traverse(depth + 1)
    }
}

typealias Depth = Int