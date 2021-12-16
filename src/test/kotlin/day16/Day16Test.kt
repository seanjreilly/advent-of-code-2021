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
        assert(result.typeId == 4)
        assert(result is LiteralValuePacket)
        val literalValuePacket = result as LiteralValuePacket

        assert(literalValuePacket.literal == 2021)
    }

    @Test
    fun `parseBITSPacket should return an operator packet given a container with a bit length`() {
        val result: BITSPacket = parseBITSPacket(operatorPacketWithBitLengthExample)

        //validate header
        assert(result.version == 1)
        assert(result.typeId == 6)
        assert(result is OperatorPacket)

        val operatorPacket = result as OperatorPacket
        assert(operatorPacket.subPackets.size == 2)
        assert(operatorPacket.subPackets[0].typeId == 4)
        assert((operatorPacket.subPackets[0] as LiteralValuePacket).literal == 10)

        assert(operatorPacket.subPackets[1].typeId == 4)
        assert((operatorPacket.subPackets[1] as LiteralValuePacket).literal == 20)
    }

    @Test
    fun `parseBITSPacket should return an operator packet given a container with a subpacket length`() {
        val result: BITSPacket = parseBITSPacket(operatorPacketWithSubPacketLengthExample)

        //validate header
        assert(result.version == 7)
        assert(result.typeId == 3)
        assert(result is OperatorPacket)

        val operatorPacket = result as OperatorPacket
        assert(operatorPacket.subPackets.size == 3)
        assert(operatorPacket.subPackets[0].typeId == 4)
        assert((operatorPacket.subPackets[0] as LiteralValuePacket).literal == 1)

        assert(operatorPacket.subPackets[1].typeId == 4)
        assert((operatorPacket.subPackets[1] as LiteralValuePacket).literal == 2)

        assert(operatorPacket.subPackets[2].typeId == 4)
        assert((operatorPacket.subPackets[2] as LiteralValuePacket).literal == 3)
    }

    @Test
    fun `sumVersionNumbers should return the version number given a literal packet`() {
        val packet : BITSPacket = LiteralValuePacket(1, 4, 0)

        val result = packet.sumVersionNumbers()

        assert(result == packet.version)
    }

    @Test
    fun `sumVersionNumbers should return the version number plus the sum of versions from all subpackets given an operator packet`() {
        val subPacketA = LiteralValuePacket(16, 4, 0)
        val subPacketB = OperatorPacket(1, 2, listOf(LiteralValuePacket(3, 4, 1)))
        val packet : BITSPacket = OperatorPacket(4, 3, listOf(subPacketA, subPacketB))

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
}