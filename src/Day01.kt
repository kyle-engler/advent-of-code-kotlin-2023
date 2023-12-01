fun main() {
    fun part1(input: List<String>): Int {
        var sum = 0
        var firstChar: Char? = null
        var lastChar: Char? = null
        input.forEach line@{ line ->
            line.forEach char@{ char ->
               char.digitToIntOrNull()?.let {
                   if (firstChar == null) {
                       firstChar = char
                   } else {
                       lastChar = char
                   }
                }
            }

            val chars = (firstChar.toString() + (lastChar ?: firstChar).toString())
            sum += chars.toInt()
            firstChar = null
            lastChar = null
        }
        return sum
    }

    val numberWords = listOf(
            "one",
            "two",
            "three",
            "four",
            "five",
            "six",
            "seven",
            "eight",
            "nine",
    )

    fun parseLine(str: String): List<String> {
        val numbers = mutableListOf<String>()
        for (charIndex in str.indices) {
            str[charIndex].digitToIntOrNull()?.let {
                numbers.add(str[charIndex].toString())
            } ?:
            numberWords.forEachIndexed { numWordIndex, numStr ->
                if (str.substring(charIndex).indexOf(numStr) == 0) {
                    numbers.add((numWordIndex + 1).toString())
                }
            }
        }
        return numbers
    }

    fun part2(input: List<String>): Int {
        var sum = 0
        input.forEach { line ->
            val arr = parseLine(line)
            sum += (arr.first() + arr.last()).toInt()
        }
        return sum
    }

    // test if implementation meets criteria from the description, like:
//    val testInput = readInput("Day01_test")
//    check(part1(testInput) == 1)

    val input = readInput("Day01")
    part1(input).println()
    part2(input).println()
}
