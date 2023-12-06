// Your toy boat has a starting speed of zero millimeters per millisecond. For each whole
// millisecond you spend at the beginning of the race holding down the button, the boat's speed
// increases by one millimeter per millisecond.
class Boat {
    private var holdTime: Long = 0

    fun holdFor(time: Long) {
        holdTime = time
    }

    // How far will the boat travel in the given time
    fun runFor(time: Long): Long {
        return time * holdTime
    }

    fun reset() {
        holdTime = 0
    }
}

data class Race(
    val time: Long,
    val distance: Long,
) {
    fun isWinner(boat: Boat, remainingTime: Long): Boolean {
        return boat.runFor(remainingTime) > distance
    }

    fun timeRemaining(elapsedTime: Long): Long {
        return maxOf(0, time - elapsedTime)
    }
}

fun <T, K, S> combineLists(l1: List<T>, l2: List<K>, transform: (T, K) -> S): List<S> {
    assert(l1.size == l2.size)
    val result = mutableListOf<S>()
    for (i in l1.indices) {
        result.add(transform(l1[i], l2[i]))
    }
    return result
}

fun main() {
    fun parseInput1(input: List<String>): List<Race> {
        val whitespace = "\\s+".toRegex()
        val times = input[0].substringAfter("Time:").split(whitespace).mapNotNull { it.toLongOrNull() }
        val distances = input[1].substringAfter("Distance:").split(whitespace).mapNotNull { it.toLongOrNull() }
        val races = combineLists(times, distances) { time, distance ->
            Race(time, distance)
        }
        return races
    }

    fun parseInput2(input: List<String>): Race {
        val regex = "[^0-9]".toRegex()
        val time = input[0].replace(regex, "").toLong()
        val distance = input[1].replace(regex, "").toLong()
        return Race(time, distance)
    }

    fun getWinsCount1(race: Race): Long {
        var count = 0L
        val boat = Boat()
        for (i in 1..race.time) {
            boat.holdFor(i)
            if (race.isWinner(boat, race.timeRemaining(i))) {
                count++
            }
            boat.reset()
        }
        return count
    }

    fun part1(input: List<String>): Long {
        val races: List<Race> = parseInput1(input)
        var total = 1L
        races.forEach { race ->
            total *= getWinsCount1(race)
        }
        return total
    }

    fun part2(input: List<String>): Long {
        val race = parseInput2(input)
        return getWinsCount1(race)
    }

    val input = readInput("Day06")
    println("Part 1:")
    part1(input).println()
    println("Part 2:")
    part2(input).println()
}