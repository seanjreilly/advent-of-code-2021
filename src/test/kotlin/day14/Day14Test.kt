package day14

import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

class Day14Test {
    private val sampleInput = """
        NNCB

        CH -> B
        HH -> N
        CB -> H
        NH -> C
        HB -> C
        HC -> B
        HN -> C
        NN -> C
        BH -> H
        NC -> B
        NB -> B
        BN -> B
        BB -> N
        BC -> B
        CC -> N
        CN -> C
    """.trimIndent().lines()
    
    @Test
    fun `parsePolymerTemplate should return the polymer template from the input`() {
        val polymerTemplate : PolymerTemplate = parsePolymerTemplate(sampleInput)

        assert(polymerTemplate.toString() == "NNCB")
    }

    @Test
    fun `parsePairInsertionRules should return a set of pair insertion rules`() {
        val pairInsertionRules: PairInsertionRules = parsePairInsertionRules(sampleInput)

        assert(pairInsertionRules.size == 16)
        assert(pairInsertionRules[PolymerPair("CH")] == PolymerInsertion('B'))
        assert(pairInsertionRules[PolymerPair("HH")] == PolymerInsertion('N'))
        assert(pairInsertionRules[PolymerPair("CB")] == PolymerInsertion('H'))
        assert(pairInsertionRules[PolymerPair("NH")] == PolymerInsertion('C'))
        assert(pairInsertionRules[PolymerPair("HB")] == PolymerInsertion('C'))
        assert(pairInsertionRules[PolymerPair("HC")] == PolymerInsertion('B'))
        assert(pairInsertionRules[PolymerPair("HN")] == PolymerInsertion('C'))
        assert(pairInsertionRules[PolymerPair("NN")] == PolymerInsertion('C'))
        assert(pairInsertionRules[PolymerPair("BH")] == PolymerInsertion('H'))
        assert(pairInsertionRules[PolymerPair("NC")] == PolymerInsertion('B'))
        assert(pairInsertionRules[PolymerPair("NB")] == PolymerInsertion('B'))
        assert(pairInsertionRules[PolymerPair("BN")] == PolymerInsertion('B'))
        assert(pairInsertionRules[PolymerPair("BB")] == PolymerInsertion('N'))
        assert(pairInsertionRules[PolymerPair("BC")] == PolymerInsertion('B'))
        assert(pairInsertionRules[PolymerPair("CC")] == PolymerInsertion('N'))
        assert(pairInsertionRules[PolymerPair("CN")] == PolymerInsertion('C'))
    }

    @Nested
    inner class PolymerTemplateTest {

        @Test
        fun `step should simultaneously process all pair insertion rules that match sections in the polymer`() {
            val polymerTemplate = parsePolymerTemplate(sampleInput)
            val pairInsertionRules = parsePairInsertionRules(sampleInput)

            polymerTemplate.step(pairInsertionRules)

            assert(polymerTemplate.toString() == "NCNBCHB")
        }

        @Test
        fun `step should not consider new pairs created in a step during that step`() {
            val polymerTemplate = PolymerTemplate("NN")
            val rules = mapOf(
                PolymerPair("NN") to PolymerInsertion('C'),
                PolymerPair("CN") to PolymerInsertion('X') //this rule must NOT fire
            )

            polymerTemplate.step(rules)

            assert(!polymerTemplate.toString().contains('X'))
        }

        @Test
        fun `step should apply each rule multiple times in a single invocation where necessary`() {
            val polymerTemplate = PolymerTemplate("NNN")
            val rules = mapOf(
                PolymerPair("NN") to PolymerInsertion('C')
            )

            polymerTemplate.step(rules)

            assert(polymerTemplate.toString() == "NCNCN")
        }

        @Test
        fun `step should return expected results after 2 invocations`() {
            val polymerTemplate = parsePolymerTemplate(sampleInput)
            val rules = parsePairInsertionRules(sampleInput)

            (1..2).forEach { _ -> polymerTemplate.step(rules) }

            assert(polymerTemplate.toString() == "NBCCNBBBCBHCB")
        }

        @Test
        fun `step should return expected results after 3 invocations`() {
            val polymerTemplate = parsePolymerTemplate(sampleInput)
            val rules = parsePairInsertionRules(sampleInput)

            (1..3).forEach { _ -> polymerTemplate.step(rules) }

            assert(polymerTemplate.toString() == "NBBBCNCCNBBNBNBBCHBHHBCHB")
        }

        @Test
        fun `step should return expected results after 4 invocations`() {
            val polymerTemplate = parsePolymerTemplate(sampleInput)
            val rules = parsePairInsertionRules(sampleInput)

            (1..4).forEach { _ -> polymerTemplate.step(rules) }

            assert(polymerTemplate.toString() == "NBBNBNBBCCNBCNCCNBBNBBNBBBNBBNBBCBHCBHHNHCBBCBHCB")
        }

        @Test
        fun `step should produce a polymer of the expected length after 10 invocations`() {
            val polymerTemplate = parsePolymerTemplate(sampleInput)
            val rules = parsePairInsertionRules(sampleInput)

            (1..10).forEach { _ -> polymerTemplate.step(rules) }

            assert(polymerTemplate.toString().length == 3073)
        }
    }

    @Test
    fun `part1 should process the polymer for 10 steps, find the elements that occur most and least frequently, and return the difference of their counts`() {
        assert(part1(sampleInput) == 1588)
    }
}