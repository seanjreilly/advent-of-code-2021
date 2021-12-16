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

        assert(literalValuePacket.literal == "011111100101")
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
        assert((operatorPacket.subPackets[0] as LiteralValuePacket).literal == 10.toString(2))

        assert(operatorPacket.subPackets[1].typeId == 4)
        assert((operatorPacket.subPackets[1] as LiteralValuePacket).literal == 20.toString(2).padStart(8, '0'))
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
        assert((operatorPacket.subPackets[0] as LiteralValuePacket).literal == "0001")

        assert(operatorPacket.subPackets[1].typeId == 4)
        assert((operatorPacket.subPackets[1] as LiteralValuePacket).literal == "0010")

        assert(operatorPacket.subPackets[2].typeId == 4)
        assert((operatorPacket.subPackets[2] as LiteralValuePacket).literal == "0011")
    }
}

