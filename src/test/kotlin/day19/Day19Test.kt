package day19

import org.apache.commons.math.geometry.Vector3D
import org.junit.jupiter.api.Test

class Day19Test {
    private val sampleInput = """
        --- scanner 0 ---
        404,-588,-901
        528,-643,409
        -838,591,734
        390,-675,-793
        -537,-823,-458
        -485,-357,347
        -345,-311,381
        -661,-816,-575
        -876,649,763
        -618,-824,-621
        553,345,-567
        474,580,667
        -447,-329,318
        -584,868,-557
        544,-627,-890
        564,392,-477
        455,729,728
        -892,524,684
        -689,845,-530
        423,-701,434
        7,-33,-71
        630,319,-379
        443,580,662
        -789,900,-551
        459,-707,401

        --- scanner 1 ---
        686,422,578
        605,423,415
        515,917,-361
        -336,658,858
        95,138,22
        -476,619,847
        -340,-569,-846
        567,-361,727
        -460,603,-452
        669,-402,600
        729,430,532
        -500,-761,534
        -322,571,750
        -466,-666,-811
        -429,-592,574
        -355,545,-477
        703,-491,-529
        -328,-685,520
        413,935,-424
        -391,539,-444
        586,-435,557
        -364,-763,-893
        807,-499,-711
        755,-354,-619
        553,889,-390

        --- scanner 2 ---
        649,640,665
        682,-795,504
        -784,533,-524
        -644,584,-595
        -588,-843,648
        -30,6,44
        -674,560,763
        500,723,-460
        609,671,-379
        -555,-800,653
        -675,-892,-343
        697,-426,-610
        578,704,681
        493,664,-388
        -671,-858,530
        -667,343,800
        571,-461,-707
        -138,-166,112
        -889,563,-600
        646,-828,498
        640,759,510
        -630,509,768
        -681,-892,-333
        673,-379,-804
        -742,-814,-386
        577,-820,562

        --- scanner 3 ---
        -589,542,597
        605,-692,669
        -500,565,-823
        -660,373,557
        -458,-679,-417
        -488,449,543
        -626,468,-788
        338,-750,-386
        528,-832,-391
        562,-778,733
        -938,-730,414
        543,643,-506
        -524,371,-870
        407,773,750
        -104,29,83
        378,-903,-323
        -778,-728,485
        426,699,580
        -438,-605,-362
        -469,-447,-387
        509,732,623
        647,635,-688
        -868,-804,481
        614,-800,639
        595,780,-596

        --- scanner 4 ---
        727,592,562
        -293,-554,779
        441,611,-461
        -714,465,-776
        -743,427,-804
        -660,-479,-426
        832,-632,460
        927,-485,-438
        408,393,-506
        466,436,-512
        110,16,151
        -258,-428,682
        -393,719,612
        -211,-452,876
        808,-476,-593
        -575,615,604
        -485,667,467
        -680,325,-822
        -627,-443,-432
        872,-547,-609
        833,512,582
        807,604,487
        839,-516,451
        891,-625,532
        -652,-548,-490
        30,-46,-14
    """.trimIndent().lines()
    
    @Test
    fun `parse should return 5 scanners with 3d beacon points given sample input`() {
        val result: List<Scanner> = parse(sampleInput)

        assert(result.size == 5)
        assert(result[0].beacons.size == 25)
        assert(result[1].beacons.size == 25)
        assert(result[2].beacons.size == 26)
        assert(result[3].beacons.size == 25)
        assert(result[4].beacons.size == 26)
    }

    @Test
    fun `overlaps should return a rotation and an offset given a set of beacons that overlaps`() {
        val scanners = parse(sampleInput)

        val result: Pair<Vector3D, List<Vector3D>>? = scanners[0].beacons.overlaps(scanners[1].beacons)

        assert(result != null)
        assert(result!!.first == Vector3D(68, -1246, -43))
        assert(Vector3D(-618, -824, -621) in result.second)
        assert(Vector3D(-537, -823, -458) in result.second)
        assert(Vector3D(-447, -329, 318) in result.second)
        assert(Vector3D(404, -588, -901) in result.second)
        assert(Vector3D(544, -627, -890) in result.second)
        assert(Vector3D(528, -643, 409) in result.second)
        assert(Vector3D(-661, -816, -575) in result.second)
        assert(Vector3D(390, -675, -793) in result.second)
        assert(Vector3D(423, -701, 434) in result.second)
        assert(Vector3D(-345, -311, 381) in result.second)
        assert(Vector3D(459, -707, 401) in result.second)
        assert(Vector3D(-485, -357, 347) in result.second)
    }

    @Test
    fun `overlaps should return a rotation and an offset given another set of beacons that overlaps`() {
        val scanners = parse(sampleInput)

        val result: Pair<Vector3D, List<Vector3D>>? = scanners[1].beacons.overlaps(scanners[4].beacons)

        assert(result != null)
        assert(result!!.first == Vector3D(88, 113, -1104))
    }

    @Test
    fun `overlaps should return null given another set of beacons that does not overlap`() {
        val scanners = parse(sampleInput)

        val result: Pair<Vector3D, List<Vector3D>>? = scanners[0].beacons.overlaps(scanners[4].beacons)

        assert(result == null)
    }

    @Test
    fun `overlaps should return true given two sets that overlap`() {
        val scanners = parse(sampleInput)

        val result: Pair<Vector3D, List<Vector3D>>? = scanners[4].beacons.overlaps(scanners[2].beacons)

        assert(result != null)
    }

    @Test
    fun `buildCompleteMap should return a list of 79 beacons given sample input`() {
        val scanners = parse(sampleInput)

        val result: Set<Vector3D> = scanners.buildCompleteMap()

        assert (result.size == 79)
        assert(Vector3D(-892, 524, 684) in result)
        assert(Vector3D(-876, 649, 763) in result)
        assert(Vector3D(-838, 591, 734) in result)
        assert(Vector3D(-789, 900, -551) in result)
        assert(Vector3D(-739, -1745, 668) in result)
        assert(Vector3D(-706, -3180, -659) in result)
        assert(Vector3D(-697, -3072, -689) in result)
        assert(Vector3D(-689, 845, -530) in result)
        assert(Vector3D(-687, -1600, 576) in result)
        assert(Vector3D(-661, -816, -575) in result)
        assert(Vector3D(-654, -3158, -753) in result)
        assert(Vector3D(-635, -1737, 486) in result)
        assert(Vector3D(-631, -672, 1502) in result)
        assert(Vector3D(-624, -1620, 1868) in result)
        assert(Vector3D(-620, -3212, 371) in result)
        assert(Vector3D(-618, -824, -621) in result)
        assert(Vector3D(-612, -1695, 1788) in result)
        assert(Vector3D(-601, -1648, -643) in result)
        assert(Vector3D(-584, 868, -557) in result)
        assert(Vector3D(-537, -823, -458) in result)
        assert(Vector3D(-532, -1715, 1894) in result)
        assert(Vector3D(-518, -1681, -600) in result)
        assert(Vector3D(-499, -1607, -770) in result)
        assert(Vector3D(-485, -357, 347) in result)
        assert(Vector3D(-470, -3283, 303) in result)
        assert(Vector3D(-456, -621, 1527) in result)
        assert(Vector3D(-447, -329, 318) in result)
        assert(Vector3D(-430, -3130, 366) in result)
        assert(Vector3D(-413, -627, 1469) in result)
        assert(Vector3D(-345, -311, 381) in result)
        assert(Vector3D(-36, -1284, 1171) in result)
        assert(Vector3D(-27, -1108, -65) in result)
        assert(Vector3D(7, -33, -71) in result)
        assert(Vector3D(12, -2351, -103) in result)
        assert(Vector3D(26, -1119, 1091) in result)
        assert(Vector3D(346, -2985, 342) in result)
        assert(Vector3D(366, -3059, 397) in result)
        assert(Vector3D(377, -2827, 367) in result)
        assert(Vector3D(390, -675, -793) in result)
        assert(Vector3D(396, -1931, -563) in result)
        assert(Vector3D(404, -588, -901) in result)
        assert(Vector3D(408, -1815, 803) in result)
        assert(Vector3D(423, -701, 434) in result)
        assert(Vector3D(432, -2009, 850) in result)
        assert(Vector3D(443, 580, 662) in result)
        assert(Vector3D(455, 729, 728) in result)
        assert(Vector3D(456, -540, 1869) in result)
        assert(Vector3D(459, -707, 401) in result)
        assert(Vector3D(465, -695, 1988) in result)
        assert(Vector3D(474, 580, 667) in result)
        assert(Vector3D(496, -1584, 1900) in result)
        assert(Vector3D(497, -1838, -617) in result)
        assert(Vector3D(527, -524, 1933) in result)
        assert(Vector3D(528, -643, 409) in result)
        assert(Vector3D(534, -1912, 768) in result)
        assert(Vector3D(544, -627, -890) in result)
        assert(Vector3D(553, 345, -567) in result)
        assert(Vector3D(564, 392, -477) in result)
        assert(Vector3D(568, -2007, -577) in result)
        assert(Vector3D(605, -1665, 1952) in result)
        assert(Vector3D(612, -1593, 1893) in result)
        assert(Vector3D(630, 319, -379) in result)
        assert(Vector3D(686, -3108, -505) in result)
        assert(Vector3D(776, -3184, -501) in result)
        assert(Vector3D(846, -3110, -434) in result)
        assert(Vector3D(1135, -1161, 1235) in result)
        assert(Vector3D(1243, -1093, 1063) in result)
        assert(Vector3D(1660, -552, 429) in result)
        assert(Vector3D(1693, -557, 386) in result)
        assert(Vector3D(1735, -437, 1738) in result)
        assert(Vector3D(1749, -1800, 1813) in result)
        assert(Vector3D(1772, -405, 1572) in result)
        assert(Vector3D(1776, -675, 371) in result)
        assert(Vector3D(1779, -442, 1789) in result)
        assert(Vector3D(1780, -1548, 337) in result)
        assert(Vector3D(1786, -1538, 337) in result)
        assert(Vector3D(1847, -1591, 415) in result)
        assert(Vector3D(1889, -1729, 1762) in result)
        assert(Vector3D(1994, -1805, 1792) in result)
    }

    @Test
    fun `part1 should parse the input into scanners, build the complete map, and return the count of distinct beacons `() {
        assert(part1(sampleInput) == 79)
    }

    @Suppress("TestFunctionName") //fake constructor
    private fun Vector3D(x: Int, y: Int, z:Int) : Vector3D = Vector3D(x.toDouble(), y.toDouble(), z.toDouble())
}

