package day03

import readInput

fun main() {
    val input = readInput("Day03")
    println(part1(input))
    println(part2(input))
}

fun part1(input: List<String>): UInt {
    val oneBits = IntArray(input[0].length)
    input.forEach { line ->
        for (i in line.toCharArray().indices) {
            if (line[i] == '1') {
                oneBits[i]++
            }
        }
    }
    val rawBinaryResult = StringBuilder()
    oneBits
        .map { if (it > (input.size/2)) '1' else '0' }
        .forEach (rawBinaryResult::append)

    //Let's use UInt because we're dealing with unsigned binary numbers
    val gamma = rawBinaryResult.toString().toUInt(2)

    //kotlin platform invert functions use two's complement which wrecks everything
    val invertedRawBinaryResult = rawBinaryResult.toString().replace("0", "X").replace("1", "0").replace("X", "1")

    val epsilon = invertedRawBinaryResult.toUInt(2)

    return gamma * epsilon
}

fun part2(input: List<String>): Int {
    return input.size
}