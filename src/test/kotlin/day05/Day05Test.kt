package day05

import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

class Day05Test {
    @Test
    fun `part1 should return the number of points covered by more than one horizontal or vertical line`() {
        val input = """
            0,9 -> 5,9
            8,0 -> 0,8
            9,4 -> 3,4
            2,2 -> 2,1
            7,0 -> 7,4
            6,4 -> 2,0
            0,9 -> 2,9
            3,4 -> 1,4
            0,0 -> 8,8
            5,5 -> 8,2
            
        """.trimIndent().lines() //blank line because there's one in the file

        val result = part1(input)

        assert(result == 5)
    }

    @Test
    fun `part1 should work with wonky input`() {
        val input = """
            10,100 -> 120,100
            70,100 -> 500,100
        """.trimIndent().lines()

        val result = part1(input)

        assert(result == 51)
    }

    @Test
    fun `part2 should return the number of points covered by more than one line, including diagonal lines`() {
        val input = """
            0,9 -> 5,9
            8,0 -> 0,8
            9,4 -> 3,4
            2,2 -> 2,1
            7,0 -> 7,4
            6,4 -> 2,0
            0,9 -> 2,9
            3,4 -> 1,4
            0,0 -> 8,8
            5,5 -> 8,2
            
        """.trimIndent().lines() //blank line because there's one in the file

        val result = part2(input)

        assert(result == 12)
    }

    @Test
    fun `pad should extend a list to the desired length`() {
        val input = listOf("A", "B")
        val result = input.pad(3)
        assert(result == listOf("A", "B", "B"))
    }

    @Nested
    inner class LineTest {
        @Test
        fun `isDiagonal should return false if the endpoints have the same y value`() {
            val line = Line(Point(1,1), Point(3,1))

            assert(!line.isDiagonal)
        }

        @Test
        fun `isDiagonal should return false if the endpoints have the same x value`() {
            val line = Line(Point(1,1), Point(1,3))

            assert(!line.isDiagonal)
        }

        @Test
        fun `isDiagonal should return true if the endpoints have the different x and y values`() {
            val line = Line(Point(1,1), Point(3,3))

            assert(line.isDiagonal)
        }

        @Test
        fun `getPointsOnLine should return appropriate points given a horizontal line`() {
            val line = Line(Point(9,7), Point(7,7))

            val points = line.getPoints()

            assert(points == setOf(Point(9,7), Point(8,7), Point(7,7)))
        }

        @Test
        fun `getPointsOnLine should return appropriate points given a vertical line`() {
            val line = Line(Point(1,1), Point(1,3))

            val points = line.getPoints()

            assert(points == setOf(Point(1,1), Point(1,2), Point(1,3)))
        }

        @Test
        fun `getPointsOnLine should return appropriate points for 45 degree lines`() {
            val lineA = Line(Point(1,1), Point(3,3))
            val pointsA = lineA.getPoints()
            assert(pointsA == setOf(Point(1,1), Point(2,2), Point(3,3)))

            val lineB = Line(Point(9,7), Point(7,9))
            val pointsB = lineB.getPoints()
            assert(pointsB == setOf(Point(9,7), Point(8,8), Point(7,9)))
        }
    }
}

