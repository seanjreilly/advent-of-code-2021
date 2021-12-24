package day23

import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import java.lang.IllegalArgumentException

class Day23Test {
    @Nested
    inner class AmphipodTypeTest {
        @Test
        fun `values should return 4 types of amphipod`() {
            val types:Array<AmphipodType> = AmphipodType.values()

            assert(types.size == 4)
            assert(AmphipodType.Amber in types)
            assert(AmphipodType.Bronze in types)
            assert(AmphipodType.Copper in types)
            assert(AmphipodType.Desert in types)
        }

        @Test
        fun `each amphipod type has a movement cost`() {
            assert(AmphipodType.Amber.movementCost == 1)
            assert(AmphipodType.Bronze.movementCost == 10)
            assert(AmphipodType.Copper.movementCost == 100)
            assert(AmphipodType.Desert.movementCost == 1000)
        }

        @Test
        fun `no two ampipod types have the same movement cost`() {
            val movementCostCounts = AmphipodType.values()
                .groupingBy { it.movementCost }
                .eachCount()

            assert(movementCostCounts.maxOf { it.value } == 1)
        }
    }

    @Nested
    inner class AmphipodTest {
        @Test
        fun `Amphipods have a type and an id`() {
            val expectedType = AmphipodType.Copper
            val expectedId = 1

            val amphipod = Amphipod(expectedId, expectedType)

            assert(amphipod.id == expectedId)
            assert(amphipod.type == expectedType)
        }
    }

    internal abstract inner class SpaceTest<T:Space> {
        protected abstract val instance:T

        @Test
        fun `has an id`() {
            assert(instance.id > 0)
        }

        @Test
        fun `has a set of potential moves`() {
            val moves = getAtLeastOneValidMoveForSpace()

            instance.moves = moves

            assert(instance.moves == moves)
        }

        protected abstract fun getAtLeastOneValidMoveForSpace(): Set<PotentialMove>
    }

    @Nested
    internal inner class RoomSpaceTest : SpaceTest<RoomSpace>() {
        override val instance = RoomSpace(1, AmphipodType.Bronze)

        @Test
        fun `type returns the type of the RoomSpace`() {
            assert(instance.type == AmphipodType.Bronze)
        }

        override fun getAtLeastOneValidMoveForSpace(): Set<PotentialMove> {
            //up and around the corner
            val destination = HallwaySpace(3)
            val intermediate = HallwaySpace(2)
            return setOf(PotentialMove(instance, setOf(intermediate), destination))
        }
    }

    @Nested
    internal inner class HallwaySpaceTest : SpaceTest<HallwaySpace>() {
        override val instance = HallwaySpace(12)
        override fun getAtLeastOneValidMoveForSpace(): Set<PotentialMove> {
            //across and down into a room
            val destination = RoomSpace(1, AmphipodType.Amber)
            val intermediateSpaces = setOf(
                RoomSpace(2, AmphipodType.Amber), //other space in the same room
                HallwaySpace(6), //space just above the room
                HallwaySpace(7), //other hallway space
            )
            return setOf(PotentialMove(instance, intermediateSpaces, destination))
        }
    }

    @Nested
    inner class PotentialMoveTest {
        @Test
        fun `PotentialMove has a source space, a destination space, and a set of zero or more intermediate spaces`() {
            //model a route from the bottom of a room up and just around a corner
            val source:Space = RoomSpace(1, AmphipodType.Desert)
            val destination:Space = HallwaySpace(4)
            val intermediateSpaces = setOf(RoomSpace(2, AmphipodType.Desert), HallwaySpace(3))

            val potentialMove = PotentialMove(source, intermediateSpaces, destination)

            assert(potentialMove.source == source)
            assert(potentialMove.destination == destination)
            assert(potentialMove.intermediateSpaces == intermediateSpaces)
        }

        @Test
        fun `constructor should fail given a route from a space to itself`() {
            val source:Space = RoomSpace(1, AmphipodType.Desert)
            val intermediateSpaces = setOf(HallwaySpace(2))

            val expectedException = assertThrows<IllegalArgumentException> { PotentialMove(source, intermediateSpaces, source) }

            assert("cannot build a route from a space to itself" in expectedException.message!!)
        }

        @Test
        fun `constructor should fail given a route from a room space to another room space of the same type`() {
            val source:Space = RoomSpace(1, AmphipodType.Desert)
            val destination:Space = RoomSpace(2, AmphipodType.Desert)

            val expectedException = assertThrows<IllegalArgumentException> { PotentialMove(source, emptySet(), destination) }

            assert("cannot build a route from a room to another room of the same type" in expectedException.message!!)
        }

        @Test
        fun `constructor should fail given a route from a hallway space to another hallway space`() {
            val source:Space = HallwaySpace(6)
            val destination:Space = HallwaySpace(7)

            val expectedException = assertThrows<IllegalArgumentException> { PotentialMove(source, emptySet(), destination) }

            assert("amphipods only stop in rooms once they are in a hallway" in expectedException.message!!)
        }

        @Test
        fun `constructor should fail given a route from a room space to a hallway space without at least one intermediate hallway space`() {
            val source:Space = RoomSpace(6, AmphipodType.Amber)
            val destination:Space = HallwaySpace(7)

            val expectedException = assertThrows<IllegalArgumentException> { PotentialMove(source, emptySet(), destination) }

            assert("amphipods only stop in hallway spaces that don't block rooms" in expectedException.message!!)
        }

        @Test
        fun `constructor should fail given a route from a room space to a hallway space with only an intermediate room space`() {
            //model moving from the bottom space of a room to just outside the door
            val source:Space = RoomSpace(5, AmphipodType.Amber)
            val destination:Space = HallwaySpace(7)
            val intermediateSpace = RoomSpace(6, AmphipodType.Amber)

            val expectedException = assertThrows<IllegalArgumentException> { PotentialMove(source, setOf(intermediateSpace), destination) }

            assert("amphipods only stop in hallway spaces that don't block rooms" in expectedException.message!!)
        }

        @Test
        fun `distance should return the number of intermediate points plus one`() {
            val source:Space = RoomSpace(1, AmphipodType.Desert)
            val destination:Space = HallwaySpace(4)
            val intermediateSpaces = setOf(RoomSpace(2, AmphipodType.Desert), HallwaySpace(3))

            val potentialMove = PotentialMove(source, intermediateSpaces, destination)

            assert(potentialMove.distance == 3)
        }
    }

    @Nested
    inner class SpaceMapTest {

        @Test
        fun `map should have 7 hallway spaces`() {
            val spaces: List<HallwaySpace> = SpaceMap.hallwaySpaces

            assert(spaces.size == 7)
            assert(spaces.toSet().size == 7) { "all hallway spaces are unique" }
        }

        @Test
        fun `map should have 8 room spaces, 2 of each type`() {
            val spaces: List<RoomSpace> = SpaceMap.roomSpaces

            assert(spaces.size == 8)
            assert(spaces.toSet().size == 8) { "all room spaces are unique" }

            assert(spaces.count { it.type == AmphipodType.Amber } == 2)
            assert(spaces.count { it.type == AmphipodType.Bronze } == 2)
            assert(spaces.count { it.type == AmphipodType.Copper } == 2)
            assert(spaces.count { it.type == AmphipodType.Desert } == 2)
        }

        @Test
        fun `every space should have a different id`() {
            val allSpaces: List<Space> = SpaceMap.hallwaySpaces + SpaceMap.roomSpaces + SpaceMap.intermediateHallwaySpaces

            assert(allSpaces.groupingBy { it.id }.eachCount().maxOf { it.value } == 1)
        }

        @Test
        fun `intermediate rooms should have no potential moves`() {
            SpaceMap.intermediateHallwaySpaces.forEach {
                assert(it.moves.isEmpty())
            }
        }

        @Test
        fun `every hallway space should have a journey to every room space`() {
            SpaceMap.hallwaySpaces.forEach { hallwaySpace ->
                SpaceMap.roomSpaces.forEach { roomSpace ->
                    val moves = hallwaySpace.moves
                    assert(moves.find { it.destination == roomSpace} != null)
                }
            }
        }

        @Test
        fun `every room space should have a journey to every hallway space and every room space of a different type`() {
            SpaceMap.roomSpaces.forEach { roomSpace ->
                SpaceMap.hallwaySpaces.forEach { hallwaySpace ->
                    val moves = roomSpace.moves
                    val moveFromRoomToSpace = moves.find { it.destination == hallwaySpace }
                    assert(moveFromRoomToSpace != null)
                    assert(
                        moveFromRoomToSpace == hallwaySpace.moves.find { it.destination == roomSpace }!!.reverse()
                    ) { "room to hallway should pass through the same spaces as hallway to room" }
                }

                assert(roomSpace.moves.count { it.destination is RoomSpace } == 6)
                SpaceMap.roomSpaces.filter { it.type != roomSpace.type }.forEach { otherRoomSpace ->
                    assert(roomSpace.moves.any { it.destination == otherRoomSpace }) { "rooms must visit all other rooms" }
                }
            }
        }
    }

    @Nested
    inner class PositionTest {
        @Test
        fun `nextMoves should return the potential next moves from a given position`() {
            val a = Amphipod(1, AmphipodType.Amber)
            val amphipodLocations = mapOf(a to SpaceMap.hallwaySpaces[0])
            val position = Position(amphipodLocations)

            val nextMoves: List<Pair<Position, Int>> = position.nextMoves()

            assert(nextMoves.size == 2) //should be able to move to the a positions only
        }

        @Test
        fun `isBlocked should return true given a move with an amphipod in its destination already`() {
            val a = Amphipod(1, AmphipodType.Amber)
            val move = SpaceMap.roomSpaces[0].moves.find { it.destination == SpaceMap.hallwaySpaces[0] }!!
            val amphipodLocations = mapOf(a to move.destination)
            val position = Position(amphipodLocations)

            assert(position.isBlocked(move))
        }

        @Test
        fun `isBlocked should return true given a move with an amphipod in an intermediate step destination already`() {
            val a = Amphipod(1, AmphipodType.Amber)
            val move = SpaceMap.roomSpaces[0].moves.find { it.destination == SpaceMap.hallwaySpaces[0] }!!
            val amphipodLocations = mapOf(a to move.intermediateSpaces.first())
            val position = Position(amphipodLocations)


            assert(position.isBlocked(move))
        }

        @Test
        fun `isBlocked should return false given a move with no amphipods in an intermediate step destination already`() {
            val a = Amphipod(1, AmphipodType.Amber)
            val move = SpaceMap.roomSpaces[0].moves.find { it.destination == SpaceMap.hallwaySpaces[0] }!!
            val amphipodLocations = mapOf(a to move.source)
            val position = Position(amphipodLocations)


            assert(!position.isBlocked(move))
        }

        @Test
        fun `isFinished should return false given a position that is not complete`() {
            val position = Position(mapOf())

            assert(!position.isFinished())

        }

        @Test
        fun `isFinished should return false given a position that is almost complete`() {
            val a1 = Amphipod(1, AmphipodType.Amber)
            val a2 = Amphipod(2, AmphipodType.Amber)
            val b1 = Amphipod(3, AmphipodType.Bronze)
            val b2 = Amphipod(4, AmphipodType.Bronze)
            val c1 = Amphipod(5, AmphipodType.Copper)
            val c2 = Amphipod(6, AmphipodType.Copper)
            val d1 = Amphipod(7, AmphipodType.Desert)
            val d2 = Amphipod(8, AmphipodType.Desert)

            val position = Position(mapOf(
                a1 to SpaceMap.roomSpacesByType[AmphipodType.Amber]!!.first(),
                a2 to SpaceMap.roomSpacesByType[AmphipodType.Amber]!!.last(),
                b1 to SpaceMap.roomSpacesByType[AmphipodType.Bronze]!!.first(),
                b2 to SpaceMap.roomSpacesByType[AmphipodType.Bronze]!!.last(),
                c1 to SpaceMap.roomSpacesByType[AmphipodType.Copper]!!.first(),
                c2 to SpaceMap.roomSpacesByType[AmphipodType.Copper]!!.last(),
                d1 to SpaceMap.roomSpacesByType[AmphipodType.Desert]!!.first(),
                d2 to SpaceMap.hallwaySpaces[0] //not quite done
            ))

            assert(!position.isFinished())

        }

        @Test
        fun `isFinished should return false given a position that is complete`() {
            val a1 = Amphipod(1, AmphipodType.Amber)
            val a2 = Amphipod(2, AmphipodType.Amber)
            val b1 = Amphipod(3, AmphipodType.Bronze)
            val b2 = Amphipod(4, AmphipodType.Bronze)
            val c1 = Amphipod(5, AmphipodType.Copper)
            val c2 = Amphipod(6, AmphipodType.Copper)
            val d1 = Amphipod(7, AmphipodType.Desert)
            val d2 = Amphipod(8, AmphipodType.Desert)

            val position = Position(mapOf(
                a1 to SpaceMap.roomSpacesByType[AmphipodType.Amber]!!.first(),
                a2 to SpaceMap.roomSpacesByType[AmphipodType.Amber]!!.last(),
                b1 to SpaceMap.roomSpacesByType[AmphipodType.Bronze]!!.first(),
                b2 to SpaceMap.roomSpacesByType[AmphipodType.Bronze]!!.last(),
                c1 to SpaceMap.roomSpacesByType[AmphipodType.Copper]!!.first(),
                c2 to SpaceMap.roomSpacesByType[AmphipodType.Copper]!!.last(),
                d1 to SpaceMap.roomSpacesByType[AmphipodType.Desert]!!.first(),
                d2 to SpaceMap.roomSpacesByType[AmphipodType.Desert]!!.last(),
            ))

            assert(position.isFinished())

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

        val position:Position = parse(input)

        assert(position.reverseMap[SpaceMap.roomSpaces[0]]!!.id == 1)
        assert(position.reverseMap[SpaceMap.roomSpaces[0]]!!.type == AmphipodType.Bronze)
        assert(position.reverseMap[SpaceMap.roomSpaces[1]]!!.id == 2)
        assert(position.reverseMap[SpaceMap.roomSpaces[1]]!!.type == AmphipodType.Amber)
        assert(position.reverseMap[SpaceMap.roomSpaces[2]]!!.id == 3)
        assert(position.reverseMap[SpaceMap.roomSpaces[2]]!!.type == AmphipodType.Copper)
        assert(position.reverseMap[SpaceMap.roomSpaces[3]]!!.id == 4)
        assert(position.reverseMap[SpaceMap.roomSpaces[3]]!!.type == AmphipodType.Desert)
        assert(position.reverseMap[SpaceMap.roomSpaces[4]]!!.id == 5)
        assert(position.reverseMap[SpaceMap.roomSpaces[4]]!!.type == AmphipodType.Bronze)
        assert(position.reverseMap[SpaceMap.roomSpaces[5]]!!.id == 6)
        assert(position.reverseMap[SpaceMap.roomSpaces[5]]!!.type == AmphipodType.Copper)
        assert(position.reverseMap[SpaceMap.roomSpaces[6]]!!.id == 7)
        assert(position.reverseMap[SpaceMap.roomSpaces[6]]!!.type == AmphipodType.Desert)
        assert(position.reverseMap[SpaceMap.roomSpaces[7]]!!.id == 8)
        assert(position.reverseMap[SpaceMap.roomSpaces[7]]!!.type == AmphipodType.Amber)
    }

    private fun PotentialMove.reverse() : PotentialMove = PotentialMove(destination, intermediateSpaces, source)
}