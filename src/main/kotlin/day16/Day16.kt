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
    return parsePacket(input).sumVersionNumbers()
}

fun part2(input: String): Long {
    return parsePacket(input).value
}

internal sealed class BITSPacket(val version: Int) {
    abstract val value: Long
    abstract fun sumVersionNumbers() : Int
}
internal class LiteralValuePacket(version: Int, override val value:Long ) : BITSPacket(version) {
    override fun sumVersionNumbers() =  version
}
internal class OperatorPacket(version: Int, operator: Operator, val subPackets: List<BITSPacket>) : BITSPacket(version) {
    override fun sumVersionNumbers() =  version + subPackets.sumOf { it.sumVersionNumbers() }
    override val value: Long = operator(subPackets.map(BITSPacket::value))
}

//region binary parsing logic

internal fun parsePacket(input: String): BITSPacket {
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
    if (typeId == 4) {
        //type id 4 represents a literal packet
        val (literalBinaryValue, bitsReadInLiteralValue) = parseLiteralValue(inputStream)
        bitsRead += bitsReadInLiteralValue
        return Pair(LiteralValuePacket(version, literalBinaryValue), bitsRead)
    }

    //all other type ids represent an operator packet
    //length of sub packets is determined in one of two ways
    val lengthType = inputStream.readBits(1)
    bitsRead += 1
    val operation = if (lengthType == 0L) ::parseSubPacketsByBitLength else ::parseSubPacketsByPacketCount

    val (subPackets: List<BITSPacket>, bitsReadInSubPackets) = operation(inputStream)
    bitsRead += bitsReadInSubPackets

    return Pair(OperatorPacket(version, getPacketOperator(typeId), subPackets), bitsRead)
}

private fun parseSubPacketsByPacketCount(inputStream: BitInputStream): Pair<List<BITSPacket>, Int> {
    var bitsRead = 0
    val subPackets = mutableListOf<BITSPacket>()
    val subPacketsToParse = inputStream.readBits(11)
    bitsRead += 11

    while (subPackets.size < subPacketsToParse) {
        val (subPacket, subPacketBitsRead) = parsePacket(inputStream)
        subPackets += subPacket
        bitsRead += subPacketBitsRead
    }
    return Pair(subPackets, bitsRead)
}

private fun parseSubPacketsByBitLength(inputStream: BitInputStream): Pair<List<BITSPacket>, Int> {
    val subPackets = mutableListOf<BITSPacket>()
    var bitsRead = 0
    val bitLengthOfSubpackets = inputStream.readBits(15)
    bitsRead += 15

    var bitsToGo = bitLengthOfSubpackets
    do {
        val (subPacket, subPacketBitsRead) = parsePacket(inputStream)
        subPackets += subPacket
        bitsToGo -= subPacketBitsRead
        bitsRead += subPacketBitsRead
    } while (bitsToGo > 0)
    return Pair(subPackets, bitsRead)
}

private fun parseLiteralValue(inputStream: BitInputStream, ): Pair<Long, Int> {
    var bitsRead = 0
    //read literal in chunks
    val literalBinaryValue = StringBuilder()
    var anotherPacketComing: Boolean
    do {
        anotherPacketComing = (inputStream.readBits(1) == 1L)
        val packet = inputStream.readBits(4).toString(2).padStart(4, '0')
        literalBinaryValue.append(packet)
        bitsRead += 5
    } while (anotherPacketComing)

    return Pair(literalBinaryValue.toString().toLong(2), bitsRead)
}

//endregion

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