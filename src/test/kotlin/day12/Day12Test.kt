package day12

import org.junit.jupiter.api.Test

class Day12Test {
    private val smallInput = """
        start-A
        start-b
        A-c
        A-b
        b-d
        A-end
        b-end
    """.trimIndent().lines()

    @Test
    fun `parseGraph should return an undirected graph given a list of edges`() {
        val graph : Graph = parseGraph(smallInput)

        //graph should have 6 vertices
        assert(graph.keys.size == 6)
        assert(graph.values.flatten().distinct().size == 6)

        //check edges
        assert(graph["start"] == setOf("A", "b"))
        assert(graph["A"] == setOf("b", "c", "start", "end"))
        assert(graph["b"] == setOf("A", "d", "start", "end"))
        assert(graph["c"] == setOf("A"))
        assert(graph["d"] == setOf("b"))
        assert(graph["end"] == setOf("A", "b"))
    }

    @Test
    fun `isSmallCave should return true if the cave name is lower case and false otherwise`() {
        assert(!isSmallCave("A"))
        assert(isSmallCave("b"))
        assert(isSmallCave("c"))
        assert(isSmallCave("d"))
        assert(isSmallCave("start"))
        assert(isSmallCave("end"))
    }

    @Test
    fun `findDistinctPaths should return the distinct paths that start at 'start', end at 'end', and pass a validity function`() {
        val graph : Graph = parseGraph(smallInput)

        val paths: Set<Path> = findDistinctPaths(graph, Path::neverVisitsASmallCaveMoreThanOnce)

        val expectedResults = """
            start,A,b,A,c,A,end
            start,A,b,A,end
            start,A,b,end
            start,A,c,A,b,A,end
            start,A,c,A,b,end
            start,A,c,A,end
            start,A,end
            start,b,A,c,A,end
            start,b,A,end
            start,b,end
        """.trimIndent().lines().map { line -> line.trim().split(',') }.toSet()

        assert(expectedResults == paths)
    }

    @Test
    fun `part1 should return the number of distinct paths through the cave system, never visiting any small cave more than once`() {
        assert(part1(smallInput) == 10)
    }

    @Test
    fun `part1 should work with a medium example, never visiting any small cave more than once`() {
        val input = """
            dc-end
            HN-start
            start-kj
            dc-start
            dc-HN
            LN-dc
            HN-end
            kj-sa
            kj-HN
            kj-dc
        """.trimIndent().lines()

        assert(part1(input) == 19)
    }

    @Test
    fun `part1 should work with a large example, never visiting any small cave more than once`() {
        val input = """
            fs-end
            he-DX
            fs-he
            start-DX
            pj-DX
            end-zg
            zg-sl
            zg-pj
            pj-he
            RW-he
            fs-DX
            pj-RW
            zg-RW
            start-pj
            he-WI
            zg-he
            pj-fs
            start-RW
        """.trimIndent().lines()

        assert(part1(input) == 226)
    }

    @Test
    fun `part2 should return the number of distinct paths through the cave system`() {
        assert(part2(smallInput) == 36)
    }

    @Test
    fun `part2 should work with a medium example, visiting a single small cave more than once`() {
        val input = """
            dc-end
            HN-start
            start-kj
            dc-start
            dc-HN
            LN-dc
            HN-end
            kj-sa
            kj-HN
            kj-dc
        """.trimIndent().lines()

        assert(part2(input) == 103)
    }

    @Test
    fun `part2 should work with a large example, visiting a single small cave more than once`() {
        val input = """
            fs-end
            he-DX
            fs-he
            start-DX
            pj-DX
            end-zg
            zg-sl
            zg-pj
            pj-he
            RW-he
            fs-DX
            pj-RW
            zg-RW
            start-pj
            he-WI
            zg-he
            pj-fs
            start-RW
        """.trimIndent().lines()

        assert(part2(input) == 3509)
    }
}