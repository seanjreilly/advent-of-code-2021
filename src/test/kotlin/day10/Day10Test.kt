package day10

import org.junit.jupiter.api.Test

class Day10Test {
    private val input = """
        [({(<(())[]>[[{[]{<()<>>
        [(()[<>])]({[<{<<[]>>(
        {([(<{}[<>[]}>{[]{[(<()>
        (((({<>}<{<{<>}{[]{[]{}
        [[<[([]))<([[{}[[()]]]
        [{[{({}]{}}([{[{{{}}([]
        {<[[]]>}<{[{[{[]{()[[[]
        [<(<(<(<{}))><([]([]()
        <{([([[(<>()){}]>(<<{{
        <{([{{}}[<[[[<>{}]]]>[]]
    """.trimIndent().lines()

    @Test
    fun `part1 should parse chunks, find corrupted lines, find the score for the first illegal character on each line, and return the sum of the scores`() {
        assert(part1(input) == 26397)
    }


    @Test
    fun `part2 should parse chunks, find the score for the completion string for each incomplete line, sort the scores and return the middle one`() {
        assert(part2(input) == 288957L)
    }

    @Test
    fun `parseChunks should return Legal given a complete line`() {
        val completeLines = listOf(
            "()",
            "[]",
            "{}", //extra addition not included in the problem description
            "<>", //extra addition not included in the problem description
            "([])",
            "{()()()}",
            "<([{}])>",
            "[<>({}){}[([])<>]]",
            "(((((((((())))))))))"
        )

        completeLines.forEach {
            assert(parseChunks(it) == ParseResult.Complete)
        }
    }

    @Test
    fun `parseChunks should return Incomplete (and report the missing characters) given an incomplete line`() {
        val incompleteLines = listOf(
            Pair("(", ")"),
            Pair("[", "]"),
            Pair("{", "}"),
            Pair("<", ">"),
            Pair("((", "))"),
            Pair("({", "})"),
            Pair("({[<", ">]})"),
            Pair("()(", ")")
        )

        incompleteLines.forEach { (line, expectedCompletionString) ->
            val result = parseChunks(line)

            assert(result is ParseResult.Incomplete)
            assert((result as ParseResult.Incomplete).completionString == expectedCompletionString)
        }
    }

    @Test
    fun `parseChunks should return Corrupted (and report the expected and actual character) given a corrupted line`() {
        data class Input(val line: String, val expected: Char, val actual: Char)

        val corruptedLines = listOf(
            Input("{([(<{}[<>[]}>{[]{[(<()>", ']', '}'),
            Input("[[<[([]))<([[{}[[()]]]", ']', ')'),
            Input("[{[{({}]{}}([{[{{{}}([]", ')', ']'),
            Input("[<(<(<(<{}))><([]([]()", '>', ')'),
            Input("<{([([[(<>()){}]>(<<{{", ']', '>')
        )

        corruptedLines.forEach { input ->
            val result = parseChunks(input.line)

            assert(result is ParseResult.Corrupted)
            if (result is ParseResult.Corrupted) { //need this for a smart cast even though there's already an assert statement. Boo.
                assert(result.expected == input.expected)
                assert(result.actual == input.actual)
            }
        }
    }

    @Test
    fun `calculateCompletionStringScore should start at zero, multiply by 5 and add the score for each character in a completion string`() {
        val completionStrings = listOf(
            Pair("}}]])})]", 288957L),
            Pair(")}>]})", 5566L),
            Pair("}}>}>))))", 1480781L),
            Pair("]]}}]}]}>", 995444L),
            Pair("])}>", 294L)
        )

        completionStrings.forEach { (completionString, expectedScore) ->
            assert(calculateCompletionStringScore(completionString) == expectedScore)
        }
    }

    @Test
    fun `calculateCompletionStringScore should work for long completion strings`() {
        val input = "]]])>)})}>}})}>"

        val result = calculateCompletionStringScore(input)
        assert(result > 0L)
        assert(result > Int.MAX_VALUE)
    }
}

