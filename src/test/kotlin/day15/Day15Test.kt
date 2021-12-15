package day15

import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.Timeout
import utils.gridmap.GridMap
import utils.gridmap.Point
import java.util.concurrent.TimeUnit

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

    private val massiveInput = """
        11637517422274862853338597396444961841755517295286
        13813736722492484783351359589446246169155735727126
        21365113283247622439435873354154698446526571955763
        36949315694715142671582625378269373648937148475914
        74634171118574528222968563933317967414442817852555
        13191281372421239248353234135946434524615754563572
        13599124212461123532357223464346833457545794456865
        31254216394236532741534764385264587549637569865174
        12931385212314249632342535174345364628545647573965
        23119445813422155692453326671356443778246755488935
        22748628533385973964449618417555172952866628316397
        24924847833513595894462461691557357271266846838237
        32476224394358733541546984465265719557637682166874
        47151426715826253782693736489371484759148259586125
        85745282229685639333179674144428178525553928963666
        24212392483532341359464345246157545635726865674683
        24611235323572234643468334575457944568656815567976
        42365327415347643852645875496375698651748671976285
        23142496323425351743453646285456475739656758684176
        34221556924533266713564437782467554889357866599146
        33859739644496184175551729528666283163977739427418
        35135958944624616915573572712668468382377957949348
        43587335415469844652657195576376821668748793277985
        58262537826937364893714847591482595861259361697236
        96856393331796741444281785255539289636664139174777
        35323413594643452461575456357268656746837976785794
        35722346434683345754579445686568155679767926678187
        53476438526458754963756986517486719762859782187396
        34253517434536462854564757396567586841767869795287
        45332667135644377824675548893578665991468977611257
        44961841755517295286662831639777394274188841538529
        46246169155735727126684683823779579493488168151459
        54698446526571955763768216687487932779859814388196
        69373648937148475914825958612593616972361472718347
        17967414442817852555392896366641391747775241285888
        46434524615754563572686567468379767857948187896815
        46833457545794456865681556797679266781878137789298
        64587549637569865174867197628597821873961893298417
        45364628545647573965675868417678697952878971816398
        56443778246755488935786659914689776112579188722368
        55172952866628316397773942741888415385299952649631
        57357271266846838237795794934881681514599279262561
        65719557637682166874879327798598143881961925499217
        71484759148259586125936169723614727183472583829458
        28178525553928963666413917477752412858886352396999
        57545635726865674683797678579481878968159298917926
        57944568656815567976792667818781377892989248891319
        75698651748671976285978218739618932984172914319528
        56475739656758684176786979528789718163989182927419
        67554889357866599146897761125791887223681299833479
    """.trimIndent().lines()

    @Test
    fun `part1 should find the least risky path to the bottom right corner and report the total cumulative risk`() {
        assert(part1(sampleInput) == 40)
    }

    @Test
    fun `part2 should map a bigger map from the input, and use that map to find the least risky path to the bottom right corner and report the total cumulative risk`() {
        assert(part2(sampleInput) == part1(massiveInput))
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
        fun `makeBiggerMap should return a new RiskMap 5 times as wide and 5 times as tall`() {
            val map = RiskMap(sampleInput)
            val expectedBiggerMap = RiskMap(massiveInput)

            val result:RiskMap = map.makeBiggerMap()

            assert(result.width == expectedBiggerMap.width)
            assert(result.height == expectedBiggerMap.height)
            result.forEach {
                assert(result[it] == expectedBiggerMap[it])
            }
        }

        @Test
        fun `findLowestRiskPathToBottomRightCorner should return the path to the bottom right corner with the lowest total risk`() {
            val riskMap = RiskMap(sampleInput)

            val path: Path = riskMap.findLowestRiskPathToBottomRightCorner()

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
            assert(path[18].first == riskMap.bottomRightCorner)

            //ensure correct cumulative risk is returned each part of the path
            assert(path[0].second == 0)
            (1 until path.size).forEach {
                assert(path[it].second == path[it-1].second + riskMap[path[it].first])
            }

            //ensure expected overall risk
            assert(path.last().second == 40)
        }

        @Test
        fun `findLowestRiskPathToBottomRightCorner should work for a fairly big map`() {
            val map = RiskMap(massiveInput)

            val result = map.findLowestRiskPathToBottomRightCorner()

            assert(result.last().second == 315)
        }

        @Test
        @Timeout(1, unit = TimeUnit.SECONDS)
        fun `findLowestRiskPathToBottomRightCorner should work for a really big map`() {
            val map = RiskMap(massiveInput).makeBiggerMap()

            map.findLowestRiskPathToBottomRightCorner()
        }
    }
}