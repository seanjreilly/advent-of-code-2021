package day04

import utils.readInput

fun main() {
    val input = readInput("Day04")
    println(part1(input))
    println(part2(input))
}

fun part1(input: List<String>): Int {
    val drawOrder = parseDrawOrder(input)
    val bingoBoards = parseBingoBoards(input)

    drawOrder.forEach { number ->
        bingoBoards.forEach { board ->
            val score = board.markNumber(number)
            if (score != null) {
                return score
            }
        }
    }
    throw IllegalStateException("no board won!")
}

fun part2(input: List<String>): Int {
    val drawOrder = parseDrawOrder(input)
    val bingoBoards = parseBingoBoards(input).toMutableList()

    drawOrder.forEach { number ->
        //use a mutable iterator so we can remove boards as we're looping over the boards
        val mutableIterator = bingoBoards.iterator()
        mutableIterator.forEach { board ->
            val score = board.markNumber(number)
            if (score != null) {
                if (bingoBoards.size == 1) {
                    return score
                }
                mutableIterator.remove()
            }
        }
    }
    throw IllegalStateException("no board won!")
}

private fun parseBingoBoards(input: List<String>) = input
    .drop(1) //skip the draw order
    .windowed(6, 6).map { it.drop(1) } //skip the blank line at the beginning of each board definition
    .map(::BingoBoard)

private fun parseDrawOrder(input: List<String>) =
    input.first().trim().split(",").map { it.trim().toInt() }

internal class BingoBoard(input: List<String>) {
    companion object {
        val WHITESPACE_REGEX = """\s+""".toRegex()
    }

    private val rows : Array<MutableSet<Int>>
    private val columns : Array<MutableSet<Int>>
    private val remainingNumbers : MutableSet<Int>

    init {
        val parsedInput = input.map { it.trim().split(WHITESPACE_REGEX).map(String::toInt) } //5x5 list of integers
        rows = parsedInput.map { it.toMutableSet() }.toTypedArray()
        columns = (0 until parsedInput.first().size)
            .map { index -> parsedInput.map { it[index] } } //slice columns
            .map { it.toMutableSet()}.toTypedArray()
        remainingNumbers = parsedInput.flatten().toMutableSet()
    }

    fun markNumber(number: Int): Int? {
        //TODO: blow up if this board is already a winner

        rows.forEach { it.remove(number) }
        columns.forEach { it.remove(number) }
        remainingNumbers.remove(number)

        if (rows.none { it.isEmpty() } && columns.none { it.isEmpty() }) {
            return null //no winners yet
        }
        return remainingNumbers.sum() * number
    }
}