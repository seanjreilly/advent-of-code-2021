package day22

import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

class Day22Test {
    @Nested
    inner class CuboidTest {
        @Test
        fun `constructor should take 3 ranges`() {
            val xRange = 10..12
            val yRange = 11..13
            val zRange = 12..14

            Cuboid(xRange, yRange, zRange)
        }

        @Test
        fun `size should return the number of points in the cuboid`() {
            val cuboid = Cuboid(10..12, 10..12, 10..12)

            assert(cuboid.size == 27L)
        }

        @Test
        @Suppress("EmptyRange")
        fun `size should return zero given an empty range`() {
            val cuboid = Cuboid(10..12, 10 until 10, 10 .. 12)

            assert(cuboid.size == 0L)
        }

        @Suppress("EmptyRange")
        @Test
        fun `size should return zero given a negative range`() {
            val cuboid = Cuboid(12..10, 12 until 10, 12 .. 10)

            assert(cuboid.size == 0L)
        }

        @Test
        fun `contains should return true given a cuboid entirely contained within this cuboid`() {
            val cuboid = Cuboid(10..15, 10..15, 10..15)
            val other = Cuboid(12..13, 12..13, 11..14)

            assert(cuboid.contains(other))
        }

        @Test
        fun `contains should return false given a cuboid not entirely contained in the x dimension`() {
            val cuboid = Cuboid(10..15, 10..15, 10..15)
            val other = Cuboid(9..13, 12..13, 11..14)

            assert(!cuboid.contains(other))
        }

        @Test
        fun `contains should return false given a cuboid not entirely contained in the y dimension`() {
            val cuboid = Cuboid(10..15, 10..15, 10..15)
            val other = Cuboid(12..13, 12..16, 11..14)

            assert(!cuboid.contains(other))
        }

        @Test
        fun `contains should return false given a cuboid not entirely contained in the z dimension`() {
            val cuboid = Cuboid(10..15, 10..15, 10..15)
            val other = Cuboid(12..13, 12..13, 9..14)

            assert(!cuboid.contains(other))
        }

        @Test
        fun `contains should return false given a cuboid that contains this cuboid`() {
            val cuboid = Cuboid(10..10, 10..10, 10..10)
            val other = Cuboid(8..12, 8..12, 8..12)

            assert(!cuboid.contains(other))
        }

        @Test
        fun `intersects should return true given another cuboid that overlaps when neither contains the other`() {
            val cuboid = Cuboid(10..15, 10..15, 10..15)
            val other = Cuboid(14..18, 8..12, 14..18)

            assert(cuboid.intersects(other))
        }

        @Test
        fun `intersects should return true given a cuboid contained in this one`() {
            val cuboid = Cuboid(1..10, 1..10, 1..10)
            val other = Cuboid(5..5, 5..5, 5..5)

            assert(cuboid.intersects(other))
        }

        @Test
        fun `intersects should return true given a cuboid that contains this one`() {
            val cuboid = Cuboid(5..5, 5..5, 5..5)
            val other = Cuboid(1..10, 1..10, 1..10)

            assert(cuboid.intersects(other))
        }

        @Test
        fun `intersects should return false given another cuboid that does not intersect this cuboid`() {
            val cuboid = Cuboid(10..15, 10..15, 10..15)
            val other = Cuboid(10..15, 10..15, 16..18)

            assert(!cuboid.intersects(other))
        }

        @Test
        fun `difference should return this cuboid given a cuboid that doesn't intersect this one`() {
            val cuboid = Cuboid(10..15, 10..15, 10..15)
            val other = Cuboid(14..18, 8..12, 17..18)
            check(!cuboid.intersects(other))

            val result: List<Cuboid> = cuboid - other

            assert(result == listOf(cuboid))
        }

        @Test
        fun `difference should return 6 cuboids given a cuboid completely contained within this one`() {
            val cuboid = Cuboid(1..3, 1..3, 1..3)
            val other = Cuboid(2..2, 2..2, 2..2)

            val result = cuboid - other
            assert(result.size == 6)
            assert(result.sumOf {it.size} == cuboid.size - other.size)
            assert(result.contains(Cuboid(1..1, 1..3, 1..3)))
            assert(result.contains(Cuboid(3..3, 1..3, 1..3)))
            assert(result.contains(Cuboid(2..2, 1..1, 1..3)))
            assert(result.contains(Cuboid(2..2, 3..3, 1..3)))
            assert(result.contains(Cuboid(2..2, 2..2, 1..1)))
            assert(result.contains(Cuboid(2..2, 2..2, 3..3)))

            result.forEach {
                assert(!other.contains(it))
                assert(!it.intersects(other))
                assert(cuboid.contains(it))
            }
        }

        @Test
        fun `difference should return 5 cuboids given another cuboid touching 1 side of this one`() {
            val cuboid = Cuboid(1..3, 1..3, 1..3)
            val other = Cuboid(3..3, 2..2, 2..2)

            val result = cuboid - other
            assert(result.size == 5)
            assert(result.sumOf {it.size} == cuboid.size - other.size)

            result.forEach {
                assert(!other.contains(it))
                assert(!it.intersects(other))
                assert(cuboid.contains(it))
            }
        }

        @Test
        fun `difference should return 4 cuboids given another cuboid touching 2 sides not at a corner of this one`() {
            val cuboid = Cuboid(1..3, 1..3, 1..3)
            val other = Cuboid(3..3, 3..3, 2..2)

            val result = cuboid - other
            assert(result.size == 4)
            assert(result.sumOf {it.size} == cuboid.size - other.size)

            result.forEach {
                assert(!other.contains(it))
                assert(!it.intersects(other))
                assert(cuboid.contains(it))
            }
        }

        @Test
        fun `difference should return 3 cuboids given another cuboid touching 1 corner (3 sides) of this one`() {
            val cuboid = Cuboid(1..3, 1..3, 1..3)
            val other = Cuboid(3..3, 3..3, 3..3)

            val result = cuboid - other
            assert(result.size == 3)
            assert(result.sumOf {it.size} == cuboid.size - other.size)

            result.forEach {
                assert(!other.contains(it))
                assert(!it.intersects(other))
                assert(cuboid.contains(it))
            }
        }

        @Test
        fun `difference should return 2 cuboids given another cuboid touching 2 corners (4 sides) of this one`() {
            val cuboid = Cuboid(1..3, 1..3, 1..3)
            val other = Cuboid(1..3, 2..3, 3..3)

            val result = cuboid - other
            assert(result.size == 2)
            assert(result.sumOf {it.size} == cuboid.size - other.size)

            result.forEach {
                assert(!other.contains(it))
                assert(!it.intersects(other))
                assert(cuboid.contains(it))
            }
        }

        @Test
        fun `difference should return 1 cuboid given another cuboid touching the entire xy face (5 sides) of this one`() {
            val cuboid = Cuboid(1..3, 1..3, 1..3)
            val other = Cuboid(1..3, 1..3, 3..3)

            val result = cuboid - other
            assert(result.size == 1)
            assert(result.sumOf {it.size} == cuboid.size - other.size)

            result.forEach {
                assert(!other.contains(it))
                assert(!it.intersects(other))
                assert(cuboid.contains(it))
            }
        }

        @Test
        fun `difference should return 1 cuboid given another cuboid touching the entire yz face (5 sides) of this one`() {
            val cuboid = Cuboid(1..3, 1..3, 1..3)
            val other = Cuboid(3..3, 1..3, 1..3)

            val result = cuboid - other
            assert(result.size == 1)
            assert(result.sumOf {it.size} == cuboid.size - other.size)

            result.forEach {
                assert(!other.contains(it))
                assert(!it.intersects(other))
                assert(cuboid.contains(it))
            }
        }

        @Test
        fun `difference should return 1 cuboid given another cuboid touching the entire xz face (5 sides) of this one`() {
            val cuboid = Cuboid(1..3, 1..3, 1..3)
            val other = Cuboid(1..3, 1..1, 1..3)

            val result = cuboid - other
            assert(result.size == 1)
            assert(result.sumOf {it.size} == cuboid.size - other.size)

            result.forEach {
                assert(!other.contains(it))
                assert(!it.intersects(other))
                assert(cuboid.contains(it))
            }
        }

        @Test
        fun `difference should return an empty list given another cuboid that contains this one`() {
            val cuboid = Cuboid(1..3, 1..3, 1..3)
            val other = Cuboid(2..2, 2..2, 2..2)

            val result = other - cuboid
            assert(result.isEmpty())
        }

        @Test
        fun `difference should return the correct result given another cuboid that only partially intersects with this one`() {
            val cuboid = Cuboid(1..3, 1..3, 1..3)
            val other = Cuboid(1..3, 1..3, 3..5)

            val result = cuboid - other
            assert(result.size == 1)
            assert(result[0] == Cuboid(1..3, 1..3, 1..2))
        }

        @Test
        fun `difference should return the correct result given another cuboid which intersects at a 3d offset`() {
            val cuboid = Cuboid(11..13, 11..13, 11..13)
            val other = Cuboid(10..12, 10..12, 10..12)

            val difference = cuboid - other

            assert(difference.sumOf { it.size } == 19L)
        }
    }

    val sampleInput = """
        on x=-20..26,y=-36..17,z=-47..7
        on x=-20..33,y=-21..23,z=-26..28
        on x=-22..28,y=-29..23,z=-38..16
        on x=-46..7,y=-6..46,z=-50..-1
        on x=-49..1,y=-3..46,z=-24..28
        on x=2..47,y=-22..22,z=-23..27
        on x=-27..23,y=-28..26,z=-21..29
        on x=-39..5,y=-6..47,z=-3..44
        on x=-30..21,y=-8..43,z=-13..34
        on x=-22..26,y=-27..20,z=-29..19
        off x=-48..-32,y=26..41,z=-47..-37
        on x=-12..35,y=6..50,z=-50..-2
        off x=-48..-32,y=-32..-16,z=-15..-5
        on x=-18..26,y=-33..15,z=-7..46
        off x=-40..-22,y=-38..-28,z=23..41
        on x=-16..35,y=-41..10,z=-47..6
        off x=-32..-23,y=11..30,z=-14..3
        on x=-49..-5,y=-3..45,z=-29..18
        off x=18..30,y=-20..-8,z=-3..13
        on x=-41..9,y=-7..43,z=-33..15
        on x=-54112..-39298,y=-85059..-49293,z=-27449..7877
        on x=967..23432,y=45373..81175,z=27513..53682
    """.trimIndent().lines()

    @Test
    fun `parse should return a list of instruction+cuboid pairs`() {
        val result:List<Pair<Boolean, Cuboid>> = parse(sampleInput)

        assert(result.size == 22)
        assert(result[0] == Pair(true, Cuboid(-20..26,-36..17, -47..7)))
        assert(result[10] == Pair(false, Cuboid(-48..-32,26..41,-47..-37)))
        assert(result.last() == Pair(true, Cuboid(967..23432, 45373..81175, 27513..53682)))
        assert(result.count { !it.first } == 5)
        assert(result.count { it.first } == 17)
    }

    @Test
    fun `part1 should parse instructions, discard instructions outside of -50 to 50, and return the total number of on cubes after following all commands in order`() {
        assert(part1(sampleInput) == 590784L)
    }

    @Test
    fun `processInstructions should work with a small set of instructions`() {
        val smallInput = """
            on x=10..12,y=10..12,z=10..12
            on x=11..13,y=11..13,z=11..13
            off x=9..11,y=9..11,z=9..11
            on x=10..10,y=10..10,z=10..10
        """.trimIndent().lines()

        val instructions = parse(smallInput)

        val result = processInstructions(instructions)

        assert (result.sumOf { it.size } == 39L)
    }
}
