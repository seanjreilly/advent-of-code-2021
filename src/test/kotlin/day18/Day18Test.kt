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
            
            //TODO: explode tests? //regular numbers do not explode, but we might need a do nothing impl
            //TODO: plus operator?

            //regular number *done*
            //constructor *done*
            //regular number dot split, no split needed *done*
            //regular number dot split, split needed *done*
            // 10 *done*
            // 11 *done*
            // 12 *done*
            //regular numbers do not explode
            //magnitude *done*
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
            
            //pair number
                //constructor *done*
                //pair number dot split, contains a regular number *done*
                    //no split needed *done*
                    //left is a regular number with a split needed *done*
                    //right is a regular number with a split needed *done*
                //split pair containing another pair *done*
                    //left subpair splits *done*
                    //right subpair splits *done*
                    //no subpair splits *done*
                //split should stop at the first split found *done*
                    //two regular numbers *done*
                    //left regular number, right pair *done*
                    //right regular number, right pair *done*
                    //two pairs *done*

                //explode pair depth less than four *done*
                //explode pair with a depth greater than four *done*
                    //example 1 *done*
                    //example 2 *done*
                    //example 3 *done*
                    //example 4 *done*
                //magnitude

                //reduce (don't need to test reduce directly on a regular number — outer ones will always be pairs)
                    //check for explodes, then check for splits, then loop until stable

                //add
        }
    }

    

    //parse //use a stack based parser like day 10
    //part 1

    internal fun pairOf(left: Int, right:Int) = PairNumber(RegularNumber(left), RegularNumber(right))
    internal fun pairOf(left: Int, right:PairNumber) = PairNumber(RegularNumber(left), right)
    internal fun pairOf(left: PairNumber, right:Int) = PairNumber(left, RegularNumber(right))
    internal fun pairOf(left: PairNumber, right:PairNumber) = PairNumber(left, right)
    internal fun pairThatSplits() = pairOf(10, 1)
    internal fun pairThatDoesNotSplit() = pairOf(1, 1)
    internal val numberBigEnoughToSplit = 12
    internal val numberThatDoesNotNeedToSplit = 9
}
