package day10

import print
import kotlin.math.max

fun main() {
    val adapters = adaptersInBag.lines().map { it.toInt() }
    val adapterDifference = findDifference(adapters)
    adaptersDifferenceMultiplication(adapterDifference).print()
    countDifferentAdapterCombinations(adapterDifference).print()
}

fun countDifferentAdapterCombinations(adapterDifference: AdapterDifference): Long =
    adapterDifference.run {
        combinations.getValue(maxJolts)
    }

const val OUTLET_JOLTAGE = 0

data class AdapterDifference(
    val byOneCount: Int,
    val byTwoCount: Int,
    val byThreeCount: Int,
    val maxJolts: Int,
    val combinations: Map<Int, Long>
) {

    constructor() : this(0, 0, 0, OUTLET_JOLTAGE, mapOf(0 to 1))

    fun process(n: Int): AdapterDifference {
        val joltageDiff = n - maxJolts
        return when (joltageDiff) {
            1 -> copy(byOneCount = byOneCount + 1)
            2 -> copy(byTwoCount = byTwoCount + 1)
            3 -> copy(byThreeCount = byThreeCount + 1)
            else -> error("adapterDifference was $joltageDiff")
        }.run {
            val possibleCombinations = adaptersThatFit(n)
            var updatedCombinations = combinations
            possibleCombinations.forEach { possibleAdapter ->
                combinations[possibleAdapter]?.let {
                    val existingCombinations = updatedCombinations.getOrDefault(n, 0)
                    updatedCombinations = updatedCombinations + (n to (existingCombinations + it))
                }
            }
            copy(maxJolts = n, combinations = updatedCombinations)
        }
    }

    private fun adaptersThatFit(n: Int) = setOf(
        max(n - 1, 0),
        max(n - 2, 0),
        max(n - 3, 0)
    )

}

fun adaptersDifferenceMultiplication(adapterDifference: AdapterDifference): Int {
    return adapterDifference.byOneCount * adapterDifference.byThreeCount
}

private fun findDifference(adapters: List<Int>): AdapterDifference {
    var adapterDifference = adapters
        .sorted()
        .fold(AdapterDifference()) { diff, n ->
            diff.process(n)
        }
    adapterDifference = adapterDifference.process(adapterDifference.maxJolts + 3)

    return adapterDifference
}
