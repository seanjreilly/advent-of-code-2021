package day23

import day23.AmphipodType.*
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

class Day23Test {
    @Nested
    inner class AmphipodTypeTest {
        @Test
        fun `values should return 4 types of amphipod`() {
            val types:Array<AmphipodType> = AmphipodType.values()

            assert(types.size == 4)
            assert(Amber in types)
            assert(Bronze in types)
            assert(Copper in types)
            assert(Desert in types)
        }

        @Test
        fun `each amphipod type has a movement cost`() {
            assert(Amber.movementCost == 1)
            assert(Bronze.movementCost == 10)
            assert(Copper.movementCost == 100)
            assert(Desert.movementCost == 1000)
        }

        @Test
        fun `no two amphipod types have the same movement cost`() {
            val movementCostCounts = AmphipodType.values()
                .groupingBy { it.movementCost }
                .eachCount()

            assert(movementCostCounts.maxOf { it.value } == 1)
        }
    }

    @Nested
    inner class ConfigurationTest {
        @Test
        fun `isFinished should return false given a configuration that is not complete`() {
            val configuration = buildConfiguration(
                0 to Desert,
                1 to Desert,
                2 to Copper,
                3 to Copper,
                4 to Bronze,
                5 to Bronze,
                6 to Amber,
                7 to Amber,
            )

            assert(!configuration.isFinished())
        }

        @Test
        fun `isFinished should return false given a configuration that is almost complete`() {
            val configuration = buildConfiguration(
                0 to Amber,
                1 to Amber,
                2 to Bronze,
                3 to Bronze,
                4 to Copper,
                5 to Copper,
                6 to Desert,
                18 to Desert, //one amphipod still in the hallway
            )

            assert(!configuration.isFinished())
        }

        @Test
        fun `isFinished should return true given a configuration that is complete`() {
            val configuration = buildConfiguration(
                0 to Amber,
                1 to Amber,
                2 to Bronze,
                3 to Bronze,
                4 to Copper,
                5 to Copper,
                6 to Desert,
                7 to Desert,
            )

            assert(configuration.isFinished())
        }

        @Test
        fun `nextMoves should return multiple moves where possible`() {
            val configuration = buildConfiguration(
                0 to Amber,
                2 to Bronze,
                4 to Copper,
                6 to Desert,
                1 to Desert,
                3 to Copper,
                5 to Bronze,
                7 to Amber,
            )

            val results:Collection<Pair<Configuration, Int>> = configuration.nextMoves()

            assert(results.size == 28) //each of the amphipods in the top row can move to 7 hallway places
        }

        @Test
        fun `nextMoves should allow amphipods of the correct type to empty an empty room`() {
            val configuration = buildConfiguration(
                0 to null,
                1 to null,
                2 to null,
                2 to Bronze,
                4 to Copper,
                5 to Copper,
                6 to Desert,
                7 to Desert,
                8 to Amber,
                9 to Amber,
                13 to Bronze
            )

            val results = configuration.nextMoves()

            assert(results.map{ it.first.positions }.any { it[0] == Amber }) { "legal to move to top of A, if Amber" }
            assert(results.map{ it.first.positions }.any { it[1] == Amber }) { "legal to move to bottom of A, if Amber" }
            assert(!results.map{ it.first.positions }.any { it[0] == Bronze || it[1] == Bronze }) { "not legal to A, if Bronze" }
        }
        
        @Test
        fun `nextMoves should allow an amphipod of the correct type to enter a partially full room with the correct amphipod in the other room space`() {
            val configuration = buildConfiguration(
                0 to null,
                1 to Amber,
                2 to null,
                2 to Bronze,
                4 to Copper,
                5 to Copper,
                6 to Desert,
                7 to Desert,
                8 to Amber,
                18 to Bronze
            )

            val results = configuration.nextMoves()

            assert(results.map{ it.first.positions }.any { it[0] == Amber }) { "legal to move to top of A, if Amber" }
            assert(results.map{ it.first.positions }.any { it[2] == Bronze }) { "legal to move to top of B, if Bronze" }
        }

        @Test
        fun `nextMoves should not allow an amphipod of the right typ to enter a partially full room while an correct amphipod is in the room`() {
            val configuration = buildConfiguration(
                0 to null,
                1 to Bronze,
                2 to null,
                3 to Amber,
                4 to Copper,
                5 to Copper,
                6 to Desert,
                7 to Desert,
                8 to Amber,
                18 to Bronze
            )

            val results = configuration.nextMoves()

            assert(!results.map{ it.first.positions }.any { it[0] == Amber }) { "not legal to move to top of A until other one clears" }
            assert(!results.map{ it.first.positions }.any { it[2] == Bronze }) { "not legal to move to top of B until other one clears" }
        }

        @Test
        fun `nextMoves should not allow any moves when blocked`() {
            val configuration = buildConfiguration(
                9 to Copper,
                11 to Copper,
                13 to Desert,
                15 to Desert,
                6 to Bronze,
                7 to Bronze,
                17 to Amber,
                18 to Amber
            )

            val results = configuration.nextMoves()

            assert(results.isEmpty())
        }

        @Test
        fun `nextMoves should return the next move and the cost of the next move`() {
            val configuration = buildConfiguration(
                0 to Amber,
                1 to Amber,
                2 to Bronze,
                3 to Bronze,
                4 to Copper,
                5 to Copper,
                7 to Desert,
                18 to Desert,
            )

            val results = configuration.nextMoves()

            assert(results.first { it.first.isFinished() }.second == 3000)
        }
    }

    @Test
    fun `parse should return a map of amphipods to initial positions`() {
        val input = """
            #############
            #...........#
            ###B#C#B#D###
              #A#D#C#A#
              #########
        """.trimIndent().lines()

        val configuration:Configuration = parse(input)
        assert(configuration.positions.size == 19)
        assert(configuration.positions[0] == Bronze)
        assert(configuration.positions[1] == Amber)
        assert(configuration.positions[2] == Copper)
        assert(configuration.positions[3] == Desert)
        assert(configuration.positions[4] == Bronze)
        assert(configuration.positions[5] == Copper)
        assert(configuration.positions[6] == Desert)
        assert(configuration.positions[7] == Amber)
        (8 until 19).forEach {
            assert(configuration.positions[it] == null)
        }
    }

    @Test
    fun `part1 should parse the model and determine the cheapest path to a completed position`() {
        val input = """
            #############
            #...........#
            ###B#C#B#D###
              #A#D#C#A#
              #########
        """.trimIndent().lines()

        assert(part1(input) == 12521)
    }

    private fun buildConfiguration(vararg setPositions: Pair<Int, AmphipodType?>) : Configuration {
        val positions = arrayOfNulls<AmphipodType>(19)
        setPositions.forEach { positions[it.first] = it.second }
        return Configuration(positions)
    }
}