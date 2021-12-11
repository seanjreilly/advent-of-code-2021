package utils.gridmap

abstract class GridMap<T>(private val data : Array<Array<T>>) : Iterable<Point> {
    val height: Int = data.size
    val width: Int = data.first().size

    init {
        //ensure the map is rectangular
        check(data.all { it.size == width }) {"every row must be the same size"}
    }

    operator fun get(point: Point): T = data[point.y][point.x]

    internal fun isPointInMap(point: Point) : Boolean {
        return point.x in (0 until width) && point.y in (0 until height)
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
    fun south() = Point(x, y + 1)
    fun east() = Point(x + 1, y)
    fun west() = Point(x - 1, y)

    fun getCardinalNeighbours() : Collection<Point> {
        return listOf(
            north(),
            south(),
            east(),
            west(),
        )
    }
}