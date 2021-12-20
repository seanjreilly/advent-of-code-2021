package day19

import org.apache.commons.math.geometry.Vector3D
import utils.readInput

fun main() {
    val input = readInput("Day19")
    println(part1(input))
    println(part2(input))
}

fun part1(input: List<String>): Int {
    return input.size
}

fun part2(input: List<String>): Int {
    return input.size
}

internal fun parse(input: List<String>): List<Scanner> {
    val pointRegex = """,""".toRegex()
    val scanners = mutableListOf<Scanner>()
    var beacons = mutableListOf<Vector3D>()

    for (i in input.indices) {
        if (i == 0) { continue } //skip the first line
        val line = input[i]
        if (line.isBlank()) { continue }
        if (line.startsWith("---")) {
            //new scanner
            scanners.add(Scanner(beacons))
            beacons = mutableListOf()
            continue
        }
        val (x, y, z) = line.split(pointRegex).map { it.toInt() }
        beacons += Vector3D(x.toDouble(), y.toDouble(), z.toDouble())
    }
    scanners.add(Scanner(beacons))
    return scanners
}

internal data class Scanner(val beacons: List<Vector3D>) {
}