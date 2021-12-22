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

            assert(cuboid.size == 27)
        }

        @Test
        @Suppress("EmptyRange")
        fun `size should return zero given an empty range`() {
            val cuboid = Cuboid(10..12, 10 until 10, 10 .. 12)

            assert(cuboid.size == 0)
        }

        @Suppress("EmptyRange")
        @Test
        fun `size should return zero given a negative range`() {
            val cuboid = Cuboid(12..10, 12 until 10, 12 .. 10)

            assert(cuboid.size == 0)
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
            assert(result.contains(Cuboid(1..3, 1..3, 1..1)))
            assert(result.contains(Cuboid(1..3, 1..3, 3..3)))
            assert(result.contains(Cuboid(1..1, 2..2, 1..3)))
            assert(result.contains(Cuboid(3..3, 2..2, 1..3)))
            assert(result.contains(Cuboid(2..2, 1..1, 2..2)))
            assert(result.contains(Cuboid(2..2, 3..3, 2..2)))

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

        //TODO: carry on/off-ness?
    }
}
