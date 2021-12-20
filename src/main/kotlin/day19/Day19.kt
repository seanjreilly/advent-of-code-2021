package day19

import org.apache.commons.math.geometry.Vector3D
import utils.readInput
import kotlin.math.absoluteValue
import kotlin.math.ceil

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
    var beacons = mutableListOf<Vector3D>()

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
        beacons += Vector3D(x.toDouble(), y.toDouble(), z.toDouble())
    }
    scanners.add(Scanner(scanners.size, beacons))
    return scanners
}

internal data class Scanner(val id: Int, val beacons: List<Vector3D>)

fun Collection<Vector3D>.overlaps(otherBeacons: List<Vector3D>): Pair<Vector3D, List<Vector3D>>? {
    return possible3DRotations
        .asSequence()
        .map { otherBeacons.map(it) }
        .mapNotNull { rotatedOtherBeacons ->
            //the potential offsets is every point in beacons minus every point in rotated other beacons
            //the number of times each potential offset exists is also be the number of common beacons for that offset
            val potentialOffsets =
                this
                    .flatMap { beacon -> rotatedOtherBeacons.map { beacon.subtract(it) }}
                    .groupingBy { it }
                    .eachCount()

            potentialOffsets.keys.forEach {
                assert(it.x == ceil(it.x))
                assert(it.y == ceil(it.y))
                assert(it.z == ceil(it.z))
            }

            val bestPotentialOffset = potentialOffsets.entries.find { it.value >= 12 }
                ?: return@mapNotNull null //there are not enough matches with the best potential offset to be an overlap

            Pair(bestPotentialOffset.key, rotatedOtherBeacons)
        }
        .map { (potentialOffset, rotatedOtherBeacons) ->
            val rotatedAndOffsetBeacons = rotatedOtherBeacons.map { it.add(potentialOffset) }
            Pair(potentialOffset, rotatedAndOffsetBeacons)
        }
        .firstOrNull()
}

typealias RotationTransformation = (Vector3D) -> Vector3D

internal fun List<Scanner>.buildCompleteMap(): CompletedScannerMap {
    val beaconPositions: MutableSet<Vector3D> = this.first().beacons.toMutableSet()
    val scannerPositions = mutableSetOf(Vector3D(0.0,0.0,0.0)) //everything is relative to the position of scanner 0

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

data class CompletedScannerMap(val scannerPositions: Set<Vector3D>, val beaconPositions: Set<Vector3D>)

internal fun Vector3D.manhattanDistance(other: Vector3D): Int {
    return (this.x - other.x).toInt().absoluteValue +
        (this.y - other.y).toInt().absoluteValue +
        (this.z - other.z).toInt().absoluteValue
}

// since the rotations are right angle rotations, we can do them with coordinate substitution and negation
// using the parity rule (number of negations mod 2 = number of substitutions)
// https://math.stackexchange.com/a/2603691
val possible3DRotations = listOf<RotationTransformation>(
    { it }, //+x+y+z //identity
    { Vector3D(it.x, it.y * -1.0, it.z * -1.0) }, //+x-y-z
    { Vector3D(it.x * -1.0, it.y, it.z * -1.0) }, //-x+y-z
    { Vector3D(it.x * -1.0, it.y * -1.0, it.z) }, //-x-y+z

    { Vector3D(it.x, it.z, it.y * -1.0) }, //+x+z-y
    { Vector3D(it.x, it.z * -1.0, it.y) }, //+x-z+y
    { Vector3D(it.x * -1.0, it.z, it.y) }, //-x+z+y
    { Vector3D(it.x * -1.0, it.z * -1.0, it.y * -1.0) }, //-x-z-y

    { Vector3D(it.y, it.x, it.z * -1.0) }, //+y+x-z
    { Vector3D(it.y, it.x * -1.0, it.z) }, //+y-x+z
    { Vector3D(it.y * -1.0, it.x, it.z) }, //-y+x+z
    { Vector3D(it.y * -1.0, it.x * -1.0, it.z * -1.0) }, //-y-x-z

    { Vector3D(it.y, it.z, it.x) }, //+y+z+x
    { Vector3D(it.y, it.z * -1.0, it.x * -1.0) }, //+y-z-x
    { Vector3D(it.y * -1.0, it.z, it.x * -1.0) }, //-y+z-x
    { Vector3D(it.y * -1.0, it.z * -1.0, it.x) }, //-y-z+x

    { Vector3D(it.z, it.x, it.y) }, //+z+x+y
    { Vector3D(it.z, it.x * -1.0, it.y * -1.0) }, //+z-x-y
    { Vector3D(it.z * -1.0, it.x, it.y * -1.0) }, //-z+x-y
    { Vector3D(it.z * -1.0, it.x * -1.0, it.y) }, //-z-x+y

    { Vector3D(it.z, it.y, it.x * -1.0) }, //+z+y-x
    { Vector3D(it.z, it.y * -1.0, it.x) }, //+z-y+x
    { Vector3D(it.z * -1.0, it.y, it.x) }, //-z+y+x
    { Vector3D(it.z * -1.0, it.y * -1.0, it.x * -1.0) }, //-z-y-x
)