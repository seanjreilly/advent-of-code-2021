package day19

import utils.readInput
import kotlin.math.absoluteValue

fun main() {
    val input = readInput("Day19")
    println(part1(input))
    println(part2(input))
}

fun part1(input: List<String>): Int {
    return parse(input).buildCompleteMap().beaconPositions.size
}

fun part2(input: List<String>): Int {
    val scannerPositions = parse(input).buildCompleteMap().scannerPositions
    val manhattanDistances = scannerPositions
        .flatMap { thisPosition -> scannerPositions
            .map { otherPosition -> thisPosition.manhattanDistance(otherPosition) }
        }
    return manhattanDistances.maxOrNull()!!
}

internal fun parse(input: List<String>): List<Scanner> {
    val pointRegex = """,""".toRegex()
    val scanners = mutableListOf<Scanner>()
    var beacons = mutableListOf<Point3D>()

    for (i in input.indices) {
        if (i == 0) { continue } //skip the first line
        val line = input[i]
        if (line.isBlank()) { continue }
        if (line.startsWith("---")) {
            //new scanner
            scanners.add(Scanner(scanners.size, beacons))
            beacons = mutableListOf()
            continue
        }
        val (x, y, z) = line.split(pointRegex).map { it.toInt() }
        beacons += Point3D(x, y, z)
    }
    scanners.add(Scanner(scanners.size, beacons))
    return scanners
}

internal data class Scanner(val id: Int, val beacons: List<Point3D>)

fun Collection<Point3D>.overlaps(otherBeacons: List<Point3D>): Pair<Point3D, List<Point3D>>? {
    return possible3DRotations
        .asSequence()
        .map { otherBeacons.map(it) }
        .mapNotNull { rotatedOtherBeacons ->
            //the potential offsets is every point in beacons minus every point in rotated other beacons
            //the number of times each potential offset exists is also be the number of common beacons for that offset
            val potentialOffsets =
                this
                    .flatMap { beacon -> rotatedOtherBeacons.map { beacon - it }}
                    .groupingBy { it }
                    .eachCount()

            val bestPotentialOffset = potentialOffsets.entries.find { it.value >= 12 }
                ?: return@mapNotNull null //there are not enough matches with the best potential offset to be an overlap

            Pair(bestPotentialOffset.key, rotatedOtherBeacons)
        }
        .map { (potentialOffset, rotatedOtherBeacons) ->
            val rotatedAndOffsetBeacons = rotatedOtherBeacons.map { it + potentialOffset }
            Pair(potentialOffset, rotatedAndOffsetBeacons)
        }
        .firstOrNull()
}

typealias RotationTransformation = (Point3D) -> Point3D

internal fun List<Scanner>.buildCompleteMap(): CompletedScannerMap {
    val beaconPositions: MutableSet<Point3D> = this.first().beacons.toMutableSet()
    val scannerPositions = mutableSetOf(Point3D(0,0,0)) //everything is relative to the position of scanner 0

    val unmergedBeacons = this.drop(1).toMutableList()
    while (unmergedBeacons.isNotEmpty()) {
        val iterator = unmergedBeacons.iterator()
        while (iterator.hasNext()) {
            val beaconsToMerge = iterator.next().beacons
            val operationResult = beaconPositions.overlaps(beaconsToMerge)
            if (operationResult != null) {
                val (scannerPosition, translatedBeacons) = operationResult
                scannerPositions += scannerPosition
                beaconPositions += translatedBeacons
                iterator.remove()
                break
            }
        }
    }

    return CompletedScannerMap(scannerPositions, beaconPositions)
}

data class CompletedScannerMap(val scannerPositions: Set<Point3D>, val beaconPositions: Set<Point3D>)

data class Point3D(val x: Int, val y:Int, val z:Int) {
    fun manhattanDistance(other: Point3D): Int {
        return (this.x - other.x).absoluteValue +
            (this.y - other.y).absoluteValue +
            (this.z - other.z).absoluteValue
    }

    operator fun plus(other:Point3D) : Point3D {
        return Point3D(this.x + other.x, this.y + other.y, this.z + other.z)
    }

    operator fun minus(other: Point3D) : Point3D {
        return Point3D(this.x - other.x, this.y - other.y, this.z - other.z)
    }
}

// since the rotations are right angle rotations, we can do them with coordinate substitution and negation
// using the parity rule (number of negations mod 2 = number of substitutions)
// https://math.stackexchange.com/a/2603691
val possible3DRotations = listOf<RotationTransformation>(
    { it }, //+x+y+z //identity
    { Point3D(it.x, it.y * -1, it.z * -1) }, //+x-y-z
    { Point3D(it.x * -1, it.y, it.z * -1) }, //-x+y-z
    { Point3D(it.x * -1, it.y * -1, it.z) }, //-x-y+z

    { Point3D(it.x, it.z, it.y * -1) }, //+x+z-y
    { Point3D(it.x, it.z * -1, it.y) }, //+x-z+y
    { Point3D(it.x * -1, it.z, it.y) }, //-x+z+y
    { Point3D(it.x * -1, it.z * -1, it.y * -1) }, //-x-z-y

    { Point3D(it.y, it.x, it.z * -1) }, //+y+x-z
    { Point3D(it.y, it.x * -1, it.z) }, //+y-x+z
    { Point3D(it.y * -1, it.x, it.z) }, //-y+x+z
    { Point3D(it.y * -1, it.x * -1, it.z * -1) }, //-y-x-z

    { Point3D(it.y, it.z, it.x) }, //+y+z+x
    { Point3D(it.y, it.z * -1, it.x * -1) }, //+y-z-x
    { Point3D(it.y * -1, it.z, it.x * -1) }, //-y+z-x
    { Point3D(it.y * -1, it.z * -1, it.x) }, //-y-z+x

    { Point3D(it.z, it.x, it.y) }, //+z+x+y
    { Point3D(it.z, it.x * -1, it.y * -1) }, //+z-x-y
    { Point3D(it.z * -1, it.x, it.y * -1) }, //-z+x-y
    { Point3D(it.z * -1, it.x * -1, it.y) }, //-z-x+y

    { Point3D(it.z, it.y, it.x * -1) }, //+z+y-x
    { Point3D(it.z, it.y * -1, it.x) }, //+z-y+x
    { Point3D(it.z * -1, it.y, it.x) }, //-z+y+x
    { Point3D(it.z * -1, it.y * -1, it.x * -1) }, //-z-y-x
)