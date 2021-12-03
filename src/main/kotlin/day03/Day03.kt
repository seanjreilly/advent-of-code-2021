package day03

import readInput

fun main() {
    val input = readInput("Day03")
    println(part1(input))
    println(part2(input))
}

fun part1(input: List<String>): UInt {
    val numberOfSetBitsInEachPosition = countNumberOfSetBitsInEachPosition(input)
    val rawBinaryResult = convertBitCountsToRawBinaryResult(numberOfSetBitsInEachPosition, input.size)

    val gamma = rawBinaryResult.toUInt()
    //least common bit in each field is just the inverse of the binary result for gamma
    val epsilon = rawBinaryResult.invert().toUInt()

    return gamma * epsilon
}

typealias BitArray = String
fun BitArray.toUInt() = this.toUInt(2)
private fun BitArray.invert(): BitArray {
    //kotlin platform invert functions use two's complement which wrecks everything, so just use string replacement
    return this.replace("0", "X").replace("1", "0").replace("X", "1")
}

//the only possible values for a bit are 1 and 0, so we can compare 1s to the total amount instead of also counting 0s
private fun convertBitCountsToRawBinaryResult(numberOfSetBitsInEachPosition: IntArray, totalNumberOfRows: Int): BitArray {
    val rawBinaryResult = StringBuilder()
    numberOfSetBitsInEachPosition
        .map { if (it > (totalNumberOfRows / 2)) '1' else '0' } //unspecified condition in the question: we can have a tie if the numbers are equal with an even number of input rows!!!
        .forEach(rawBinaryResult::append)
    val rawBinaryResultString = rawBinaryResult.toString()
    return rawBinaryResultString
}

private fun countNumberOfSetBitsInEachPosition(input: List<BitArray>): IntArray {
    val oneBits = IntArray(input[0].length)
    input.flatMap { it.toCharArray().withIndex() }
        .filter { it.value == '1' }
        .map { it.index }
        .forEach {oneBits[it]++}
    return oneBits
}

fun part2(input: List<String>): Int {
    return input.size
}