package day16

import org.junit.jupiter.api.Test

class Day16Test {

    // version id 6, type id 4, value 2021
    private val literalValuePacketExample = "D2FE28"

    //version id 1, type id 6, 27 bit payload
    // literal packet: 10
    // literal packet: 20
    private val operatorPacketWithBitLengthExample = "38006F45291200"

    //version 7, type id 3, 3 packet payload
    //literal packet: 1
    //literal packet: 2
    //literal packet: 3
    private val operatorPacketWithSubPacketLengthExample = "EE00D40C823060"

    @Test
    fun `parseBITSPacket should return a single literal value packet given appropriate input`() {
        val result : BITSPacket = parseBITSPacket(literalValuePacketExample)

        //validate header
        assert(result.version == 6)
        assert(result is LiteralValuePacket)
        val literalValuePacket = result as LiteralValuePacket

        assert(literalValuePacket.value == 2021L)
    }

    @Test
    fun `parseBITSPacket should return an operator packet given a container with a bit length`() {
        val result: BITSPacket = parseBITSPacket(operatorPacketWithBitLengthExample)

        //validate header
        assert(result.version == 1)
        assert(result is OperatorPacket)

        val operatorPacket = result as OperatorPacket
        assert(operatorPacket.subPackets.size == 2)
        assert((operatorPacket.subPackets[0] as LiteralValuePacket).value == 10L)

        assert((operatorPacket.subPackets[1] as LiteralValuePacket).value == 20L)
    }

    @Test
    fun `parseBITSPacket should return an operator packet given a container with a subpacket length`() {
        val result: BITSPacket = parseBITSPacket(operatorPacketWithSubPacketLengthExample)

        //validate header
        assert(result.version == 7)
        assert(result is OperatorPacket)

        val operatorPacket = result as OperatorPacket
        assert(operatorPacket.subPackets.size == 3)
        assert((operatorPacket.subPackets[0] as LiteralValuePacket).value == 1L)

        assert((operatorPacket.subPackets[1] as LiteralValuePacket).value == 2L)

        assert((operatorPacket.subPackets[2] as LiteralValuePacket).value == 3L)
    }

    @Test
    fun `sumVersionNumbers should return the version number given a literal packet`() {
        val packet : BITSPacket = LiteralValuePacket(1, 0)

        val result = packet.sumVersionNumbers()

        assert(result == packet.version)
    }

    @Test
    fun `sumVersionNumbers should return the version number plus the sum of versions from all subpackets given an operator packet`() {
        val subPacketA = LiteralValuePacket(16, 0)
        val subPacketB = OperatorPacket(1, getPacketOperator(0), listOf(LiteralValuePacket(3, 1)))
        val packet : BITSPacket = OperatorPacket(4, getPacketOperator(0), listOf(subPacketA, subPacketB))

        val result = packet.sumVersionNumbers()

        assert(result == 24)
    }

    @Test
    fun `part1 should sum the version numbers given sample inputs`() {
        assert(part1("8A004A801A8002F478") == 16)
        assert(part1("620080001611562C8802118E34") == 12)
        assert(part1("C0015000016115A2E0802F182340") == 23)
        assert(part1("A0016C880162017C3686B18A3D4780") == 31)
    }

    @Test
    fun `getPacketOperator should return a sum operator given a typeId of 0`() {
        val operator: Operator = getPacketOperator(0)

        assert(operator(listOf(1)) == 1L)
        assert(operator(listOf(1,2)) == 3L)
        assert(operator(listOf(1,2,3)) == 6L)
    }

    @Test
    fun `getPacketOperator should return a product operator given a typeId of 1`() {
        val operator: Operator = getPacketOperator(1)

        assert(operator(listOf(1)) == 1L)
        assert(operator(listOf(1,2)) == 2L)
        assert(operator(listOf(1,2,3)) == 6L)
    }

    @Test
    fun `getPacketOperator should return a min operator given a typeId of 2`() {
        val operator: Operator = getPacketOperator(2)

        assert(operator(listOf(1)) == 1L)
        assert(operator(listOf(1,2)) == 1L)
        assert(operator(listOf(2,3,4)) == 2L)
    }

    @Test
    fun `getPacketOperator should return a max operator given a typeId of 3`() {
        val operator: Operator = getPacketOperator(3)

        assert(operator(listOf(1)) == 1L)
        assert(operator(listOf(1,2)) == 2L)
        assert(operator(listOf(2,3,4)) == 4L)
    }

    @Test
    fun `getPacketOperator should return a greater than operator given a typeId of 5`() {
        val operator: Operator = getPacketOperator(5)

        assert(operator(listOf(2,1)) == 1L)
        assert(operator(listOf(1,1)) == 0L)
        assert(operator(listOf(1,2)) == 0L)
    }

    @Test
    fun `getPacketOperator should return a less than operator given a typeId of 6`() {
        val operator: Operator = getPacketOperator(6)

        assert(operator(listOf(2,1)) == 0L)
        assert(operator(listOf(1,1)) == 0L)
        assert(operator(listOf(1,2)) == 1L)
    }

    @Test
    fun `getPacketOperator should return an equals operator given a typeId of 7`() {
        val operator: Operator = getPacketOperator(7)

        assert(operator(listOf(2,1)) == 0L)
        assert(operator(listOf(1,1)) == 1L)
        assert(operator(listOf(1,2)) == 0L)
    }

    @Test
    fun `OperatorPacket dot value should return the operator applied over all of the subpackets values`() {
        val subPackets = listOf(
            LiteralValuePacket(1, 1),
            LiteralValuePacket(1, 2),
            LiteralValuePacket(1, 3)
        )

        val packet = OperatorPacket(1, getPacketOperator(0), subPackets)

        val result = packet.value

        assert(result == 6L)
    }

    @Test
    fun `part2 should parse the packets and find the value of the outermost packet`() {
        assert(part2("C200B40A82") == 3L) //sum of 1 and 2
        assert(part2("04005AC33890") == 54L) //product of 54
        assert(part2("880086C3E88112") == 7L) //min of 7, 8, 9
        assert(part2("CE00C43D881120") == 9L) //max of 7, 8, 9
        assert(part2("D8005AC2A8F0") == 1L) //5 < 15
        assert(part2("F600BC2D8F") == 0L) //5 > 15
        assert(part2("9C005AC2F8F0") == 0L) //5 == 15
        assert(part2("9C0141080250320F1802104A08") == 1L) //1 + 3 == 2 * 2
    }
}