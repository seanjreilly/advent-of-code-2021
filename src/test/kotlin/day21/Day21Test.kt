package day21

import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import java.util.*

class Day21Test {
    val sampleInput = """
        Player 1 starting position: 4
        Player 2 starting position: 8
    """.trimIndent().lines()

    @Nested
    inner class DeterministicDieTest {
        @Test
        fun `calling roll 101 times should return in the numbers 1 to 100 and then 1 again`() {
            val die = DeterministicDie()

            val expectedResult = (1..100).toList() + 1

            val result = (1..101)
                .map { die.roll(1) }
                .toList()

            assert(result == expectedResult)
        }

        @Test
        fun `timesRolled should return 0 if roll hasn't been called`() {
            val die = DeterministicDie()

            assert(die.timesRolled == 0)
        }

        @Test
        fun `timesRolled should increase when roll is called`() {
            val die = DeterministicDie()

            (1..1000).forEach {
                die.roll(1)
                assert(die.timesRolled == it)
            }
        }

        @Test
        fun `roll should return the sum of calling roll three times given 3`() {
            val die = DeterministicDie()

            val result = die.roll(3)

            assert(result == 6)
            assert(die.timesRolled == 3)

            val secondResult = die.roll(3)

            assert(secondResult == 15)
            assert(die.timesRolled == 6)
        }
    }

    @Test
    fun `parse should return a LinkedList of players`() {
        val players: LinkedList<Player> = parse(sampleInput)

        assert(players.size == 2)
        assert(players[0].position == 3) //use zero-based positions instead of one-based
        assert(players[0].score == 0)
        assert(players[1].position == 7) //use zero-based positions instead of one-based
        assert(players[1].score == 0)
    }

    @Test
    fun `part1 should parse input, play until the first player's score is 1000 or higher, and then return the losing players turn multiplied the number of times the die was rolled`() {
        assert(part1(sampleInput) == 739785)
    }
}