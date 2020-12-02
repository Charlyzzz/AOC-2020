package day2

import java.io.File

data class Policy(val min: Int, val max: Int, val char: Char, val password: String) {
    private val range = IntRange(min, max)

    fun hasValidRange(): Boolean {
        val charOccurrences = password.count { it == char }
        return range.contains(charOccurrences)
    }

    fun matchesOnePosition(): Boolean {
        val minIndex = min - 1
        val maxIndex = max - 1
        val matches = listOf(minIndex, maxIndex).count { index ->
            index >= 0 && index <= password.length && password[index] == char
        }
        return matches == 1
    }
}

fun main() {
    val policiesFile = File("src/main/kotlin/day2/input")
    val regex = Regex("""(\d+)-(\d+) (\w): (\w+)""")
    val policies = policiesFile.readLines().map {
        val matches = regex.find(it)!!.groupValues
        val min = matches[1].toInt()
        val max = matches[2].toInt()
        val char = matches[3][0]
        val password = matches[4]
        Policy(min, max, char, password)
    }
    println(countValidPasswordRanges(policies))
    println(countValidPasswordPosition(policies))
}

fun countValidPasswordRanges(policies: List<Policy>) = policies.count { it.hasValidRange() }

fun countValidPasswordPosition(policies: List<Policy>) = policies.count { it.matchesOnePosition() }
