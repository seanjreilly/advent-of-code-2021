package utils.gridmap

abstract class GridMap<T>(protected val data : Array<Array<T>>, private val getNeighboursMethod: (Point) -> Collection<Point>) : Iterable<Point> {
    val height: Int = data.size
    val width: Int = data.first().size

    init {
        //ensure the map is rectangular
        check(data.all { it.size == width }) {"every row must be the same size"}
    }

    operator fun get(point: Point): T = data[point.y][point.x]

    fun getNeighbours(point: Point): Collection<Point> {
        return getNeighboursMethod(point)
            .filter { it.x in (0 until width) }
            .filter { it.y in (0 until height) }
    }

    override fun iterator() = iterator {
        for (x in 0 until width) {
            for (y in 0 until height) {
                yield(Point(x, y))
            }
        }
    }
}

data class Point(val x: Int, val y: Int) {
    fun north() = Point(x, y - 1)
    fun northEast() = Point(x+1, y-1)
    fun east() = Point(x + 1, y)
    fun southEast() = Point(x + 1, y + 1)
    fun south() = Point(x, y + 1)
    fun southWest() = Point(x - 1, y + 1)
    fun west() = Point(x - 1, y)
    fun northWest() = Point(x-1, y - 1)

    fun getCardinalNeighbours() : Collection<Point> {
        return listOf(
            north(),
            south(),
            east(),
            west(),
        )
    }

    fun getCardinalAndDiagonalNeighbours() : Collection<Point> {
        return getCardinalNeighbours() + listOf(
            northEast(),
            northWest(),
            southEast(),
            southWest()
        )
    }
}