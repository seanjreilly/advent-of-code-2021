package day13

import utils.gridmap.Point
import utils.readInput

fun main() {
    val input = readInput("Day13")
    println(part1(input))
    println(part2(input))
}

fun part1(input: List<String>): Int {
    val map = SparseBooleanMap.parse(input)
    val foldInstruction = FoldInstruction.parse(input).first()
    return map.performFold(foldInstruction).size
}

fun part2(input: List<String>): String {
    var map = SparseBooleanMap.parse(input)
    FoldInstruction.parse(input).forEach { fold -> map = map.performFold(fold) }
    return map.print()
}

internal class SparseBooleanMap(private val setPoints: Set<Point>) : Iterable<Point> {
    val width: Int = setPoints.maxOf { it.x } + 1
    val height: Int = setPoints.maxOf { it.y } + 1

    operator fun get(point: Point) = setPoints.contains(point)

    override fun iterator() = iterator {
        for (x in 0 until width) {
            for (y in 0 until height) {
                yield(Point(x, y))
            }
        }
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false
        if (setPoints != (other as SparseBooleanMap).setPoints) return false

        return true
    }

    override fun hashCode(): Int {
        return setPoints.hashCode()
    }

    val size = setPoints.size

    fun performFold(fold: FoldInstruction): SparseBooleanMap {
        val transform  = { point:Point ->
            if (fold.foldAlong == FoldAxis.y) {
                if (point.y < fold.foldValue) {
                    point
                } else {
                    Point(point.x, fold.foldValue - (point.y - fold.foldValue))
                }
            } else {
                if (point.x < fold.foldValue) {
                    point
                } else {
                    Point(fold.foldValue - (point.x - fold.foldValue), point.y)
                }
            }
        }

        val newSetPoints = setPoints
            .map(transform)
            .toSet()

        return SparseBooleanMap(newSetPoints)
    }

    fun print(): String {
        return (0 until height).map { y ->
            (0 until width).map { x ->
                if (this[Point(x,y)]) { '#' } else { '.'}
            }.joinToString("")
        }.joinToString ("\n")
    }

    companion object {
        fun parse(inputPoints: List<String>): SparseBooleanMap {
            val points = inputPoints
                .takeUntil { it.isBlank() }
                .map { it.split(',').map(String::toInt) }
                .map { (x,y) -> Point(x, y) }
                .toSet()

            return SparseBooleanMap(points)
        }
    }
}

@Suppress("EnumEntryName") //enums need to match the case in the input & input file
internal enum class FoldAxis {
    x,
    y
}

internal data class FoldInstruction(val foldAlong: FoldAxis, val foldValue: Int) {
    companion object {
        fun parse(input: List<String>) : List<FoldInstruction> {
            return input
                .dropUntil { it.isBlank() }
                .asSequence()
                .drop(1)
                .map { it.split('=') }
                .map { Pair( it[0].takeLast(1), it[1].toInt())}
                .map { Pair(FoldAxis.valueOf(it.first), it.second) }
                .map { FoldInstruction(it.first, it.second) }
                .toList()
        }
    }
}

/**
 * Drops elements from the collection until the first element
 * that satisfies a predicate, and then returns all remaining elements, whether they satisfy the predicate
 * or not
 */
internal fun <T> List<T>.dropUntil(predicate: (T) -> Boolean): List<T> {
    val firstIndex = this.indexOfFirst(predicate)
    return if (firstIndex > 0) { this.subList(firstIndex, this.size) } else { this }
}

/**
 * Allows elements from the collection up to but not including the first element
 * that satisfies a predicate. All remaining elements are suppressed whether they satisfy the predicate
 * or not
 */
fun <T> List<T>.takeUntil(predicate: (T) -> Boolean): List<T> {
    val firstIndex = this.indexOfFirst(predicate)
    return if (firstIndex > 0) { this.subList(0, firstIndex) } else { this }
}