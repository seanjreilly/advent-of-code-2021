package day16

import org.junit.jupiter.api.Test

class Day16Test {

    // version id 6, type id 4, value 2021
    private val literalValuePacketInput = "D2FE28"

    @Test
    fun `parseBITSPacket should return a single literal value packet given appropriate input`() {
        val result : BITSPacket = parseBITSPacket(literalValuePacketInput)

        //validate header
        assert(result.version == 6)
        assert(result is LiteralValuePacket)
        val literalValuePacket = result as LiteralValuePacket

        assert(literalValuePacket.literal == "011111100101")
    }

}

