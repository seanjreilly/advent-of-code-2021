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

fun part2(input: List<String>): Int {
    return input
        .map(::parseEncodedLine)
        .map(::decodeOutput)
        .sum()
}

internal typealias EncodedDigit = Set<Char>
internal data class EncodedLine(val inputValues: List<EncodedDigit>, val outputValues: List<EncodedDigit>)

internal typealias DigitDecoder = Map<EncodedDigit, String>

internal fun parseEncodedLine(rawLine: String): EncodedLine {
    val (rawInput, rawOutput) = rawLine.split('|').map { it.trim() }.filter { it.isNotEmpty() }
    val parseListOfEncodedDigits = { it:String ->
        it.split(" ")
            .map { it.trim() }
            .filter { it.isNotEmpty() }
            .map { it.toCharArray().toSet() }
    }
    return EncodedLine(parseListOfEncodedDigits(rawInput), parseListOfEncodedDigits(rawOutput))
}

internal fun generateDecoder(inputValues: List<EncodedDigit>): DigitDecoder {
    val one = inputValues.filter { it.size == 2 }.single()
    val four = inputValues.filter { it.size == 4 }.single()
    val seven = inputValues.filter { it.size == 3 }.single()
    val eight = inputValues.filter { it.size == 7 }.single()

    val charsInAllSixCharSets = inputValues
        .filter { it.size == 6 }
        .reduce(EncodedDigit::intersect)

    val charsInAllFiveCharSets = inputValues
        .filter { it.size == 5 }
        .reduce(EncodedDigit::intersect)

    val bAndF = charsInAllSixCharSets.filter { !charsInAllFiveCharSets.contains(it) }.toSet()
    val d = charsInAllFiveCharSets.filter { !charsInAllSixCharSets.contains(it) }.single()

    val five = inputValues.filter { it.size == 5 && it.containsAll(bAndF) }.single()
    val zero = inputValues.filter { it.size == 6 && !it.contains(d) }.single()
    val three = inputValues.filter { it.size == 5 && it.containsAll(one) }.single()
    val two = inputValues.filter { it.size == 5 && it != three && it != five }.single()
    val nine = inputValues.filter { it.size == 6 && it != zero && it.containsAll(one) }.single()
    val six = inputValues.filter { it.size == 6 && it != zero && it != nine }.single()

    return mapOf(
        zero to "0",
        one to "1",
        two to "2",
        three to "3",
        four to "4",
        five to "5",
        six to "6",
        seven to "7",
        eight to "8",
        nine to "9"
    )
}

internal fun decodeOutput(encodedLine: EncodedLine) : Int {
    val decoder = generateDecoder(encodedLine.inputValues)
    return encodedLine.outputValues
        .map { decoder[it]!! }
        .joinToString("")
        .toInt()
}