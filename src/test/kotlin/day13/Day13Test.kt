package day13

import org.junit.jupiter.api.Nested

import org.junit.jupiter.api.Test
import utils.gridmap.Point

class Day13Test {
    private val sampleInput = """
        6,10
        0,14
        9,10
        0,3
        10,4
        4,11
        6,0
        6,12
        4,1
        0,13
        10,12
        3,4
        3,0
        8,4
        1,10
        2,14
        8,10
        9,0

        fold along y=7
        fold along x=5
    """.trimIndent().lines()

    private val sampleInputPoints = sampleInput.dropLast(3)

    private val sampleInputFoldInstructions = sampleInput.takeLast(3)

    @Test
    fun `part1 should perform the first fold and return the number of points set`() {
        assert(part1(sampleInput) == 17)
    }

    @Nested
    inner class SparseBooleanMapTest {

        @Test
        fun `parse should return a rectangular map with only the specified points set`() {
            val map: SparseBooleanMap = SparseBooleanMap.parse(sampleInputPoints)

            assert(map.width == 11)
            assert(map.height == 15)
        }

        @Test
        fun `parse should ignore any lines after a blank line`() {
            val expectedMap = SparseBooleanMap.parse(sampleInputPoints)
            val map = SparseBooleanMap.parse(sampleInput)

            assert(map == expectedMap)
        }

        @Test
        fun `SparseMap should report the points set in its input as set, and any others as unset`() {
            val map: SparseBooleanMap = SparseBooleanMap.parse(sampleInputPoints)
            val expectedTotalNumberOfPoints = 11 * 15 //width and height of the map based on biggest x and y values
            val expectedNumberOfSetPoints = 18
            val expectedNumberOfUnsetPoints = expectedTotalNumberOfPoints - expectedNumberOfSetPoints

            val (setPoints, unsetPoints) = map.partition { map[it] }

            assert(setPoints.size == expectedNumberOfSetPoints)
            assert(unsetPoints.size == expectedNumberOfUnsetPoints)
        }

        @Test
        fun `fold should return a new map with the bottom rows of the map folded up given a fold instruction with a y axis`() {
            val map = SparseBooleanMap.parse(sampleInputPoints)
            val foldInstruction = FoldInstruction(FoldAxis.y, 7)
            val expectedSetPoints = """
                #.##..#..#.
                #...#......
                ......#...#
                #...#......
                .#.#..#.###
                ...........
                ...........
            """.trimIndent().lines().toPointList()
            assert(expectedSetPoints.size == 17) {"precondition failed — expected points not calculated correctly"}

            val newMap : SparseBooleanMap = map.performFold(foldInstruction)

            assert(newMap.width == map.width) //vertical fold shouldn't change the width
            assert(newMap.height <= foldInstruction.foldValue) //if the last lines are empty the map might be smaller
            expectedSetPoints.forEach { assert(newMap[it]) }
            assert(newMap.count { newMap[it] } == expectedSetPoints.size)
        }

        @Test
        fun `fold should return a new map with the rightmost columns of the map folded left given a fold instruction with an x axis`() {

            val originalMapPoints = """
                #.##..#..#.
                #...#......
                ......#...#
                #...#......
                .#.#..#.###
                ...........
                ...........
            """.trimIndent().lines().toPointList()
            val map = SparseBooleanMap(originalMapPoints.toSet())

            val expectedMapPoints = """
                #####
                #...#
                #...#
                #...#
                #####
                .....
                .....
            """.trimIndent().lines().toPointList()
            assert(expectedMapPoints.size == 16) {"precondition failed — expected points not calculated correctly"}

            val foldInstruction = FoldInstruction(FoldAxis.x, 5)

            val newMap = map.performFold(foldInstruction)

            assert(newMap.height == map.height) //horizontal fold shouldn't change the height
            assert(newMap.width <= foldInstruction.foldValue) //if the last columns are empty the map might be smaller
            expectedMapPoints.forEach { assert(newMap[it]) }
            assert(newMap.count { newMap[it] } == expectedMapPoints.size)
        }
    }

    @Nested
    inner class FoldInstructionTest {

        @Test
        fun `parse should return a list of fold instructions`() {
            val foldInstructions:List<FoldInstruction> = FoldInstruction.parse(sampleInputFoldInstructions)

            assert(foldInstructions.size == 2)
            assert(foldInstructions[0].foldAlong == FoldAxis.y)
            assert(foldInstructions[0].foldValue == 7)
            assert(foldInstructions[1].foldAlong == FoldAxis.x)
            assert(foldInstructions[1].foldValue == 5)
        }

        @Test
        fun `parse should ignore any lines before a blank line`() {
            val instructionsWithoutLeadingLines = FoldInstruction.parse(sampleInputFoldInstructions)
            val instructionsFromOverallInput = FoldInstruction.parse(sampleInput)

            assert(instructionsWithoutLeadingLines == instructionsFromOverallInput)
        }
    }

}

private fun List<String>.toPointList(): List<Point> {
    return this
        .flatMapIndexed { y, line ->
            line.mapIndexed { x, char -> Pair(x, char) }
                .filter { (_, char) -> char == '#' }
                .map { (x, _) -> Point(x, y) }
        }
}