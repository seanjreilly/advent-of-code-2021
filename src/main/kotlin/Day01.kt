fun main() {
    fun part1(input: List<String>): Int {
        var last = input[0].toInt()
        var increases = 0
        input.subList(1, input.size).map(String::toInt).forEach {
            if (it > last) { increases++ }
            last = it
        }

        return increases
    }

    fun part2(input: List<String>): Int {
        return input.size
    }

    val input = readInput("Day01")
    println(part1(input))
    println(part2(input))
}
