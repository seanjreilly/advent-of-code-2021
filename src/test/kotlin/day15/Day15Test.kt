package day15

import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import utils.gridmap.GridMap
import utils.gridmap.Point

class Day15Test {
    private val sampleInput = """
        1163751742
        1381373672
        2136511328
        3694931569
        7463417111
        1319128137
        1359912421
        3125421639
        1293138521
        2311944581
    """.trimIndent().lines()

    @Test
    fun `part1 should find the least risky path to the bottom right corner and report the total cumulative risk`() {
        assert(part1(sampleInput) == 40)
    }

    @Nested
    inner class RiskMapTest {
        @Test
        fun `constructor should build a risk map`() {
            val riskMap = RiskMap(sampleInput)

            @Suppress("USELESS_IS_CHECK")
            assert(riskMap is GridMap<Risk>)
            assert(riskMap.width == sampleInput.first().length)
            assert(riskMap.height == sampleInput.size)
            (sampleInput.indices).forEach { y ->
                val line = sampleInput[y]
                line.forEachIndexed { x, risk ->
                    val point = Point(x,y)
                    assert(riskMap[point] == risk.toString().toInt())
                }
            }
        }

        @Test
        fun `findLowestRiskPath should return the path from source to destination with the lowest total risk`() {
            val riskMap = RiskMap(sampleInput)
            val source = Point(0,0)
            val destination = Point(riskMap.width, riskMap.height).northWest() //-1 to each coordinate to stay on the map

            val path: Path = riskMap.findLowestRiskPath(source, destination)

            //ensure correct path is returned
            assert(path.size == 19)
            assert(path[0].first == Point(0,0))
            assert(path[1].first == path[0].first.south())
            assert(path[2].first == path[1].first.south())
            assert(path[3].first == path[2].first.east())
            assert(path[4].first == path[3].first.east())
            assert(path[5].first == path[4].first.east())
            assert(path[6].first == path[5].first.east())
            assert(path[7].first == path[6].first.east())
            assert(path[8].first == path[7].first.east())
            assert(path[9].first == path[8].first.south())
            assert(path[10].first == path[9].first.east())
            assert(path[11].first == path[10].first.south())
            assert(path[12].first == path[11].first.south())
            assert(path[13].first == path[12].first.east())
            assert(path[14].first == path[13].first.south())
            assert(path[15].first == path[14].first.south())
            assert(path[16].first == path[15].first.south())
            assert(path[17].first == path[16].first.east())
            assert(path[18].first == path[17].first.south())
            assert(path[18].first == destination)

            //ensure correct cumulative risk is returned each part of the path
            assert(path[0].second == 0)
            (1 until path.size).forEach {
                assert(path[it].second == path[it-1].second + riskMap[path[it].first])
            }

            //ensure expected overall risk
            assert(path.last().second == 40)
        }
    }
}