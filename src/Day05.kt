sealed interface Category {
    val value: Long
    fun fromLong(l: Long): Category

    @JvmInline
    value class Seed(override val value: Long) : Category {
        override fun fromLong(l: Long) = Seed(l)
    }

    @JvmInline
    value class Soil(override val value: Long) : Category {
        override fun fromLong(l: Long) = Soil(l)
    }

    @JvmInline
    value class Fertilizer(override val value: Long) : Category {
        override fun fromLong(l: Long) = Fertilizer(l)
    }

    @JvmInline
    value class Water(override val value: Long) : Category {
        override fun fromLong(l: Long) = Water(l)
    }

    @JvmInline
    value class Light(override val value: Long) : Category {
        override fun fromLong(l: Long) = Light(l)
    }

    @JvmInline
    value class Temperature(override val value: Long) : Category {
        override fun fromLong(l: Long) = Temperature(l)
    }

    @JvmInline
    value class Humidity(override val value: Long) : Category {
        override fun fromLong(l: Long) = Humidity(l)
    }

    @JvmInline
    value class Location(override val value: Long) : Category {
        override fun fromLong(l: Long) = Humidity(l)
    }
}

//fun Category.fromLong(value: Long) = this.fromLong(value)

data class AlmanacRange<T : Category, K : Category>(
        val inputStart: T,
        val outputStart: K,
        val range: Long,
) {
    private val inputRange = LongRange(inputStart.value, (inputStart.value + range) - 1)
    private val convertInput = { input: T -> input.value + (outputStart.value - inputStart.value) }
    fun contains(input: T): Boolean {
        return inputRange.contains(input.value)
    }

    fun getOutput(input: T, toOutput: (Long) -> K): K {
        return if (inputRange.contains(input.value)) {
            toOutput(convertInput(input))
        } else {
            toOutput(input.value)
        }
    }
}

abstract class IslandAlmanac<T : Category, K : Category> {
    abstract val tag: String
    private val tagInput: String
        get() = tag.split("-")[0]
    private val tagOutput: String
        get() = tag.split("-")[2]
    val map: MutableMap<T, K> = mutableMapOf()
    private val almanacRanges: MutableList<AlmanacRange<T, K>> = mutableListOf()
    abstract fun toInput(value: Long): T
    abstract fun toOutput(value: Long): K

    fun addRange(inputStart: Long, outputStart: Long, range: Long) {
        almanacRanges.add(
                AlmanacRange(
                        toInput(inputStart),
                        toOutput(outputStart),
                        range,
                )
        )
    }

    operator fun invoke(input: T): K {
        almanacRanges.forEachIndexed { index, almanacRange ->
            if (almanacRange.contains(input)) {
                return almanacRange.getOutput(input, ::toOutput)
            }
        }
        return toOutput(input.value)
    }
}

data object SeedToSoil : IslandAlmanac<Category.Seed, Category.Soil>() {
    override val tag = "seed-to-soil"
    override fun toInput(value: Long) = Category.Seed(value)

    override fun toOutput(value: Long) = Category.Soil(value)
}

data object SoilToFertilizer : IslandAlmanac<Category.Soil, Category.Fertilizer>() {
    override val tag = "soil-to-fertilizer"
    override fun toInput(value: Long) = Category.Soil(value)
    override fun toOutput(value: Long) = Category.Fertilizer(value)
}

data object FertilizerToWater : IslandAlmanac<Category.Fertilizer, Category.Water>() {
    override val tag = "fertilizer-to-water"
    override fun toInput(value: Long) = Category.Fertilizer(value)

    override fun toOutput(value: Long) = Category.Water(value)
}

data object WaterToLight : IslandAlmanac<Category.Water, Category.Light>() {
    override val tag = "water-to-light"
    override fun toInput(value: Long) = Category.Water(value)

    override fun toOutput(value: Long) = Category.Light(value)
}

data object LightToTemperature : IslandAlmanac<Category.Light, Category.Temperature>() {
    override val tag = "light-to-temperature"
    override fun toInput(value: Long) = Category.Light(value)

    override fun toOutput(value: Long) = Category.Temperature(value)
}

data object TemperatureToHumidity : IslandAlmanac<Category.Temperature, Category.Humidity>() {
    override val tag = "temperature-to-humidity"
    override fun toInput(value: Long) = Category.Temperature(value)

    override fun toOutput(value: Long) = Category.Humidity(value)
}

data object HumidityToLocation : IslandAlmanac<Category.Humidity, Category.Location>() {
    override val tag = "humidity-to-location"
    override fun toInput(value: Long) = Category.Humidity(value)

    override fun toOutput(value: Long) = Category.Location(value)
}

fun main() {

    fun readSeeds(line: String, seeds: MutableList<Category.Seed>) {
        line.split(" ").forEach {
            seeds.add(Category.Seed(it.toLong()))
        }
    }

    // seed, soil, fertilizer, water, light, temperature, humidity, location
    fun seedToLocation(seed: Category.Seed): Category.Location {
        return HumidityToLocation(
                TemperatureToHumidity(
                        LightToTemperature(
                                WaterToLight(
                                        FertilizerToWater(
                                                SoilToFertilizer(
                                                        SeedToSoil(seed)
                                                )
                                        )
                                )
                        )
                )
        )
    }

    fun part1Solution(seeds: List<Category.Seed>): Long {
        var minValue: Long = Long.MAX_VALUE
        seeds.forEach { seed ->
            val location = seedToLocation(seed)
            minValue = minOf(minValue, location.value)
        }
        return minValue
    }

    fun part1(input: List<String>): Long {
        val seeds: MutableList<Category.Seed> = mutableListOf()
        var curAlmanac: IslandAlmanac<out Category, out Category>? = null
        input.forEach { line ->
            when {
                line.startsWith("seeds: ") -> {
                    readSeeds(line.substringAfter("seeds: "), seeds)
                }

                line.startsWith("seed-to-soil map") -> {
                    curAlmanac = SeedToSoil
                }

                line.startsWith("soil-to-fertilizer map") -> {
                    curAlmanac = SoilToFertilizer
                }

                line.startsWith("fertilizer-to-water map") -> {
                    curAlmanac = FertilizerToWater
                }

                line.startsWith("water-to-light map") -> {
                    curAlmanac = WaterToLight
                }

                line.startsWith("light-to-temperature map") -> {
                    curAlmanac = LightToTemperature
                }

                line.startsWith("temperature-to-humidity map") -> {
                    curAlmanac = TemperatureToHumidity
                }

                line.startsWith("humidity-to-location map") -> {
                    curAlmanac = HumidityToLocation
                }

                line.isEmpty() -> Unit
                else -> {
                    val rangeValues = line.split(" ").map { it.toLong() }
                    curAlmanac?.addRange(rangeValues[1], rangeValues[0], rangeValues[2])
                }
            }
        }

        return part1Solution(seeds)
    }

    fun readSeedsPart2(line: String, seeds: MutableList<LongRange>) {
        val values = line.split(" ").map { it.toLong() }
        for (i in 0 until values.size - 1 step 2) {
            val range = LongRange(values[i], (values[i] + values[i + 1]) - 1)
            seeds.add(range)
        }
    }

    fun part2Solution(seeds: MutableList<LongRange>): Long {
        var minValue: Long = Long.MAX_VALUE
        seeds.forEach { seedRange ->
            seedRange.forEach { seedValue ->
                val seed = Category.Seed(seedValue)
                val location = seedToLocation(seed)
                minValue = minOf(minValue, location.value)
            }
        }
        return minValue
    }

    fun part2(input: List<String>): Long {
        val seeds = mutableListOf<LongRange>()
        var curAlmanac: IslandAlmanac<out Category, out Category>? = null
        input.forEach { line ->
            when {
                line.startsWith("seeds: ") -> {
                    readSeedsPart2(line.substringAfter("seeds: "), seeds)
                }

                line.startsWith("seed-to-soil map") -> {
                    curAlmanac = SeedToSoil
                }

                line.startsWith("soil-to-fertilizer map") -> {
                    curAlmanac = SoilToFertilizer
                }

                line.startsWith("fertilizer-to-water map") -> {
                    curAlmanac = FertilizerToWater
                }

                line.startsWith("water-to-light map") -> {
                    curAlmanac = WaterToLight
                }

                line.startsWith("light-to-temperature map") -> {
                    curAlmanac = LightToTemperature
                }

                line.startsWith("temperature-to-humidity map") -> {
                    curAlmanac = TemperatureToHumidity
                }

                line.startsWith("humidity-to-location map") -> {
                    curAlmanac = HumidityToLocation
                }

                line.isEmpty() -> Unit
                else -> {
                    val rangeValues = line.split(" ").map { it.toLong() }
                    curAlmanac?.addRange(rangeValues[1], rangeValues[0], rangeValues[2])
                }
            }
        }

        return part2Solution(seeds)
    }

    val input = readInput("Day05")
    println("part 1:")
    part1(input).println()
    println("part 2:")
    part2(input).println()
}