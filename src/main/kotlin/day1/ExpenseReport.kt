package day1

import java.io.File

fun main() {
    val numbers = File("src/main/kotlin/day1/input").readLines().map { it.toInt() }
    println(expenseReportWithTwoNumbers(numbers))
    println(expenseReportWithThreeNumbers(numbers))
}

fun expenseReportWithTwoNumbers(numbers: List<Int>): Int {
    val numbersSoFar = mutableSetOf<Int>()
    val goal = 2020
    numbers.forEach {
        val counterpart = goal - it
        if (numbersSoFar.contains(counterpart)) {
            return it * counterpart
        } else {
            numbersSoFar.add(it)
        }
    }
    throw RuntimeException("no match found :(")
}

fun expenseReportWithThreeNumbers(numbers: List<Int>): Int {
    val numbersSoFar = mutableMapOf<Int, MutableSet<Int>>()
    val goal = 2020
    numbers.forEach {
        numbersSoFar.putIfAbsent(it, mutableSetOf())
        for (entry in numbersSoFar.entries) {
            val primary = entry.key
            val missing = entry.value
            val counterpart = goal - primary - it
            if (missing.contains(it)) {
                return primary * it * counterpart
            }
            missing.add(counterpart)
        }
    }
    throw RuntimeException("no match found :(")
}
