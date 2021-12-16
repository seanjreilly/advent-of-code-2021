package day16

import org.apache.commons.compress.utils.BitInputStream
import utils.readInput
import java.io.ByteArrayInputStream
import java.nio.ByteOrder
import java.util.*

fun main() {
    val input = readInput("Day16")
    println(part1(input.first()))
    println(part2(input.first()))
}

fun part1(input: String): Int {
    return parseBITSPacket(input).sumVersionNumbers()
}

fun part2(input: String): Long {
    return parseBITSPacket(input).value
}

internal sealed class BITSPacket(val version: Int) {
    abstract val value: Long
    abstract fun sumVersionNumbers() : Int
}
internal class LiteralValuePacket(version: Int, override val value:Long ) : BITSPacket(version) {
    override fun sumVersionNumbers() =  version
}
internal class OperatorPacket(version: Int, private val operator: Operator, val subPackets: List<BITSPacket>) : BITSPacket(version) {
    override fun sumVersionNumbers() =  version + subPackets.sumOf { it.sumVersionNumbers() }
    override val value: Long
        get() = operator(subPackets.map(BITSPacket::value))
}

internal fun parseBITSPacket(input: String): BITSPacket {
    //convert hex string to a byte array
    val byteArray : ByteArray = HexFormat.of().parseHex(input)
    val inputStream = BitInputStream(ByteArrayInputStream(byteArray), ByteOrder.BIG_ENDIAN)

    return parsePacket(inputStream).first
}

internal fun parsePacket(inputStream: BitInputStream) : Pair<BITSPacket, Int> {
    var bitsRead = 0

    //read header
    val version = inputStream.readBits(3).toInt()
    bitsRead += 3
    val typeId = inputStream.readBits(3).toInt()
    bitsRead +=3
    if (typeId == 4) { //literal packet
        //read literal in chunks
        val literalBinaryValue = StringBuilder()
        var anotherPacketComing: Boolean
        do {
            anotherPacketComing = (inputStream.readBits(1) == 1L)
            val packet = inputStream.readBits(4).toString(2).padStart(4, '0')
            literalBinaryValue.append(packet)
            bitsRead += 5
        } while (anotherPacketComing)

        return Pair(LiteralValuePacket(version, literalBinaryValue.toString().toLong(2)), bitsRead)
    }

    //operator packet
    val subPackets = mutableListOf<BITSPacket>()
    val lengthType = inputStream.readBits(1)
    bitsRead += 1

    if (lengthType == 0L) {
        //subpackets are restricted by bit length
        val lengthOfSubpackets = inputStream.readBits(15)
        bitsRead += 15

        var bitsToGo = lengthOfSubpackets
        do {

            val (subPacket, subPacketBitsRead) = parsePacket(inputStream)
            subPackets += subPacket
            bitsToGo -= subPacketBitsRead
            bitsRead += subPacketBitsRead
        } while (bitsToGo > 0)
    } else {
        //subpackets are restricted by number of subpackets
        var subPacketsRemaining = inputStream.readBits(11)
        bitsRead += 11

        while (subPacketsRemaining > 0) {
            val (subPacket, subPacketBitsRead) = parsePacket(inputStream)
            subPackets += subPacket
            bitsRead += subPacketBitsRead
            subPacketsRemaining --
        }
    }

    return Pair(OperatorPacket(version, getPacketOperator(typeId), subPackets), bitsRead)
}

internal typealias Operator = (List<Long>) -> Long

internal fun getPacketOperator(typeId: Int): Operator {
    return when (typeId) {
        0 -> List<Long>::sum
        1 -> { it -> it.reduce(Long::times) }
        2 -> { it -> it.minOrNull()!! }
        3 -> { it -> it.maxOrNull()!! }
        5 -> { it -> if (it[0] > it [1]) 1  else 0 }
        6 -> { it -> if (it[0] < it [1]) 1  else 0 }
        7 -> { it -> if (it[0] == it [1]) 1  else 0 }
        else -> { throw IllegalArgumentException("valid typeIds are 0-3 or 5-7") }
    }
}