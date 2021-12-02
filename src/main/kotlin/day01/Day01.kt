package day01
import readInput

fun main() {
    val input = readInput("Day01")
    println(part1(input))
    println(part2(input))
}

fun part1(input: List<String>): Int {
    val ints = input.map(String::toInt)
    val increases = ints.windowed(2, 1).map { it[1] > it [0] }
    return increases.filter { it }.size
}

fun part2(input: List<String>): Int {
    val ints = input.map(String::toInt)
    val windowSums = ints.windowed(3, 1).map { it.sum() }
    val increases = windowSums.windowed(2, 1).map { it[1] > it [0] }
    return increases.filter { it }.size
}