package day10

import readInput

fun main() {
    val input = readInput("Day10")
    println(part1(input))
    println(part2(input))
}

fun part1(input: List<String>): Int {
    val scores = mapOf(
        ')' to 3,
        ']' to 57,
        '}' to 1197,
        '>' to 25137
    )

    return input
        .asSequence()
        .map { parseChunks(it) }
        .filterIsInstance<ParseResult.Corrupted>()
        .map { it.actual }
        .map {scores[it]!! }
        .sum()
}

fun part2(input: List<String>): Long {
    val completionScores = input
        .asSequence()
        .map { parseChunks(it) }
        .filterIsInstance<ParseResult.Incomplete>()
        .map { it.completionString }
        .map { calculateCompletionStringScore(it) }
        .sortedBy { it }
        .toList()
    return completionScores[completionScores.size / 2] //middle element (division rounds down)
}

internal sealed class ParseResult {

    /**
     * Represents a complete (uncorrupted) line
     *
     * A complete line is where every chunk is closed with the correct
     * closing character that corresponds to the character that opened it
     */
    object Complete : ParseResult()

    /**
     * Represents an incomplete line
     *
     * An incomplete line is a line that has open chunks that aren't closed,
     * but hasn't encountered an unexpected closing character
     */
    data class Incomplete(val completionString: String) : ParseResult()

    /**
     * Represents a corrupted line
     *
     * A corrupted line is a line that closes a chunk with a character that
     * isn't the one that opened the currently open chunk.
     */
    data class Corrupted(val expected: Char, val actual: Char) : ParseResult()
}

private val chunkDelimiters = mapOf(
    '(' to ')',
    '{' to '}',
    '[' to ']',
    '<' to '>'
)

internal fun parseChunks(line: String): ParseResult {
    val stack = mutableListOf<Char>()

    line.toCharArray().forEach { char ->
        if (char in chunkDelimiters.keys) {
            stack.add(char)
        } else {
            val expectedClosingCharacter = chunkDelimiters[stack.removeLast()]!!
            if (char != expectedClosingCharacter) {
                return ParseResult.Corrupted(expectedClosingCharacter, char)
            }
        }
    }

    if (stack.isEmpty()) {
        return ParseResult.Complete
    }

    //iterate in reverse order because we were pushing to the end of the list
    val completionString = stack.reversed().map { chunkDelimiters[it]!! }.joinToString("")
    return ParseResult.Incomplete(completionString)
}

private val closingDelimiterScores = mapOf(
    ')' to 1,
    ']' to 2,
    '}' to 3,
    '>' to 4,
)

internal fun calculateCompletionStringScore(completionString: String): Long {
    return completionString
        .toCharArray()
        .fold(0L) { acc, char -> (acc * 5) + closingDelimiterScores[char]!! }
}