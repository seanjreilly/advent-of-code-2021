package day08

import readInput

fun main() {
    val input = readInput("Day08")
    println(part1(input))
    println(part2(input))
}

fun part1(input: List<String>): Int {
    return input
        .map(::parseEncodedLine)
        .map {
            //one, seven, four, or eight
            it.outputValues.count() { it.size in setOf(2, 3, 4, 7) }
        }
        .sum()
}

typealias EncodedDigit = Set<Char>
data class EncodedLine(val inputValues: List<EncodedDigit>, val outputValues: List<EncodedDigit>)

fun parseEncodedLine(rawLine: String): EncodedLine {
    val (rawInput, rawOutput) = rawLine.split('|').map { it.trim() }.filter { it.isNotEmpty() }
    val parseListOfEncodedDigits = { it:String ->
        it.split(" ")
            .map { it.trim() }
            .filter { it.isNotEmpty() }
            .map { it.toCharArray().toSet() }
    }
    return EncodedLine(parseListOfEncodedDigits(rawInput), parseListOfEncodedDigits(rawOutput))
}

fun part2(input: List<String>): Int {
    return input.size
}
