package day14

import utils.readInput

fun main() {
    val input = readInput("Day14")
    println(part1(input))
    println(part2(input))
}

fun part1(input: List<String>): Int {
    val polymerTemplate = PolymerTemplate(input)
    val rules = parsePairInsertionRules(input)
    val result = polymerTemplate.step(rules, 10)
    val counts = result.groupingBy { it }.eachCount().values
    return counts.maxOrNull()!! - counts.minOrNull()!!
}

fun part2(input: List<String>): Long {
    val polymerTemplate = FastPolymerTemplate(input)
    val rules = parsePairInsertionRules(input)
    val counts = polymerTemplate.step(rules, 40).values
    return counts.maxOrNull()!! - counts.minOrNull()!!
}

internal typealias PairInsertionRules = Map<PolymerPair, PolymerInsertion>

@JvmInline
internal value class PolymerPair(internal val pair: String) {
    init { require(pair.length == 2) { "insertion pairs must have length 2" } }
}

@JvmInline
internal value class PolymerInsertion(internal val element: Char)

internal data class PolymerTemplate(private val input: String) {
    internal constructor(input: List<String>) : this(input.first())

    fun step(rules: PairInsertionRules, invocations: Int) : String {
        val polymer = StringBuilder(input)
        (1..invocations).forEach { _ -> stepInternal(rules, polymer) }
        return polymer.toString()
    }

    private fun stepInternal(rules: PairInsertionRules, polymer: StringBuilder) {
        val matches = findWhereRulesMatch(rules, polymer)

        //find the additional elements to insert
        val insertionsToPerform = matches
            .map {
                Pair(
                    it.first,
                    rules[it.second]!!
                )
            } //we know the values are in the map because we matched based on the keys

        //insert the elements starting from the end of the polymer
        // (this means that we don't need to track updated indices for the matches as we insert elements)
        insertionsToPerform.reversed().forEach { (index, insertion) ->
            polymer.insert(index + 1, insertion.element) //insert in the middle of the matched pair
        }
    }

    /*
        We use this custom traversal instead of a stream because it iterates through the polymer
        only once even though there are many strings to find. This will be important (O(n) or thereabouts per invocation)
        when the polymer gets longer.
     */
    private fun findWhereRulesMatch(rules: PairInsertionRules, polymer: StringBuilder): List<Pair<Int, PolymerPair>> {
        val pairsToFind = rules.keys.map { it.pair }
        val matches = mutableListOf<Pair<Int, String>>()

        //find all matches in the string
        var startIndex: Int
        var pairFound = polymer.findAnyOf(pairsToFind, 0)

        while (pairFound != null) {
            matches.add(pairFound)
            startIndex = pairFound.first + 1
            pairFound = polymer.findAnyOf(pairsToFind, startIndex)
        }
        return matches.map { Pair(it.first, PolymerPair(it.second)) }
    }
}

private  val pairInsertionRuleRegex = """(\w\w) -> (\w)""".toRegex()
internal fun parsePairInsertionRules(input: List<String>): PairInsertionRules {
    return input
        .mapNotNull { pairInsertionRuleRegex.matchEntire(it) }
        .map { it.destructured }
        .associate { (polymerPair, polymerInsertion) -> PolymerPair(polymerPair) to PolymerInsertion(polymerInsertion.first()) }
}

/*
    FastPolymerTemplate doesn't produce the actual polymer.
    It just returns how many times each element is present (which is all we need to solve the problem)
 */
internal class FastPolymerTemplate(private val input: String) {
    constructor(input: List<String>) : this(input.first())

    fun step(rules: PairInsertionRules, invocations: Int): Map<Char, Long> {
        //find all the 2 char pairs in the input and count how many times each one occurs
        var state : Map<PolymerPair, Long> = input
            .windowed(2, 1)
            .groupingBy { it }
            .eachCount()
            .mapKeys { PolymerPair(it.key) }
            .mapValues { it.value.toLong() }

        //for each invocation
        (1 .. invocations).forEach { _ ->
            val newState = mutableMapOf<PolymerPair, Long>()
            //for each rule that matches
            rules.keys.intersect(state.keys).forEach { match ->
                val insertion = rules[match]!!
                val oldCount = state[match]!!

                //split the old pair into 2 new pairs
                val firstNewPair = PolymerPair(String(charArrayOf(match.pair.first(), insertion.element)))
                val secondNewPair = PolymerPair(String(charArrayOf(insertion.element, match.pair.last())))

                //increment (or insert) counts for each
                newState[firstNewPair] = (newState[firstNewPair] ?: 0L) + oldCount
                newState[secondNewPair] = (newState[secondNewPair] ?: 0L) + oldCount
            }
            state = newState
        }

        //turn a count of pairs into a count of elements
        val charCount =  state
            .flatMap { (it, count) -> listOf(it.pair.first() to count, it.pair.last() to count) }
            .map { Pair(it.first, it.second) }
            .groupBy({ it.first }, { it.second })
            .mapValues { (_, values) -> values.sum() }
            .toMutableMap()


        //adjust the character count for the first and last characters in the original input
        val first = input.first()
        val last = input.last()
        charCount[first] = charCount[first]!! + 1L
        charCount[last] = charCount[last]!! + 1L

        return charCount.mapValues { (_, it) -> it / 2 } //everything is done in pairs, so the output needs to be halved
    }
}