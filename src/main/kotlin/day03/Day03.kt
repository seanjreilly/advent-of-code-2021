package day03

import readInput

fun main() {
    val input = readInput("Day03")
    println(part1(input))
    println(part2(input))
}

fun part1(input: List<String>): UInt {
    val numberOfSetBitsInEachPosition = countNumberOfSetBitsInEachPosition(input)

    val rawBinaryResult = StringBuilder()
    //the only possible values for a bit are 1 and 0, so we can compare 1s to the total amount instead of also counting 0s
    numberOfSetBitsInEachPosition
        .map { if (it > (input.size / 2)) '1' else '0' } //unspecified condition in the question: we can have a tie if the numbers are equal with an even number of input rows!!!
        .forEach(rawBinaryResult::append)

    val gamma = rawBinaryResult.toString().toUInt(2)
    val epsilon = invertRawBinaryResult(rawBinaryResult).toUInt(2) //least common bit in each field is just the inverse of the binary result for gamma

    return gamma * epsilon
}

private fun invertRawBinaryResult(rawBinaryResult: StringBuilder): String {
    //kotlin platform invert functions use two's complement which wrecks everything, so just use string replacement
    return rawBinaryResult.toString().replace("0", "X").replace("1", "0").replace("X", "1")
}

private fun countNumberOfSetBitsInEachPosition(input: List<String>): IntArray {
    val oneBits = IntArray(input[0].length)
    input.forEach { line ->
        for (i in line.toCharArray().indices) {
            if (line[i] == '1') {
                oneBits[i]++
            }
        }
    }
    return oneBits
}

fun part2(input: List<String>): Int {
    return input.size
}