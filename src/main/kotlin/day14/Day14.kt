package day14

import utils.readInput

fun main() {
    val input = readInput("Day14")
    println(part1(input))
    println(part2(input))
}

fun part1(input: List<String>): Int {
    val polymerTemplate = parsePolymerTemplate(input)
    val rules = parsePairInsertionRules(input)
    (1..10).forEach { _ -> polymerTemplate.step(rules) }
    val counts = polymerTemplate.counts().values
    return counts.maxOrNull()!! - counts.minOrNull()!!
}

fun part2(input: List<String>): Int {
    return input.size
}

internal typealias PairInsertionRules = Map<PolymerPair, PolymerInsertion>

@JvmInline
internal value class PolymerPair(internal val pair: String) {
    init { require(pair.length == 2) { "insertion pairs must have length 2" } }
}

@JvmInline
internal value class PolymerInsertion(internal val element: Char)

internal data class PolymerTemplate(private val polymer: StringBuilder) {
    internal constructor(polymerTemplate: String) : this(StringBuilder(polymerTemplate))
    override fun toString(): String = polymer.toString()
    fun step(rules: PairInsertionRules) {
        val matches = findWhereRulesMatch(rules)

        //find the additional elements to insert
        val insertionsToPerform = matches
            .map { Pair(it.first, rules[it.second]!!) } //we know the values are in the map because we matched based on the keys

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
    private fun findWhereRulesMatch(rules: PairInsertionRules): List<Pair<Int, PolymerPair>> {
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

    internal fun counts() : Map<Char, Int> {
        return polymer.groupingBy { it }.eachCount() //do this against the StringBuilder to avoid a needless copy
    }
}

internal fun parsePolymerTemplate(input: List<String>): PolymerTemplate {
    return PolymerTemplate(input.first())
}

private  val pairInsertionRuleRegex = """(\w\w) -> (\w)""".toRegex()
internal fun parsePairInsertionRules(input: List<String>): PairInsertionRules {
    return input
        .mapNotNull { pairInsertionRuleRegex.matchEntire(it) }
        .map { it.destructured }
        .associate { (polymerPair, polymerInsertion) -> PolymerPair(polymerPair) to PolymerInsertion(polymerInsertion.first()) }
}