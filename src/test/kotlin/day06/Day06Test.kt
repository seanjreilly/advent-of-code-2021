package day06

import org.junit.jupiter.api.Test

class Day06Test {

    @Test
    fun `part1 should count the total number of fish after 80 days`() {
        val result = part1("3,4,3,1,2")

        assert(result == 5934L)
    }

    @Test
    fun `part2 should count the total number of fish after 256 days`() {
        val result = part2("3,4,3,1,2")

        assert(result == 26984457539L)
    }

    @Test
    fun `totalFish should return the total number of fish`() {
        val fishCount = buildFishCount(0, 1, 2, 3, 4, 5, 6, 7, 8)

        val result = totalFish(fishCount)

        assert(result == (0 + 1 + 2 + 3 + 4 + 5 + 6 + 7 + 8).toLong())
    }

    @Test
    fun `parseFishCount should return a fish count with the appropriate number of fish in each frame`() {
        val input = "3,4,3,1,2"

        val result:FishCount = parseInitialFishCount(input)

        assert(result.counts == listOf(0L,1L,1L,2L,1L,0L,0L,0L,0L))
    }

    @Test
    fun `nextGeneration should produce a new FishCount with values from 8 through 1 decremented by 1`() {
        val fishCount = buildFishCount(0, 1, 2, 3, 4, 5, 6, 7, 8)

        val result: FishCount = nextGeneration(fishCount)

        assert(result.counts.subList(0, 8) == listOf(1L, 2L, 3L, 4L, 5L, 6L, 7L, 8L))
    }

    @Test
    fun `nextGeneration should produce a new FishCount with values from 0 added to 8 and to 6`() {
        val fishCount = buildFishCount(2, 0, 0, 0, 0, 0, 0, 1, 0)

        val result: FishCount = nextGeneration(fishCount)

        // the "zero" fish move to 6, and a new fish is added to 8 for each zero fish
        assert(result.counts == listOf(0L, 0L, 0L, 0L, 0L, 0L, 3L, 0L, 2L))
    }

    @Test
    fun `a fish count with known input should produce the correct value after 18 days`() {
        var count = parseInitialFishCount("3,4,3,1,2")
        (1..18).forEach { _ -> count = nextGeneration(count) }

        val result = totalFish(count)

        assert(result == 26L)
    }

    @Test
    fun `a fish count with known input should produce the correct value after 80 days`() {
        var count = parseInitialFishCount("3,4,3,1,2")
        (1..80).forEach { _ -> count = nextGeneration(count) }

        val result = totalFish(count)

        assert(result == 5934L)
    }

    private fun buildFishCount(
        zero: Int,
        one: Int,
        two: Int,
        three: Int,
        four: Int,
        five: Int,
        six: Int,
        seven: Int,
        eight: Int
    ): FishCount {
        return FishCount(
            listOf(
                zero.toLong(),
                one.toLong(),
                two.toLong(),
                three.toLong(),
                four.toLong(),
                five.toLong(),
                six.toLong(),
                seven.toLong(),
                eight.toLong()
            )
        )
    }
}
