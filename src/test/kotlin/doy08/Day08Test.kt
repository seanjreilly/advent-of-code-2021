package day08

import org.junit.jupiter.api.Test

class Day08Test {

    val sampleInput = """
        be cfbegad cbdgef fgaecd cgeb fdcge agebfd fecdb fabcd edb | fdgacbe cefdb cefbgd gcbe
        edbfga begcd cbg gc gcadebf fbgde acbgfd abcde gfcbed gfec | fcgedb cgb dgebacf gc
        fgaebd cg bdaec gdafb agbcfd gdcbef bgcad gfac gcb cdgabef | cg cg fdcagb cbg
        fbegcd cbd adcefb dageb afcb bc aefdc ecdab fgdeca fcdbega | efabcd cedba gadfec cb
        aecbfdg fbg gf bafeg dbefa fcge gcbea fcaegb dgceab fcbdga | gecf egdcabf bgf bfgea
        fgeab ca afcebg bdacfeg cfaedg gcfdb baec bfadeg bafgc acf | gebdcfa ecba ca fadegcb
        dbcfg fgd bdegcaf fgec aegbdf ecdfab fbedc dacgb gdcebf gf | cefg dcbef fcge gbcadfe
        bdfegc cbegaf gecbf dfcage bdacg ed bedf ced adcbefg gebcd | ed bcgafe cdgba cbgef
        egadfb cdbfeg cegd fecab cgb gbdefca cg fgcdab egfdb bfceg | gbdfcae bgc cg cgb
        gcafb gcf dcaebfg ecagb gf abcdeg gaef cafbge fdbac fegbdc | fgae cfgab fg bagce
    """.trimIndent().lines()

    @Test
    fun `part1 should return the number of 1s 4s and 7s and 8s after the pipe`() {
        //one is a word 2 letters long
        //four is a word 4 letters long
        //seven is a word 3 letters long
        //eight is a word 7 letters long

        val result:Int = part1(sampleInput)

        assert (result == 26)
    }

    @Test
    fun `parseEncodedLine should return a set of inputs and a set of outputs`() {
        val rawLine = sampleInput[0]

        val result: EncodedLine = parseEncodedLine(rawLine)

        assert(result.inputValues == listOf(
            "be".toSet(),
            "cfbegad".toSet(),
            "cbdgef".toSet(),
            "fgaecd".toSet(),
            "cgeb".toSet(),
            "fdcge".toSet(),
            "agebfd".toSet(),
            "fecdb".toSet(),
            "fabcd".toSet(),
            "edb".toSet()
        ))

        assert(result.outputValues == listOf(
            "fdgacbe".toSet(),
            "cefdb".toSet(),
            "cefbgd".toSet(),
            "gcbe".toSet()
        ))
    }

    private fun String.toSet() : Set<Char> = this.toCharArray().toSet()
}