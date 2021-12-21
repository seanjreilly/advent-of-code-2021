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
        assert(players[0].position == 3.toByte()) //use zero-based positions instead of one-based
        assert(players[0].score == 0.toShort())
        assert(players[0].id == 1.toByte())

        assert(players[1].position == 7.toByte()) //use zero-based positions instead of one-based
        assert(players[1].score == 0.toShort())
        assert(players[1].id == 2.toByte())
    }

    @Test
    fun `part1 should parse input, play until the first player's score is 1000 or higher, and then return the losing players turn multiplied the number of times the die was rolled`() {
        assert(part1(sampleInput) == 739785)
    }

    @Nested
    inner class UniverseTest {
        @Test
        fun `constructor should return a new universe, with the first player playing next`() {
            val players = parse(sampleInput)
            val universe = Universe(players[0], players[1])

            assert(universe.nextPlayer == players[0])
        }

        @Test
        fun `findWinner should return null if no player has a score of 21 or higher`() {
            val players = parse(sampleInput)
            val universe = Universe(players[0], players[1])

            val result: Player? = universe.findWinner()

            assert(result == null)
        }

        @Test
        fun `findWinner should return player 0 if that player has a score of 21 or higher`() {
            val player0 = Player(0, 4, 21)
            val player1 = Player(1, 6, 20)
            val universe = Universe(player0, player1)

            val result = universe.findWinner()

            assert(result === player0)
        }

        @Test
        fun `findWinner should return player 1 if that player has a score of 21 or higher`() {
            val player0 = Player(0, 4, 20)
            val player1 = Player(1, 6, 21)
            val universe = Universe(player0, player1)

            val result = universe.findWinner()

            assert(result === player1)
        }

        @Test
        fun `advance should return a new universe with the next player's score and position advanced, and a different nextPlayer`() {
            val players = parse(sampleInput)
            val universe = Universe(players[0], players[1])
            val advanceAmount = 8
            val (expectedScore, expectedPosition) = universe.nextPlayer.advance(advanceAmount).let { Pair(it.score, it.position)  }

            val result:Universe = universe.advance(advanceAmount)

            assert(result !== universe)
            assert(result != universe)
            assert(result.nextPlayer.id != universe.nextPlayer.id)
            assert(result.nextPlayer != result.player0)
            assert(result.player0.score == expectedScore )
            assert(result.player0.position == expectedPosition )
        }
    }

    @Test
    fun `part2 should parse input, play with Dirac dice and multiple universes and return the number of universes in which the player that played most won`() {
        assert(part2(sampleInput) == 444356092776315L)
    }
}
