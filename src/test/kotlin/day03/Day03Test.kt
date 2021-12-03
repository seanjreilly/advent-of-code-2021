package day03

import org.junit.jupiter.api.Test

class Day03Test {
    
    @Test
    fun `part1 should calculate gamma and epsilon values and return their product`() {
        val input = """
            00100
            11110
            10110
            10111
            10101
            01111
            00111
            11100
            10000
            11001
            00010
            01010
        """.trimIndent().lines()

        //gamma value is calculated from the most common bits in each position: 10110 = 22
        val expectedGammaValue = "10110".toUInt(2)
        //epsilon value is the from the least common bits in each position (and therefore the binary inverse of gamma): 01001 = 9
        val expectedEpsilonValue = "01001".toUInt(2)

        val result = part1(input)


        //returned value is the product of gamma and epsilon = 22 * 9 = 198
        assert(result == expectedGammaValue * expectedEpsilonValue)
    }
}