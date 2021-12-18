package day18

import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

class Day18Test {
    @Nested
    inner class SnailfishNumberTests {
        @Nested
        inner class RegularNumberTest {
            @Test
            fun `constructor should take a single Int`() {
                @Suppress("UNUSED_VARIABLE")
                val result:SnailfishNumber = RegularNumber(12)
            }

            @Test
            fun `split should do nothing given a value of 9 or less`() {
                for (i in 1..9) {
                    val number = RegularNumber(i)
                    assert (number.split() == null)
                }
            }

            @Test
            fun `split should return a PairNumber given a value of 10 or more`() {
                val number = RegularNumber(10)
                val expectedResult = pairOf(5,5)

                val result = number.split()

                assert(result == expectedResult)
            }

            @Test
            fun `split should produce the left side of the pair by dividing the number by two and rounding down`() {
                assert(RegularNumber(11).split()?.left == RegularNumber(5))
                assert(RegularNumber(12).split()?.left == RegularNumber(6))
            }

            @Test
            fun `split should produce the right side of the pair by dividing the number by two and rounding up`() {
                assert(RegularNumber(11).split()?.right == RegularNumber(6))
                assert(RegularNumber(12).split()?.right == RegularNumber(6))
            }

            @Test
            fun `magnitude should return the number`() {
                val expectedNumber = 14
                assert(RegularNumber(expectedNumber).magnitude() == expectedNumber)
            }
        }

        @Nested
        inner class PairNumberTest {
            @Test
            fun `constructor should take any two SnailfishNumber instances`() {
                val regularNumber:SnailfishNumber = RegularNumber(0)
                val nestedPair:SnailfishNumber = pairOf(1,2)

                @Suppress("UNUSED_VARIABLE")
                val number = PairNumber(nestedPair, regularNumber)
            }

            @Test
            fun `split should return false when the pair contains two regular numbers that don't need to split`() {
                val pair = pairOf(numberThatDoesNotNeedToSplit,numberThatDoesNotNeedToSplit)

                val result:Boolean = pair.split()

                assert(!result)
            }

            @Test
            fun `split should return true and replace its left value with the split value when the pair's left number is a regular number that splits`() {
                val pair = pairOf(numberBigEnoughToSplit, pairThatDoesNotSplit())

                val result:Boolean = pair.split()

                assert(result)
                assert(pair.left is PairNumber) { "regular number should change to a pair" }
                assert(pair.left == RegularNumber(numberBigEnoughToSplit).split())
            }

            @Test
            fun `split should return true and replace its right value with the split value when the pair's right number is a regular number that splits`() {
                val pair = pairOf(pairThatDoesNotSplit(), numberBigEnoughToSplit)

                val result:Boolean = pair.split()

                assert(result)
                assert(pair.right is PairNumber) { "regular number should change to a pair" }
                assert(pair.right == RegularNumber(numberBigEnoughToSplit).split())
            }

            @Test
            fun `split should return false when the pair contains two pairs and neither splits`() {
                val pair = pairOf(pairThatDoesNotSplit(), pairThatDoesNotSplit())

                assert(!pair.split())
            }

            @Test
            fun `split should return true when the pair contains two pairs and the left pair splits`() {
                val leftPair = pairThatSplits()
                val pair = pairOf(leftPair, pairThatDoesNotSplit())
                val expectedLeftValue = pairThatSplits().apply { split() }

                assert(pair.split())
                assert(pair.left == expectedLeftValue)
            }

            @Test
            fun `split should return true when the pair contains two pairs and the right pair splits`() {
                val rightPair = pairThatSplits()
                val pair = pairOf(pairThatDoesNotSplit(), rightPair)
                val expectedRightValue = pairThatSplits().apply { split() }

                assert(pair.split())
                assert(pair.right == expectedRightValue)
            }

            @Test
            fun `split should stop at the first split when the pair contains two regular numbers`() {
                val pair = pairOf(numberBigEnoughToSplit, numberBigEnoughToSplit)
                val expectedResult = pairOf(RegularNumber(numberBigEnoughToSplit).split()!!, numberBigEnoughToSplit)

                assert(pair.split())
                assert(pair == expectedResult)
            }

            @Test
            fun `split should stop at the first split when the pair contains a regular number and a pair`() {
                val pair = pairOf(numberBigEnoughToSplit, pairThatSplits())
                val expectedResult = pairOf(RegularNumber(numberBigEnoughToSplit).split()!!, pairThatSplits())

                assert(pair.split())
                assert(pair == expectedResult)
            }

            @Test
            fun `split should stop at the first split when the pair contains a pair and a regular number`() {
                val pair = pairOf(pairThatSplits(), numberBigEnoughToSplit)
                val expectedResult = pairOf(pairThatSplits().apply { split() }, numberBigEnoughToSplit)

                assert(pair.split())
                assert(pair == expectedResult)
            }

            @Test
            fun `split should stop at the first split when the pair contains two pairs`() {
                val pair = pairOf(pairThatSplits(), pairThatSplits())
                val expectedResult = pairOf(pairThatSplits().apply { split() }, pairThatSplits())

                assert(pair.split())
                assert(pair == expectedResult)
                assert(pair.left != pair.right) {"extra paranoid check"}
            }

            @Test
            fun `explode should return false and do nothing given a pair with a depth less than four`() {
                assert(!pairOf(1, 1).explode())
                assert(!pairOf(pairOf(1,1), 1).explode())
                assert(!pairOf(pairOf(pairOf(1,1),1), 1).explode())
            }

            @Test
            fun `explode should explode a pair nested inside of four pairs, discarding the sum to the left if there is no regular number to the left and return true`() {
                val pair = pairOf(pairOf(pairOf(pairOf(pairOf(9,8),1),2),3),4) //[[[[[9,8],1],2],3],4]
                val expectedResult = pairOf(pairOf(pairOf(pairOf(0,9),2),3), 4) //[[[[0,9],2],3],4]

                assert (pair.explode())
                assert(pair == expectedResult)
            }

            @Test
            fun `explode should explode a pair nested inside of four pairs, discarding the sum to the right if there is no regular number to the right and return true`() {
                val pair = pairOf(7,pairOf(6,pairOf(5, pairOf(4, pairOf(3,2))))) //[7,[6,[5,[4,[3,2]]]]]
                val expectedResult = pairOf(7,pairOf(6,pairOf(5, pairOf(7,0)))) //[7,[6,[5,[7,0]]]]

                assert (pair.explode())
                assert(pair == expectedResult)
            }

            @Test
            fun `explode should explode a deeply nested pair with neighbours to the left and right, adding its left value to its left neighbour and its right value to its right neighbour and return true`() {
                val pair = pairOf(pairOf(6, pairOf(5, pairOf(4, pairOf(3,2)))), 1) //[[6,[5,[4,[3,2]]]],1]
                val expectedResult = pairOf(pairOf(6, pairOf(5, pairOf(7,0))), 3) //[[6,[5,[7,0]]],3]

                assert (pair.explode())
                assert(pair == expectedResult)
            }

            @Test
            fun `explode should only explode the leftmost deeply nested pair it finds and return true`() {
                val pair = pairOf(pairOf(3, pairOf(2, pairOf(1, pairOf(7,3)))), pairOf(6, pairOf(5, pairOf(4, pairOf(3,2))))) //[[3,[2,[1,[7,3]]]],[6,[5,[4,[3,2]]]]]
                val expectedResult = pairOf(pairOf(3, pairOf(2, pairOf(8,0))), pairOf(9, pairOf(5, pairOf(4, pairOf(3,2))))) //[[3,[2,[8,0]]],[9,[5,[4,[3,2]]]]]

                assert (pair.explode())
                assert(pair == expectedResult)
            }

            @Test
            fun `reduce should perform explode and call in a loop until both return false`() {
                val pair = pairOf(pairOf(pairOf(pairOf(pairOf(4,3), 4), 4), pairOf(7, pairOf(pairOf(8,4), 9))), pairOf(1,1)) //[[[[[4,3],4],4],[7,[[8,4],9]]],[1,1]]
                val expectedResult = pairOf(pairOf(pairOf(pairOf(0,7), 4), pairOf(pairOf(7,8), pairOf(6,0))), pairOf(8,1))//[[[[0,7],4],[[7,8],[6,0]]],[8,1]]

                pair.reduce()

                assert(pair == expectedResult)
            }

            @Test
            fun `magnitude should return three times the magnitude of its left number plus two times the magnitude of its right number`() {
                assert(pairOf(9,1).magnitude() == 29)
                assert(pairOf(1,9).magnitude() == 21)
                assert(pairOf(pairOf(9,1), pairOf(1,9)).magnitude() == 129)
            }

            @Test
            fun `plus should create a new pair containing both numbers and then perform a reduce`() {
                val first = pairOf(pairOf(pairOf(pairOf(4,3), 4), 4), pairOf(7, pairOf(pairOf(8,4), 9))) //[[[[4,3],4],4],[7,[[8,4],9]]]
                val second = pairOf(1,1)
                val expectedResult = pairOf(pairOf(pairOf(pairOf(0,7), 4), pairOf(pairOf(7,8), pairOf(6,0))), pairOf(8,1))//[[[[0,7],4],[[7,8],[6,0]]],[8,1]]

                assert (first + second == expectedResult)
            }
        }
    }

    @Test
    fun `parse should return a PairNumber`() {
        assert(parse("[1,2]") == pairOf(1,2))
        assert(parse("[[1,2],3]") == pairOf(pairOf(1,2), 3))
        assert(parse("[9,[8,7]]") == pairOf(9, pairOf(8,7)))
        assert(parse("[[1,9],[8,5]]") == pairOf(pairOf(1,9), pairOf(8,5)))
        assert(parse("[[[[1,2],[3,4]],[[5,6],[7,8]]],9]") == pairOf(pairOf(pairOf(pairOf(1,2), pairOf(3,4)), pairOf(pairOf(5,6), pairOf(7,8))), 9))
        assert(parse("[[[9,[3,8]],[[0,9],6]],[[[3,7],[4,9]],3]]") == pairOf(pairOf(pairOf(9, pairOf(3,8)), pairOf(pairOf(0,9), 6)), pairOf(pairOf(pairOf(3,7), pairOf(4,9)), 3)))
    }

    @Test
    fun `part1 should parse each line into a snailfish number, sum them in order, and return the magnitude of the final sum`() {
        val sampleInput = """
            [1,1]
            [2,2]
            [3,3]
            [4,4]
            [5,5]
            [6,6]
        """.trimIndent().lines()

        assert(part1(sampleInput.slice(0..3)) == parse("[[[[1,1],[2,2]],[3,3]],[4,4]]").magnitude())
        assert(part1(sampleInput.slice(0..4)) == parse("[[[[3,0],[5,3]],[4,4]],[5,5]]").magnitude())
        assert(part1(sampleInput) == parse("[[[[5,0],[7,4]],[5,5]],[6,6]]").magnitude())

        val largerInput = """
            [[[0,[5,8]],[[1,7],[9,6]]],[[4,[1,2]],[[1,4],2]]]
            [[[5,[2,8]],4],[5,[[9,9],0]]]
            [6,[[[6,2],[5,6]],[[7,6],[4,7]]]]
            [[[6,[0,7]],[0,9]],[4,[9,[9,0]]]]
            [[[7,[6,4]],[3,[1,3]]],[[[5,5],1],9]]
            [[6,[[7,3],[3,2]]],[[[3,8],[5,7]],4]]
            [[[[5,4],[7,7]],8],[[8,3],8]]
            [[9,3],[[9,9],[6,[4,9]]]]
            [[2,[[7,7],7]],[[5,8],[[9,3],[0,2]]]]
            [[[[5,2],5],[8,[3,7]]],[[5,[7,5]],[4,4]]]
        """.trimIndent().lines()
        assert(part1(largerInput) == 4140)
    }

    @Test
    fun `part2 should parse each line into a snailfish number sum every permutation, and return the largest magnitude`() {
        val sampleInput = """
            [[[0,[5,8]],[[1,7],[9,6]]],[[4,[1,2]],[[1,4],2]]]
            [[[5,[2,8]],4],[5,[[9,9],0]]]
            [6,[[[6,2],[5,6]],[[7,6],[4,7]]]]
            [[[6,[0,7]],[0,9]],[4,[9,[9,0]]]]
            [[[7,[6,4]],[3,[1,3]]],[[[5,5],1],9]]
            [[6,[[7,3],[3,2]]],[[[3,8],[5,7]],4]]
            [[[[5,4],[7,7]],8],[[8,3],8]]
            [[9,3],[[9,9],[6,[4,9]]]]
            [[2,[[7,7],7]],[[5,8],[[9,3],[0,2]]]]
            [[[[5,2],5],[8,[3,7]]],[[5,[7,5]],[4,4]]]
        """.trimIndent().lines()
        assert(part2(sampleInput) == 3993)
    }

    internal fun pairOf(left: Int, right:Int) = PairNumber(RegularNumber(left), RegularNumber(right))
    internal fun pairOf(left: Int, right:PairNumber) = PairNumber(RegularNumber(left), right)
    internal fun pairOf(left: PairNumber, right:Int) = PairNumber(left, RegularNumber(right))
    internal fun pairOf(left: PairNumber, right:PairNumber) = PairNumber(left, right)
    internal fun pairThatSplits() = pairOf(10, 1)
    internal fun pairThatDoesNotSplit() = pairOf(1, 1)
    internal val numberBigEnoughToSplit = 12
    internal val numberThatDoesNotNeedToSplit = 9
}
