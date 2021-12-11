package day03

import utils.readInput
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
    val oxygenGeneratorRating = filterOnMostCommonValueInEachPosition(input, true)
    val co2ScrubberRating = filterOnMostCommonValueInEachPosition(input, false)
    return oxygenGeneratorRating * co2ScrubberRating
}

/*
    Each round consider the nth position in each bit array and either keep or reject
    the entries with the most common bit value.
    Keep iterating until there is only one value left in the list, and then return that
 */
private fun filterOnMostCommonValueInEachPosition(input: List<String>, keepMostCommon: Boolean): UInt {

    fun predicate(valueInBitArray:Char, mostCommonValue: Char) : Boolean {
        if (keepMostCommon) {
            return valueInBitArray != mostCommonValue
        }
        return valueInBitArray != mostCommonValue.not()
    }

    val potentialRatings: MutableList<BitArray> = input.toMutableList()
    var round = 0
    while (potentialRatings.size > 1) {
        val numberOfSetBitsInEachPosition = countNumberOfSetBitsInEachPosition(potentialRatings)
        val rawBinaryResult = convertBitCountsToRawBinaryResult(numberOfSetBitsInEachPosition, potentialRatings.size)
        val filterValue = rawBinaryResult[round]
        potentialRatings.removeAll { predicate(it[round], filterValue) }
        round++
    }
    return potentialRatings.first().toUInt()
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
