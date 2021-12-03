package day03

import readInput
import kotlin.math.roundToInt

fun main() {
    val input = readInput("Day03")
    println(part1(input))
    println(part2(input))
}

fun part1(input: List<String>): UInt {
    val numberOfSetBitsInEachPosition = countNumberOfSetBitsInEachPosition(input)
    val rawBinaryResult = convertBitCountsToRawBinaryResult(numberOfSetBitsInEachPosition, input.size)

    val gamma = rawBinaryResult.toUInt()
    val epsilon = rawBinaryResult.invert().toUInt() //least common bit in each field is just the binary inverse of gamma

    return gamma * epsilon
}

fun part2(input: List<String>): UInt {
    //find oxygen generator rating
    val potentialOxygenGeneratorRatings: MutableList<BitArray> = input.toMutableList()
    var round = 0
    while (potentialOxygenGeneratorRatings.size > 1) {
        val numberOfSetBitsInEachPosition = countNumberOfSetBitsInEachPosition(potentialOxygenGeneratorRatings)
        val rawBinaryResult = convertBitCountsToRawBinaryResult(numberOfSetBitsInEachPosition, potentialOxygenGeneratorRatings.size)
        val filterValue = rawBinaryResult[round]
        potentialOxygenGeneratorRatings.removeAll { it[round] != filterValue }
        round++
    }
    val oxygenGeneratorRating = potentialOxygenGeneratorRatings.first().toUInt()

    //find CO2 scrubber rating
    val potentialCO2ScrubberRatings: MutableList<BitArray> = input.toMutableList()
    round = 0
    while (potentialCO2ScrubberRatings.size > 1) {
        val numberOfSetBitsInEachPosition = countNumberOfSetBitsInEachPosition(potentialCO2ScrubberRatings)
        val rawBinaryResult = convertBitCountsToRawBinaryResult(numberOfSetBitsInEachPosition, potentialCO2ScrubberRatings.size)
        val filterValue = rawBinaryResult[round].not()
        potentialCO2ScrubberRatings.removeAll { it[round] != filterValue }
        round++
    }
    val co2ScrubberRating = potentialCO2ScrubberRatings.first().toUInt()

    return oxygenGeneratorRating * co2ScrubberRating
}

typealias BitArray = String
private fun BitArray.toUInt() = this.toUInt(2)
private fun BitArray.invert(): BitArray {
    //kotlin platform invert functions use two's complement which wrecks everything, so just use string replacement
    return this
        .replace('0', 'X')
        .replace('1', '0')
        .replace('X', '1')
}
//"negate" a Char that is masquerading as a bit
private fun Char.not(): Char {
    return when (this) {
        '0' -> '1'
        '1' -> '0'
        else -> throw IllegalArgumentException("expected a 1 or 0")
    }
}

//the only possible values for a bit are 1 and 0, so we can compare 1s to the total amount instead of also counting 0s
private fun convertBitCountsToRawBinaryResult(numberOfSetBitsInEachPosition: IntArray, totalNumberOfRows: Int): BitArray {
    val threshold: Int = (totalNumberOfRows.toDouble() / 2).roundToInt() //round up doing the division, not down
    /*
    Behaviour with ties is now specified: ties go towards 1
    In the case where we want to calculate the least common value and use 0 for a tie,
    this is actually the same as finding the most common value using 1 for a tie, and negating.
     */
    return numberOfSetBitsInEachPosition
        .map { if (it >= threshold) '1' else '0' }
        .joinToString("")
}

private fun countNumberOfSetBitsInEachPosition(input: List<BitArray>): IntArray {
    val oneBits = IntArray(input[0].length)
    input.flatMap { it.toCharArray().withIndex() }
        .filter { it.value == '1' }
        .map { it.index }
        .forEach {oneBits[it]++}
    return oneBits
}
