package day04

import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

class Day04Test {

    @Test
    fun `part1 takes a draw order and bingo board definitions and returns the winning score`() {
        val input = """
            7,4,9,5,11,17,23,2,0,14,21,24,10,16,13,6,15,25,12,22,18,20,8,19,3,26,1

            22 13 17 11  0
             8  2 23  4 24
            21  9 14 16  7
             6 10  3 18  5
             1 12 20 15 19
            
             3 15  0  2 22
             9 18 13 17  5
            19  8  7 25 23
            20 11 10 24  4
            14 21 16 12  6
            
            14 21 17 24  4
            10 16 15  9 19
            18  8 23 26 20
            22 11 13  6  5
             2  0 12  3  7
             
        """.trimIndent().lines() //include an empty last line to match the input file

        val result = part1(input)

        assert(result == 4512)
    }

    @Nested
    inner class BingoBoardTest {
        val input = """
                |22 13 17 11  0
                | 8  2 23  4 24
                |21  9 14 16  7
                | 6 10  3 18  5
                | 1 12 20 15 19
            """.trimMargin().lines()

        private val board = BingoBoard(input)

        @Test
        fun `markNumber returns null if the number is not on the board`() {
            val result = board.markNumber(99) //not in the board

            assert(result == null)
        }

        @Test
        fun `markNumber returns null as long as a row or column isn't complete`() {
            //omit the top left corner so that when we complete a row and column the same sequence of calls *will* return a score
            listOf(13, 17, 11, 0, 8, 21, 6, 1).forEach {
                val result = board.markNumber(it)
                assert(result == null) {"expected no solution for submission $it"}
            }
        }

        @Test
        fun `markNumber returns a non-null result when any row or column is completed`() {
            val parsedValues = parseInput()

            //preconditions
            assert(parsedValues.size == 5)
            parsedValues.forEach { assert(it.size == 5) }

            //test each row
            parsedValues.forEach { row ->
                val board = BingoBoard(input)

                assert(board.markNumber(row[0]) == null)
                assert(board.markNumber(row[1]) == null)
                assert(board.markNumber(row[2]) == null)
                assert(board.markNumber(row[3]) == null)
                assert(board.markNumber(row[4]) != null) //bingo
            }

            //test each column
            (0..4).forEach {index ->
                val board = BingoBoard(input)
                assert(board.markNumber(parsedValues[0][index]) == null)
                assert(board.markNumber(parsedValues[1][index]) == null)
                assert(board.markNumber(parsedValues[2][index]) == null)
                assert(board.markNumber(parsedValues[3][index]) == null)
                assert(board.markNumber(parsedValues[4][index]) != null) //bingo
            }
        }

        private fun parseInput() = input.map {
            it.trim().split(BingoBoard.WHITESPACE_REGEX).map { it.toInt() }
        }

        @Test
        fun `markNumber returns the sum of the remaining entries times the winning number when a winning number is entered`() {
            //winning condition is the second column, with a few other numbers selected first
            val nonWinningNumbers = listOf(22, 13, 17, 2, 9, 10)
            nonWinningNumbers.forEach {
                assert(board.markNumber(it) == null)
            }

            val winningNumber = 12
            val result = board.markNumber(winningNumber)

            val remainingNumbers = parseInput().flatten().toMutableList()
            remainingNumbers.removeAll(nonWinningNumbers)
            remainingNumbers.remove(winningNumber)

            assert(result == remainingNumbers.sum() * winningNumber)
        }
    }
}
