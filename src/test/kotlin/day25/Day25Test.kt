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
}