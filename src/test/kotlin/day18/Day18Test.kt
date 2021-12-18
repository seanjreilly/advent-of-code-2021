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

//            @Test
//            fun `split`() {
//
//            }
            
            //pair number
            //constructor *done*
            //pair number dot split, contains a regular number
                //no split needed
                //split needed
            //split pair containing another pair
                //subpair splits
                //subpair does not split

            //explode pair depth less than four
            //explode pair with a depth greater than four
            //example 1
            //example 2
            //example 3
            //example 4
            //example 5
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
}
