package day20

import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import utils.gridmap.Point
import java.lang.IllegalStateException

class Day20Test {

    //a one-line piece of input broken into multiple lines for sanity
    private val sampleEnhancementAlgorithm = """..#.#..#####.#.#.#.###.##.....###.##.#..###.####..#####..#....#..#..##..##
#..######.###...####..#..#####..##..#.#####...##.#.#..#.##..#.#......#.###
.######.###.####...#.##.##..#..#..#####.....#.#....###..#.##......#.....#.
.#..#..##..#...##.######.####.####.#.#...#.......#..#.#.#...####.##.#.....
.#..#...##.#.##..#...##.#.##..###.#......#.#.......#.#.#.####.###.##...#..
...####.#..#..#.##.#....##..#.####....##...##..#...#......#.#.......#.....
..##..####..#...#.#.#...##..#.#..###..#####........#..####......#..#""".replace("\n","")

    private val sampleInput = """
        ${sampleEnhancementAlgorithm}
        
        #..#.
        #....
        ##..#
        ..#..
        ..###
    """.trimIndent().lines()

    //had to add extra edges myself because the test input doesn't do this
    val expectedStageOneResult = """
        .##.##.
        #..#.#.
        ##.#..#
        ####..#
        .#..##.
        ..##..#
        ...#.#.
    """.trimIndent().lines()

    //had to add extra edges myself because the test input doesn't do this
    val expectedStageTwoResult = """
        .......#.
        .#..#.#..
        #.#...###
        #...##.#.
        #.....#.#
        .#.#####.
        ..#.#####
        ...##.##.
        ....###..
    """.trimIndent().lines()

    @Test
    fun `parseEnhancementAlgorithm should return a boolean array given a string of hashes and dots`() {
        val result: BooleanArray = parseEnhancementAlgorithm(sampleInput)

        assert(result.size == 512)
        assert(!result[0])
        assert(!result[1])
        assert(result[2])
        assert(!result[509])
        assert(!result[510])
        assert(result[511])
    }

    @Nested
    inner class ImageTest {
        @Test
        fun `constructor should return a 5x1 grid with a background color of 0 given sample input`() {
            val image = Image(sampleInput)

            assert(image.width == 5)
            assert(image.height == 5)
            assert(!image.backgroundColor)
        }

        @Test
        fun `enhance should return a new larger image using the image enhancement algorithm`() {
            val enhancementAlgorithm = parseEnhancementAlgorithm(sampleInput)
            val image = Image(sampleInput)
            val expectedResult = Image(listOf("garbage line", "garbage line") + expectedStageOneResult)

            val result: Image = image.enhance(enhancementAlgorithm)

            //image is 2 higher and 2 wider because pixels one beyond the border can be set if the puzzle is sneaky
            //(and I've checked. It's sneaky)

            assert(result.width == image.width + 2)
            assert(result.height == image.height + 2)
            assert(result == expectedResult)
        }

        @Test
        fun `enhance should work multiple times`() {
            val enhancementAlgorithm = parseEnhancementAlgorithm(sampleInput)
            val image = Image(sampleInput)
            val expectedResult = Image(listOf("garbage line", "garbage line") + expectedStageTwoResult)

            val result: Image = image.enhance(enhancementAlgorithm).enhance(enhancementAlgorithm)

            assert(result.countLitPixels() == 35)
            assert(result.width == image.width + 4)
            assert(result.height == image.height + 4)
            assert(result == expectedResult)
        }

        @Test
        fun `enhance should work correctly when images off the edge of the current image are set`() {
            /*
            the bit at index 1 (corresponding to the bottom corner) is set in the enhancement algorithm
            the top left bit (only) of the image is set
            this combination should set a point outside of the original image
            which will become the new point 0,0

            everything else in the enhanced image should be unset
            */
            val enhancementAlgorithm =  parseEnhancementAlgorithm(listOf("." + "#" + ".".repeat(510)))
            val imageSource = """
                ignore this line
                
                #..
                ...
                ...
            """.trimIndent().lines()
            val image = Image(imageSource)

            val result = image.enhance(enhancementAlgorithm)

            assert(result.countLitPixels() == 1)
            assert(result[Point(0,0)])
        }

        @Test
        fun `enhance should set the backgroundColor if the test input is really sneaky and turns all zeros into ones`() {
            //the sample grid doesn't do this but the actual input does!
            val enhancementAlgorithm =  parseEnhancementAlgorithm(listOf("#".repeat(512)))
            val imageSource = """
                ignore this line
                
                ...
                ...
                ...
            """.trimIndent().lines()
            val image = Image(imageSource)

            val result = image.enhance(enhancementAlgorithm)

            assert(result.backgroundColor)
        }

        @Test
        fun `enhance should switch the background color back if unset by the mask`() {
            val enhancementAlgorithm =  parseEnhancementAlgorithm(listOf("#".repeat(511) + '.'))
            val image = Image(emptySet(), 5, 5, true)

            val result = image.enhance(enhancementAlgorithm)

            assert(!result.backgroundColor)
        }

        @Test
        fun `countLitPixels should return the number of lit pixels given an image with an unset background`() {
            val imageSource = """
                ignore this line
                
                ...
                .#.
                ...
            """.trimIndent().lines()
            val image = Image(imageSource)

            val result = image.countLitPixels()

            assert(result == 1)
        }

        @Test
        fun `countLitPixels should throw an exception given an image with set infinite background`() {
            //as the number of lit pixels would be infinity
            val image = Image(emptySet(), 1, 1, backgroundColor = true)

            assertThrows<IllegalStateException> { image.countLitPixels() }
        }
    }

    @Test
    fun `part1 should parse an image and enhancement algorithm, enhance the image twice, and return the number of set pixels in the twice-enhanced image`() {
        assert(part1(sampleInput) == 35)
    }
}