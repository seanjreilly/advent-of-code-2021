package day16

import org.apache.commons.compress.utils.BitInputStream
import utils.readInput
import java.io.ByteArrayInputStream
import java.nio.ByteOrder
import java.util.*

fun main() {
    val input = readInput("Day15")
    println(part1(input.first()))
    println(part2(input.first()))
}

fun part1(input: String): Int {
    return input.length
}

fun part2(input: String): Int {
    return input.length
}

internal sealed class BITSPacket(val version: Int)
internal class LiteralValuePacket(version: Int, val literal:String ) : BITSPacket(version)

internal fun parseBITSPacket(input: String): BITSPacket {
    //convert hex string to a byte array
    val byteArray : ByteArray = HexFormat.of().parseHex(input)
    val inputStream = BitInputStream(ByteArrayInputStream(byteArray), ByteOrder.BIG_ENDIAN)

    //read header
    val version = inputStream.readBits(3).toInt()
    val typeId = inputStream.readBits(3).toInt()

    //read literal
    val literalBinaryValue = StringBuilder()
    var anotherPacketComing: Boolean
    do {
        anotherPacketComing = (inputStream.readBits(1) == 1L)
        val packet = inputStream.readBits(4).toString(2).padStart(4, '0')
        literalBinaryValue.append(packet)
    } while (anotherPacketComing)
    inputStream.alignWithByteBoundary()

    val literal = literalBinaryValue.toString().toInt(2)
    return LiteralValuePacket(version, literalBinaryValue.toString())
}