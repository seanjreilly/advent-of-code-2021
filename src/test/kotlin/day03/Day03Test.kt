package day03

import org.junit.jupiter.api.Test

class Day03Test {

    private val testInput = """
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

    @Test
    fun `part1 should calculate gamma and epsilon values and return their product`() {

        //gamma value is calculated from the most common bits in each position: 10110 = 22
        val expectedGammaValue = "10110".toUInt(2)
        //epsilon value is the from the least common bits in each position (and therefore the binary inverse of gamma): 01001 = 9
        val expectedEpsilonValue = "01001".toUInt(2)

        val result = part1(testInput)


        //returned value is the product of gamma and epsilon = 22 * 9 = 198
        assert(result == expectedGammaValue * expectedEpsilonValue)
    }
    
    @Test
    fun `part2 should calculate oxygen generator rating and CO2 scrubber rating and return their product`() {
        /*
        To calculate oxygen generator rating:
        Start with all numbers and consider only the first bit of each number.
        There are more 1 bits (7) than 0 bits (5), so keep only the 7 numbers with a 1 in the first position: 11110, 10110, 10111, 10101, 11100, 10000, and 11001.
        Consider the second bit of the 7 remaining numbers: there are more 0 bits (4) than 1 bits (3), so keep only the 4 numbers with a 0 in the second position: 10110, 10111, 10101, and 10000.
        In the third position, three of the four numbers have a 1, so keep those three: 10110, 10111, and 10101.
        In the fourth position, two of the three numbers have a 1, so keep those two: 10110 and 10111.
        In the fifth position, there are an equal number of 0 bits and 1 bits (one each). So, to find the oxygen generator rating, keep the number with a 1 in that position: 10111.
        As there is only one number left, stop; the oxygen generator rating is 10111, or 23 in decimal.
        */
        val expectedOxygenGeneratorRating = "10111".toUInt(2)

        /*
        To calculate CO2 scrubber rating:
        Start with all 12 numbers and consider only the first bit of each number.
        There are fewer 0 bits (5) than 1 bits (7), so keep only the 5 numbers with a 0 in the first position: 00100, 01111, 00111, 00010, and 01010.
        Consider the second bit of the 5 remaining numbers: there are fewer 1 bits (2) than 0 bits (3), so keep only the 2 numbers with a 1 in the second position: 01111 and 01010.
        In the third position, there are an equal number of 0 bits and 1 bits (one each). So, to find the CO2 scrubber rating, keep the number with a 0 in that position: 01010.
        As there is only one number left, stop; the CO2 scrubber rating is 01010, or 10 in decimal.
        */
        val expectedCO2ScrubberRating = "01010".toUInt(2)

        val result = part2(testInput)

        assert(result == expectedOxygenGeneratorRating * expectedCO2ScrubberRating)
    }
}