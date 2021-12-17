package day17

import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

class Day17Test {
    private val sampleInput = "target area: x=20..30, y=-10..-5"

    @Nested
    inner class TargetAreaTest {

        @Test
        fun `constructor should return a TargetArea instance`() {
            val targetArea = TargetArea(sampleInput)

            assert(targetArea.targetXPosition == 20..30)
            assert(targetArea.targetYPosition == -10..-5)
        }
        
        @Test
        fun `contains should return true given a point in the target area`() {
            val targetArea = TargetArea(sampleInput)

            assert(targetArea.contains(Point (25, -7)))
        }

        @Test
        fun `contains should return false given a point outside of the target y range`() {
            val targetArea = TargetArea(sampleInput)

            assert(!targetArea.contains(Point (25, -11)))
            assert(!targetArea.contains(Point (25, -4)))
        }

        @Test
        fun `contains should return false given a point outside of the target x range`() {
            val targetArea = TargetArea(sampleInput)

            assert(!targetArea.contains(Point (19, -7)))
            assert(!targetArea.contains(Point (31, -7)))
        }

        @Test
        fun `contains should return true given a point on the edge of the target area`() {
            val targetArea = TargetArea(sampleInput)

            assert(targetArea.contains(Point (20, -10)))
            assert(targetArea.contains(Point (30, -10)))
            assert(targetArea.contains(Point (20, -5)))
            assert(targetArea.contains(Point (30, -5)))
        }
    }
    
    @Test
    fun `willHitTarget should return true given the sample area, and a starting x and y velocity that will put the probe in the target after a whole number of steps`() {
        val target = TargetArea(20..30, -10..-5)

        val goodStartingValues = listOf<Pair<Int, Int>>(
            Pair(7,2),
            Pair(6,3),
            Pair(9,0)
        )

        goodStartingValues.forEach { (xVelocity, yVelocity) ->
            assert(willHitTarget(target, initialXVelocity = xVelocity, initialYVelocity = yVelocity))
        }
    }

    @Test
    fun `willHitTarget should return false given an x velocity way too big to hit the target`() {
        val target = TargetArea(1..10, -10..-5)

        assert(!willHitTarget(target, initialXVelocity = 100, initialYVelocity = 1))
    }

    @Test
    fun `willHitTarget should return false given an x velocity too small to hit the target`() {
        val target = TargetArea(100..200, -10..-5)

        assert(!willHitTarget(target, initialXVelocity = 1, initialYVelocity = 1))
    }

    @Test
    fun `willHitTarget should return false given a result that has the probe fall vertically through the target too fast to hit it`() {
        val target = TargetArea(1..10, -10..-5)

        assert(!willHitTarget(target, initialXVelocity = 1, initialYVelocity = -100))
    }

    @Test
    fun `willHitTarget should return false given an x and y velocity that isn't in the target after a specific step`() {
        val target = TargetArea(sampleInput)

        assert(!willHitTarget(target, initialXVelocity = 17, initialYVelocity = -4))
    }

    @Test
    fun `willHitTarget should return true given a shot that just hits the bottom edge of the target on a step`() {
        val target = TargetArea(sampleInput)
        assert(willHitTarget(target, initialXVelocity = 6, initialYVelocity = 9))
    }

    @Test
    fun `willHitTarget should return false given a shot that is going just too fast to hit the target on a step`() {
        val target = TargetArea(sampleInput)
        assert(!willHitTarget(target, initialXVelocity = 6, initialYVelocity = 10))
    }

    @Test
    fun `findMaximumY should return zero given 0`() {
        assert(findMaximumY(initialY = 0) == 0L)
    }

    @Test
    fun `findMaximumY should return its input given a negative int`() {
        (-10..-1).forEach { initialY ->
            assert(findMaximumY(initialY) == initialY.toLong())
        }
    }

    @Test
    fun `findMaximumY should return the sum of 1 to initialY given a positive int`() {
        assert(findMaximumY(1) == 1.toLong())
        assert(findMaximumY(2) == 3.toLong())
        assert(findMaximumY(3) == 6.toLong())
        assert(findMaximumY(4) == 10.toLong())
    }

    @Test
    fun `findFarthestX should return 0 given 0`() {
        assert(findFarthestX(0) == 0)
    }

    @Test
    fun `findFarthestX should return a positive number given a positive initial X`() {
        assert(findFarthestX(1) == 1)
        assert(findFarthestX(2) == 3)
        assert(findFarthestX(3) == 6)
        assert(findFarthestX(4) == 10)
    }

    @Test
    fun `findFarthestX should return a negative number given a negative initial X`() {
        assert(findFarthestX(-1) == -1)
        assert(findFarthestX(-2) == -3)
        assert(findFarthestX(-3) == -6)
        assert(findFarthestX(-4) == -10)
    }

    @Test
    fun `part1 should return the maximum y achievable for any starting velocities that hit the target area`() {
        assert(part1(sampleInput) == 45L)
    }
}