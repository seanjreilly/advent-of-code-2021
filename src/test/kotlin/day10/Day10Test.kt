package day10

import org.junit.jupiter.api.Test

class Day10Test {
    val input = """
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
    fun `part1 should parse chunks, find the score for the first illegal character on each line, and return the sum of the scores`() {
        assert(part1(input) == 26397)
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
    fun `parseChunks should return Incomplete given an incomplete line`() {
        val incompleteLines = listOf(
            "(",
            "[",
            "{",
            "<",
            "((",
            "({",
            "({[<",
            "()("
        )

        incompleteLines.forEach {
            assert(parseChunks(it) == ParseResult.Incomplete)
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
}