// Your toy boat has a starting speed of zero millimeters per millisecond. For each whole
// millisecond you spend at the beginning of the race holding down the button, the boat's speed
// increases by one millimeter per millisecond.
class Boat {
    var holdTime: Int = 0
        private set
//    var runTime: Int = 0
//        private set

    fun holdFor(time: Int) {
        holdTime = time
    }

    // How far will the boat travel in the given time
    fun runFor(time: Int): Int {
        return time * holdTime
    }

    fun reset() {
        holdTime = 0
//        runTime = 0
    }
}

data class Race(
    val time: Int,
    val distance: Int,
) {
    fun isWinner(boat: Boat, remainingTime: Int): Boolean {
        return boat.runFor(remainingTime) > distance
    }

    fun timeRemaining(elapsedTime: Int): Int {
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
        val times = input[0].substringAfter("Time:").split(whitespace).mapNotNull { it.toIntOrNull() }
        val distances = input[1].substringAfter("Distance:").split(whitespace).mapNotNull { it.toIntOrNull() }
        val races = combineLists(times, distances) { time, distance ->
            Race(time, distance)
        }
        return races
    }

    fun getWinsCount1(race: Race): Int {
        var count = 0
        var boat = Boat()
        for (i in 1..race.time) {
            boat.holdFor(i)
            if (race.isWinner(boat, race.timeRemaining(i))) {
                count++
            }
            boat.reset()
        }
        return count
    }

    fun part1(input: List<String>): Int {
        val races: List<Race> = parseInput1(input)
        var total = 1
        races.forEach { race ->
            total *= getWinsCount1(race)
        }
        return total
    }

    fun part2(input: List<String>): Int {
        return input.size
    }

    val input = readInput("Day06")
    println("Part 1:")
    part1(input).println()
    println("Part 2:")
    part2(input).println()
}