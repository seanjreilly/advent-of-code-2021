package day20

import utils.gridmap.Point
import utils.readInput

fun main() {
    val input = readInput("Day20")
    println(part1(input))
    println(part2(input))
}

fun part1(input: List<String>): Int {
    val enhancementAlgorithm = parseEnhancementAlgorithm(input)
    val result = Image(input).enhance(enhancementAlgorithm).enhance(enhancementAlgorithm)
    return result.countLitPixels()
}

fun part2(input: List<String>): Int {
    val enhancementAlgorithm = parseEnhancementAlgorithm(input)
    var image = Image(input)
    (1..50).forEach { image = image.enhance(enhancementAlgorithm) }
    return image.countLitPixels()
}

internal fun parseEnhancementAlgorithm(input: List<String>): BooleanArray {
    return input.first().toCharArray().map { it.toBinary() }.toBooleanArray()
}

internal data class Image(private val setPoints: Set<Point>, val width:Int, val height: Int, val backgroundColor: Boolean = false) : Iterable<Point> {

    internal operator fun get(point: Point): Boolean = point in setPoints

    fun countLitPixels() : Int {
        if (backgroundColor) {
            throw IllegalStateException("infinite number of set pixels")
        }
        return setPoints.size
    }

    override fun iterator() = iterator {
        for (x in 0 until width) {
            for (y in 0 until height) {
                yield(Point(x, y))
            }
        }
    }

    fun enhance(enhancementAlgorithm: BooleanArray): Image {
        val newSetPoints = (-1 .. width).flatMap { x ->
            (-1 .. height).mapNotNull { y ->
                val point = Point(x, y)
                val grid = point.threeByThreeSquare()
                val codeForGrid = grid.map {
                    when {
                        isOutOfBounds(it) -> backgroundColor
                        else -> (it in setPoints)
                    }
                }
                .map { it.toBinary() }
                .joinToString("")
                .toInt(2)

                val enhancedResult = enhancementAlgorithm[codeForGrid]
                if (enhancedResult) point else null
            }
            //shift the negative -1 row and -1 column to the new 0 row and 0 column
            .map { Point(it.x + 1,it.y + 1) }
        }.toSet()
        val newWidth = width + 2
        val newHeight = height + 2
        val newBackgroundColor = if (backgroundColor) enhancementAlgorithm["111111111".toInt(2)] else enhancementAlgorithm[0]
        return Image(newSetPoints, newWidth, newHeight, newBackgroundColor)
    }

    private fun isOutOfBounds(point: Point) : Boolean {
        return (point.x !in (0 until width)) || (point.y !in (0 until height))
    }
}

internal fun Image(input: List<String>) : Image {
    val imagePortionOfInput = input.drop(2)

    val width = imagePortionOfInput.first().length
    val height = imagePortionOfInput.size

    val setPoints = imagePortionOfInput
        .flatMapIndexed { y, line ->
            check(line.length == width) { "image must be rectangular" }
            line.toCharArray().mapIndexed { x, char ->
                val point = Point(x, y)
                Pair(char.toBinary(), point)
            }
        }
        .mapNotNull { if (it.first) it.second else null }
        .toSet()

    return Image(setPoints, width, height, false)
}

private fun Char.toBinary() = when (this) {
    '#' -> true
    '.' -> false
    else -> throw IllegalArgumentException("unexpected character '$this'")
}

private fun Boolean.toBinary() = when(this) {
    true -> '1'
    false -> '0'
}

private fun Point.threeByThreeSquare() : List<Point> {
    return listOf(
        this.northWest(), this.north(), this.northEast(),
        this.west(), this, this.east(),
        this.southWest(), this.south(), this.southEast()
    )
}