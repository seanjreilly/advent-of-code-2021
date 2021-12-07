package day07

import org.junit.jupiter.api.Test

class Day07Test {
    @Test
    fun `part1 should find the horizontal position all crabs can move to using the least fuel given a list of starting positions and return the fuel cost`() {
        val input = "16,1,2,0,4,2,7,1,2,14"

        val result = part1(input)

        assert(result == 37)
    }

    @Test
    fun `part2 should find the horizontal position all crabs can move to using the least fuel given a list of starting positions and a more complicated fuel cost formula, and return the fuel cost`() {
        val input = "16,1,2,0,4,2,7,1,2,14"

        val result = part2(input)

        assert(result == 168)
    }
}