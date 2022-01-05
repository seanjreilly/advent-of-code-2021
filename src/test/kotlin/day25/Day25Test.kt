package day25

import org.junit.jupiter.api.Test
import utils.gridmap.Point

class Day25Test {
    @Test
    fun `parse should return a sparse grid of south facing and east facing cucumbers given rectangular input`() {
        val input = """
            .>.
            v..
        """.trimIndent().lines()

        val grid: CucumberGrid = parse(input)

        assert(grid.width == 3)
        assert(grid.height == 2)
        assert(grid[Point(1,0)] == Cucumber.EAST_FACING)
        assert(grid[Point(0,1)] == Cucumber.SOUTH_FACING)
        assert(grid.count() == 2)
    }
    
    @Test
    fun `step should move unblocked east facing cucumbers one step to the east`() {
        val grid = parse("...>>>>>...".lines())
        val expectedResult = parse("...>>>>.>..".lines())

        val result: CucumberGrid = grid.step().first

        assert(result != grid)
        assert(result == expectedResult)
    }

    @Test
    fun `step should move unblocked south facing cucumbers one step to the south after east facing cucumbers have moved`() {
        val input = """
            ..........
            .>v....v..
            .......>..
            ..........
        """.trimIndent().lines()

        val expectedResult = """
            ..........
            .>........
            ..v....v>.
            ..........
        """.trimIndent().lines()

        val grid = parse(input)

        val result: CucumberGrid = grid.step().first

        assert(result != grid)
        assert(result == parse(expectedResult))
    }

    @Test
    fun `step should wrap cucumbers around the right and bottom sides of the grid when they move`() {
        val input = """
            ...>...
            .......
            ......>
            v.....>
            ......>
            .......
            ..vvv..
        """.trimIndent().lines()

        val expectedResult = """
            ..vv>..
            .......
            >......
            v.....>
            >......
            .......
            ....v..
        """.trimIndent().lines()

        val grid = parse(input)

        val result: CucumberGrid = grid.step().first

        assert(result != grid)
        assert(result == parse(expectedResult))
    }

    @Test
    fun `part1 should construct a CucumberGrid and determine how many steps it will run until nothing moves`() {
        val input = """
            v...>>.vv>
            .vv>>.vv..
            >>.>v>...v
            >>v>>.>.v.
            v>v.vv.v..
            >.>>..v...
            .vv..>.>v.
            v.v..>>v.v
            ....v..v.>
        """.trimIndent().lines()

        assert(part1(input) == 58)
    }
}