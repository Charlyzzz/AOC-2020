package day9

import print
import java.util.*
import kotlin.math.max
import kotlin.math.min

fun main() {
    val xmasSequence = xmasData.lines().map { it.toLong() }
    val preambleLength = 25
    findSequenceError(xmasSequence, preambleLength).print()
    findEncryptionWeakness(xmasSequence, preambleLength).print()
}

fun findEncryptionWeakness(xmasSequence: List<Long>, preambleLength: Int): Long {
    val sequenceError = findSequenceError(xmasSequence, preambleLength)
    val subSequence = xmasSequence.take(xmasSequence.indexOf(sequenceError) - 1)
    return findSubSequenceAddingUp(subSequence, sequenceError)
}

data class SubSequenceRun(val window: Queue<Long>, val sum: Long, val target: Long) {

    fun process(number: Long): SubSequenceRun {
        window.add(number)
        return if (sum + number > target) {
            var newSum = sum
            while (newSum + number > target) {
                val out = window.poll()
                newSum -= out
            }
            copy(sum = newSum + number)
        } else {
            copy(sum = sum + number)
        }
    }

    fun cryptoWeakness(): Long {
        val minMax = Long.MAX_VALUE to Long.MIN_VALUE
        val (minFound, maxFound) = window.fold(minMax) { (min, max), n ->
            minMax.copy(first = min(min, n), second = max(max, n))
        }
        return minFound + maxFound
    }
}

fun findSubSequenceAddingUp(sequence: List<Long>, targetNumber: Long): Long {
    sequence.fold(SubSequenceRun(LinkedList(), 0, targetNumber)) { run, number ->
        run.process(number).also {
            if (it.sum == targetNumber)
                return run.cryptoWeakness()
        }
    }
    error("no sequence found")
}

fun findSequenceError(xmasSequence: List<Long>, preambleLength: Int): Long {
    xmasSequence.fold(RunningSequence(preambleLength)) { runningSequence, n ->
        if (!runningSequence.validateNext(n)) return n
        runningSequence
    }
    error("error not found in sequence")
}

data class RunningSequence(val preambleLength: Int) {

    private var numbers: Set<Long> = emptySet()
    private val window: Queue<Long> = LinkedList()

    fun validateNext(n: Long): Boolean {
        return if (window.size < preambleLength) {
            addNumber(n)
            true
        } else {
            validateNumber(n).also {
                addNumber(n)
                removeNumber()
            }
        }
    }

    private fun validateNumber(n: Long): Boolean =
        numbers.asSequence()
            .filter { it < n }
            .any {
                val missing = n - it
                numbers.contains(missing) && missing != it
            }

    private fun removeNumber() {
        val toRemove = window.poll()
        numbers = numbers - toRemove
    }

    private fun addNumber(n: Long) {
        window.add(n)
        numbers = numbers + n
    }
}
