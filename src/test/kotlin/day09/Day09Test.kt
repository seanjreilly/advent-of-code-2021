package day09

import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

class Day09Test {
    //5 by 10 grid
    val sampleInput = """
        2199943210
        3987894921
        9856789892
        8767896789
        9899965678
    """.trimIndent().lines()

    //3 by 3 grid
    val smallInput = sampleInput.map { it.substring(0..2) }.take(3)

    @Test
    fun `part1 should find the risk level of every low point and return the sum of all risk levels`() {
        //low points are points on the map with a height smaller than every neighbour
        //the risk level is 1 + the height of the point

        assert(part1(sampleInput) == 15)
    }

    @Test
    fun `part2 should find the three largest basins and return the product of their sizes`() {
        assert(part2(sampleInput) == 1134)
    }

    @Nested
    inner class HeightMapTest {
        @Test
        fun `constructor should import the map`() {
            HeightMap(sampleInput)
        }

        @Test
        fun `width should return the width of the map`() {
            val map = HeightMap(sampleInput)
            val smallMap = HeightMap(smallInput)

            assert(map.width == 10)
            assert(smallMap.width == 3)
        }

        @Test
        fun `height should return the height of the map`() {
            val map = HeightMap(sampleInput)
            val smallMap = HeightMap(smallInput)

            assert(map.height == 5)
            assert(smallMap.height == 3)
        }

        @Test
        fun `accessor should return the height at the specified point in the map`() {
            val map = HeightMap(sampleInput)

            for (x in 0 until map.width) {
                for (y in 0  until map.height) {
                    val point = Point(x,y)
                    val height = map[point]
                    assert(height == sampleInput[y][x].digitToInt())
                }
            }

            //also test the corners manually
            assert(map[Point(0,0)] == 2)
            assert(map[Point(9,0)] == 0)
            assert(map[Point(0,4)] == 9)
            assert(map[Point(9,4)] == 8)
        }

        @Test
        fun `getNeighbours should return 4 neighbours for each cell in the interior of the map`() {
            val map = HeightMap(sampleInput)

            val interiorPoints = (1 until map.width - 1)
                .flatMap { x ->
                    (1 until map.height - 1).map { y ->
                        Point(x,y)
                    }
                }

            assert(interiorPoints.isNotEmpty()) //precondition

            interiorPoints.forEach {
                val neighbours = map.getNeighbours(it)
                assert(neighbours.size == 4)
                assert(neighbours.contains(Point(it.x, it.y-1)))
                assert(neighbours.contains(Point(it.x, it.y+1)))
                assert(neighbours.contains(Point(it.x-1, it.y)))
                assert(neighbours.contains(Point(it.x + 1, it.y)))
            }
        }

        @Test
        fun `getNeighbours should return 2 neighbours for the corner points`() {
            val map = HeightMap(sampleInput)

            val northWest = Point(0,0)
            val northWestNeighbours = map.getNeighbours(northWest)
            assert(northWestNeighbours.toSet() == setOf(Point(0,1), Point(1,0)))

            val northEast = Point(9, 0 )
            val northEastNeighbours = map.getNeighbours(northEast)
            assert(northEastNeighbours.toSet() == setOf(Point(8,0), Point(9, 1)))
            
            val southWest = Point(0, 4)
            val southWestNeighbours = map.getNeighbours(southWest)
            assert(southWestNeighbours.toSet() == setOf(Point(0,3), Point(1,4)))
            
            val southEast = Point(9, 4)
            val southEastNeighbours = map.getNeighbours(southEast)
            assert(southEastNeighbours.toSet() == setOf(Point(9, 3), Point(8, 4)))
        }

        @Test
        fun `getNeighbours should return 3 neighbours for non-corner points in the top row`() {
            val map = HeightMap(sampleInput)
            (1..8)
                .map { Point(it, 0) }
                .forEach { point ->
                    val neighbours = map.getNeighbours(point)
                    assert(neighbours.size == 3)
                    assert(neighbours.contains(point.south()))
                    assert(neighbours.contains(point.east()))
                    assert(neighbours.contains(point.west()))
                }
        }

        @Test
        fun `getNeighbours should return 3 neighbours for non-corner points in the bottom row`() {
            val map = HeightMap(sampleInput)
            (1..8)
                .map { Point(it, 4) }
                .forEach { point ->
                    val neighbours = map.getNeighbours(point)
                    assert(neighbours.size == 3)
                    assert(neighbours.contains(point.north()))
                    assert(neighbours.contains(point.east()))
                    assert(neighbours.contains(point.west()))
                }
        }

        @Test
        fun `getNeighbours should return 3 neighbours for non-corner points in the west column `() {
            val map = HeightMap(sampleInput)
            (1..3)
                .map { Point(0, it) }
                .forEach { point ->
                    val neighbours = map.getNeighbours(point)
                    assert(neighbours.size == 3)
                    assert(neighbours.contains(point.north()))
                    assert(neighbours.contains(point.south()))
                    assert(neighbours.contains(point.east()))
                }
        }

        @Test
        fun `getNeighbours should return 3 neighbours for non-corner points in the east column `() {
            val map = HeightMap(sampleInput)
            (1..3)
                .map { Point(9, it) }
                .forEach { point ->
                    val neighbours = map.getNeighbours(point)
                    assert(neighbours.size == 3)
                    assert(neighbours.contains(point.north()))
                    assert(neighbours.contains(point.south()))
                    assert(neighbours.contains(point.west()))
                }
        }

        @Test
        fun `map should be iterable and iterating should traverse all the points in the map`() {
            val map = HeightMap(sampleInput)
            val iterable: Iterable<Point> = map

            val points = iterable.toSet()
            assert(points.size == 50)
            for (x in 0 until map.width) {
                for (y in 0 until map.height) {
                    assert(Point(x,y) in points)
                }
            }
        }

        @Test
        fun `getNeighbours should return 2 neighbours for every cell in a 2x2 map`() {
            val map = HeightMap(listOf("11", "11")) //2x2 map
            map.forEach {
                assert(map.getNeighbours(it).size == 2)
            }
        }

        @Test
        fun `getNeighbors should return 0 neighbours for the cell in a 1x1 map`() {
            val map = HeightMap(listOf("1"))
            assert(map.getNeighbours(Point(0,0)).isEmpty())
        }

        @Test
        fun `findBasins should return 4 basins for the sample map`() {
            val map = HeightMap(sampleInput)

            val result:Collection<Basin> = map.findBasins()

            assert(result.size == 4)
        }

        @Test
        fun `findBasins should return 3 members for the basin with low point (1,0)`() {
            val map = HeightMap(sampleInput)

            val result = map.findBasins()

            val basin = result.filter { (lowPoint, _) -> lowPoint == Point(1, 0) }.single()
            assert(basin.members.size == 3)
            assert(Point(1,0) in basin.members)
            assert(Point(0,0) in basin.members)
            assert(Point(0,1) in basin.members)
        }

        @Test
        fun `findBasins should return 9 members for the basin with low point (9,0)`() {
            val map = HeightMap(sampleInput)

            val result = map.findBasins()

            val basin = result.filter { (lowPoint, _) -> lowPoint == Point(9, 0) }.single()
            assert(basin.members.size == 9)
            assert(Point(5,0) in basin.members)
            assert(Point(6,0) in basin.members)
            assert(Point(7,0) in basin.members)
            assert(Point(8,0) in basin.members)
            assert(Point(9,0) in basin.members)

            assert(Point(6,1) in basin.members)
            assert(Point(8,1) in basin.members)
            assert(Point(9,1) in basin.members)

            assert(Point(9,2) in basin.members)
        }

        @Test
        fun `findBasins should return 14 members for the basin with low point (2,2)`() {
            val map = HeightMap(sampleInput)

            val result = map.findBasins()

            val basin = result.filter { (lowPoint, _) -> lowPoint == Point(2, 2) }.single()
            assert(basin.members.size == 14)
            assert(Point(2,1) in basin.members)
            assert(Point(3,1) in basin.members)
            assert(Point(4,1) in basin.members)

            assert(Point(1,2) in basin.members)
            assert(Point(2,2) in basin.members)
            assert(Point(3,2) in basin.members)
            assert(Point(4,2) in basin.members)
            assert(Point(5,2) in basin.members)

            assert(Point(0,3) in basin.members)
            assert(Point(1,3) in basin.members)
            assert(Point(2,3) in basin.members)
            assert(Point(3,3) in basin.members)
            assert(Point(4,3) in basin.members)

            assert(Point(1,4) in basin.members)
        }

        @Test
        fun `findBasins should return 9 members for the basin with low point (6,4)`() {
            val map = HeightMap(sampleInput)

            val result = map.findBasins()

            val basin = result.filter { (lowPoint, _) -> lowPoint == Point(6, 4) }.single()
            assert(basin.members.size == 9)
            assert(Point(7,2) in basin.members)

            assert(Point(6,3) in basin.members)
            assert(Point(7,3) in basin.members)
            assert(Point(8,3) in basin.members)

            assert(Point(5,4) in basin.members)
            assert(Point(6,4) in basin.members)
            assert(Point(7,4) in basin.members)
            assert(Point(8,4) in basin.members)
            assert(Point(9,4) in basin.members)
        }

        @Test
        fun `findBasins should not return any basin with a member point that has a height of 9`() {
            val map = HeightMap(sampleInput)

            val result = map.findBasins()

            result.forEach { basin ->
                basin.members.forEach { member ->
                    assert(map[member] != 9)
                }
            }
        }
        //TODO:basin members should always be transitively reachable from the low point //not sure how easy this is to implement
    }
}

