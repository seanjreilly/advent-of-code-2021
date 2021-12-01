import org.junit.jupiter.api.Test

class Day01Test {
    @Test
    fun `list_windowed() creates three part sliding windows`() {
        val list = listOf(1,2,3,4,5,6,7,8,9,10)
        val windowed = list.windowed(3, 1, false)
        println(windowed)
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
}