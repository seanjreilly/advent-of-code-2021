package day08

import readInput

fun main() {
    val input = readInput("Day08")
    println(part1(input))
    println(part2(input))
}

fun part1(input: List<String>): Int {
    return input.map {
        it.split('|')
            .last()
            .split(" ")
            .map { it.trim() }
            .filter { it.isNotEmpty() }
        } //each item is a list of non-space characters. each one corresponds to a digit
        .map {
            //one, seven, four, or eight
            it.count { it.length in setOf(2, 3, 4, 7) }
        }
        .sum()
}

fun part2(input: List<String>): Int {
    return input.size
}
