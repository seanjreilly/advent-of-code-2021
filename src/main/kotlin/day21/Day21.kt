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
        val player = players.remove()
        players.add(player)

        player.score += player.advance(die.roll(3)) + 1 //zero-based position so we need to add 1 to the score
        if (player.score >= 1000) {
            //we have a winner
            val losingPlayersScore = players.first.score
            return losingPlayersScore * die.timesRolled
        }
    }
}

fun part2(input: List<String>): Int {
    return input.size
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
        .map { Pair(it[7].toString().toInt() , it.last().toString().toInt() - 1) }
        .map { Player(it.first, it.second, 0) }
        .toList()

    return LinkedList(players)
}

internal data class Player(val id: Int, var position: Int, var score: Int) {
    fun advance(spaces: Int) : Int {
        position = (position + spaces) % 10
        return position
    }
}