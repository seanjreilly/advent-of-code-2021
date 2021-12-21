package day21

import utils.readInput
import java.util.*

fun main() {
    val input = readInput("Day21")
    println(part1(input))
    println(part2(input))
}

fun part1(input: List<String>): Int {
    val players = parse(input)
    val die = DeterministicDie()
    while (true) {
        //alternate players by taking the player from the head of the queue and adding them back to the tail
        var player = players.remove()

        player = player.advance(die.roll(3))
        if (player.score >= 1000) {
            //we have a winner
            val losingPlayersScore = players.first.score
            return losingPlayersScore * die.timesRolled
        }

        players.add(player)
    }
}

fun part2(input: List<String>): Long {
    val players = parse(input)
    val playerWinsById = mutableMapOf(players[0].id to 0L, players[1].id to 0L)
    var currentRoundUniverses = mapOf(
        Universe(players[0], players[1]) to 1L //we start in a single universe
    )

    while(!currentRoundUniverses.isEmpty()) {
        val nextRoundUniverses = mutableMapOf<Universe,Long>()
        currentRoundUniverses.forEach { (universe, universeCount) ->

            universe.findWinner()?.let { winner ->
                playerWinsById.merge(winner.id, universeCount, Long::plus)
                return@forEach
            }

            diracDice().forEach { (sumOfThreeRolls, newUniverses) ->
                val newUniverse = universe.advance(sumOfThreeRolls)
                //each turn can happen in *many* identical universes
                //we multiply the total number of universes in play to simulate this happening in many universes at once
                val newUniverseCount = universeCount * newUniverses

                nextRoundUniverses.merge(newUniverse, newUniverseCount, Long::plus)
            }
        }
        currentRoundUniverses = nextRoundUniverses
    }
    return playerWinsById.maxOf { it.value }
}

private fun diracDice() : List<Pair<Int, Long>> {
    return listOf(
        Pair(3, 1L), //over the three rolls, a total of 3 happens in 1 universe
        Pair(4, 3L), //the sum is 4 in 3 universes
        Pair(5, 6L), //the sum is 5 in 6 universes
        Pair(6, 7L), //the sum is 6 in 7 universes
        Pair(7, 6L), //the sum is 7 in 6 universes
        Pair(8, 3L), //the sum is 8 in 3 universes
        Pair(9, 1L), //the sum in 9 in 1 universe
    )
}

internal class DeterministicDie {
    internal var timesRolled: Int = 0
        private set

    private val numbers = iterator {
        while (true) {
            for (i in 1..100) {
                timesRolled++
                yield(i)
            }
        }
    }

    fun roll(times: Int): Int {
        var result = 0
        for (i in 1..times) {
            result += numbers.next()
        }
        return result
    }
}

internal fun parse(input: List<String>): LinkedList<Player> {
    val players = input
        .map { it.toCharArray() }
        .map { Pair(it[7].toString().toByte() , (it.last().toString().toInt() - 1).toByte()) }
        .map { Player(it.first, it.second, 0) }
        .toList()

    return LinkedList(players)
}

internal data class Player(val id: Byte, val position: Byte, val score: Short) {
    fun advance(spaces: Int) : Player {
        val newPosition = (position + spaces) % 10
        val newScore = score + newPosition + 1
        return Player(id, newPosition.toByte(), newScore.toShort())
    }
}

internal data class Universe(val player0: Player, val player1: Player, private val player0IsNext:Boolean = true) {
    fun findWinner(): Player? {
        if (player0.score > 20) {
            return player0
        }
        if (player1.score > 20) {
            return player1
        }
        return null
    }

    fun advance(spaces: Int): Universe {
        val newPlayer0 = if (player0IsNext) { player0.advance(spaces) } else { player0 }
        val newPlayer1 = if (!player0IsNext) { player1.advance(spaces) } else { player1 }
        return Universe(newPlayer0, newPlayer1, !player0IsNext)
    }

    val nextPlayer: Player
        get() = when(player0IsNext) {
            true -> player0
            else -> player1
        }
}