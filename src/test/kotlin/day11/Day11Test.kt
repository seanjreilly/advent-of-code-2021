package day11

import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import utils.gridmap.*

class Day11Test {
    private val input = """
        5483143223
        2745854711
        5264556173
        6141336146
        6357385478
        4167524645
        2176841721
        6882881134
        4846848554
        5283751526
    """.trimIndent().lines()

    @Test
    fun `countTotalFlashes should report 204 total flashes after 10 steps`() {
        val result = countTotalFlashes(input, 10)
        assert(result == 204)
    }

    @Test
    fun `countTotalFlashes should report 1656 total flashes after 10 steps`() {
        val result = countTotalFlashes(input, 100)
        assert(result == 1656)
    }

    @Test
    fun `part1 should run 100 steps and return the total number of flashes`() {
        assert(part1(input) == 1656)
    }

    @Nested
    inner class OctopusMapTest {
        private val smallGridOfZeroes = listOf(
            "000",
            "000",
            "000"
        )

        @Test
        fun `constructor should create a grid of octopuses`() {
            val map = OctopusMap(smallGridOfZeroes)

            @Suppress("USELESS_IS_CHECK")
            assert(map is GridMap<Octopus>)
            assert(map.count() == 9)
            (0 until 3).forEach { x ->
                (0 until 3).forEach { y ->
                    val octopus = map[Point(x,y)]
                    assert(octopus == Octopus(0))
                }
            }
        }

        @Test
        fun `step should increase the power level of every octopus by 1 given no octopuses below 8`() {
            val map = OctopusMap(smallGridOfZeroes)

            map.step()

            map.forEach { assert(map[it].energyLevel == 1) }
        }
        
        @Test
        fun `step will make every octopus with an energy level greater than nine flash (which returns it's energy level to zero and raises the energy level of every adjacent octopus) and return the number of flashes`() {
            val input = listOf(
                "000",
                "090",
                "000"
            )
            val map = OctopusMap(input)
            val pointExpectedToFlash = Point(1,1)

            val result:Int = map.step()

            assert(result == 1)
            assert(map[pointExpectedToFlash].energyLevel == 0)
            map
                .filter { it != pointExpectedToFlash }
                .forEach { assert(map[it].energyLevel == 2) }
        }

        @Test
        fun `step should cascade flashing octopuses`() {
            val input = listOf(
                "999",
                "919",
                "999"
            )
            val map = OctopusMap(input)

            val result = map.step()

            assert(result == 9)
            map.forEach { assert(map[it].energyLevel == 0) }
        }
    }
}
