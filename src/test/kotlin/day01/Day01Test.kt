package day01
import org.junit.jupiter.api.Test

class Day01Test {
    @Test
    fun `list_windowed() creates three part sliding windows`() {
        val list = listOf(1,2,3,4,5,6,7,8,9,10)
        val result = list.windowed(3, 1, false)
        println(result)
        assert(result.size == 8)
        result.forEach { assert(it.size == 3) }
    }

    @Test
    fun `list_windowed(2, 1) creates 9 windows with 2 elements each`() {
        val list = listOf(1,2,3,4,5,6,7,8,9,10)
        val result = list.windowed(2, 1, false)
        println(result)
        assert(result.size == 9)
        result.forEach { assert(it.size == 2) }
    }

    @Test
    fun `calculate the sum of a window`() {
        val list = listOf(1,2,3)
        val result = list.sum()
        assert(result == 6)

        val list2 = listOf(1,2,3,4,5,6,7,8,9,10)
        val windows = list2.windowed(3, 1, false)
        val windowSums = windows.map { it.sum() }
        assert(windowSums.first() == 1 + 2 + 3)
        assert(windowSums.last() == 8 + 9 + 10)
    }

    @Test
    fun `part1 should output the number of increases in the stream`() {
        val list = listOf(
            //sample values from the problem descriptions
            199, //(N/A - no previous measurement)
            200, //(increased)
            208, //(increased)
            210, //(increased)
            200, //(decreased)
            207, //(increased)
            240, //(increased)
            269, //(increased)
            260, //(decreased)
            263, //(increased)
        ).map(Int::toString)

        assert(part1(list) == 7 )
    }

    @Test
    fun `part2 should output the number of increases in the stream using three-element sliding windows`() {
        val list = listOf(
            //sample values from problem description
            199, //  A
            200, //  A B
            208, //  A B C
            210, //    B C D
            200, //  E   C D
            207, //  E F   D
            240, //  E F G
            269, //    F G H
            260, //      G H
            263, //        H
        ).map(Int::toString)

        assert(part2(list) == 5)
    }
}