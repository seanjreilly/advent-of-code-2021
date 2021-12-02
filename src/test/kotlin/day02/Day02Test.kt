package day02

import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.MethodSource

class Day02Test {

    @Test
    fun `part1 should ignore blank lines`() {
        val input = listOf(
            "",
            "   ",
            ""
        )

        val result = part1(input)

        assert(result == 0)
    }

    @Test
    fun `part1 should calculate the multiple of horizontal distance and depth`() {
        val input = listOf(
            "forward 5", //horizontal position 5, depth 0
            "down 5", // horizontal position 5 depth 5 (down *increases* depth)
            "forward 8", //horizontal position 13, depth 5
            "up 3", //horizontal position 13, depth 2 (up *decreases* depth)
            "down 8", //horizontal position 13, depth 10
            "forward 2", //horizontal position 15, depth 10
        )

        val result = part1(input)

        assert(result == 150)
    }

    @ParameterizedTest
    @MethodSource("parseInstructionValues")
    fun `parseInstruction should return a direction and a value`(input : String, direction: Direction, distance: Int) {
        val result : Pair<Direction, Int> = parse(input)
        assert(result.first == direction)
        assert(result.second == distance)
    }

    companion object {
        @JvmStatic
        fun parseInstructionValues() : List<Arguments> {
            return listOf(
                Arguments.of("forward 5", Direction.forward, 5),
                Arguments.of("up 27", Direction.up, 27),
                Arguments.of("down 9", Direction.down, 9)
            )
        }
    }

    @Test
    fun `parseInstruction should ignore extra whitespace`() {
        val input = "     forward    5   "
        val result : Pair<Direction, Int> = parse(input)
        assert(result.first == Direction.forward)
        assert(result.second == 5)
    }
}