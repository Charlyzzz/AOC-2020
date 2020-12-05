package day5

import kotlin.math.max
import kotlin.math.min

const val PLANE_COLUMNS = 8

data class BoardingPass(val row: Int, val col: Int, val id: Int = PLANE_COLUMNS * row + col)

data class FindSeatRun(val min: Int, val max: Int, val sum: Int) {

    fun missingSeat(): Int {
        val expectedSum = IntRange(min, max).sum()
        return expectedSum - sum
    }

    fun track(pass: BoardingPass): FindSeatRun =
        copy(min = min(min, pass.id), max = max(max, pass.id), sum = sum + pass.id)
}

fun main() {
    val boardingPasses = boardingPassesInput.lines().map { parseSeat(it) }
    println(findHighestBoardingPassByID(boardingPasses))
    println(findMissingSeat(boardingPasses))
}

fun findMissingSeat(boardingPasses: List<BoardingPass>): Int =
    boardingPasses
        .fold(FindSeatRun(Int.MAX_VALUE, Int.MIN_VALUE, 0)) { run, seat -> run.track(seat) }
        .missingSeat()

private fun findHighestBoardingPassByID(boardingPasses: List<BoardingPass>): Int =
    boardingPasses
        .maxByOrNull { it.id }?.id ?: throw RuntimeException("no seat found")

fun parseSeat(s: String): BoardingPass {
    val (row, column) = s.chunked(7)
    return BoardingPass(parseRow(row), parseColumn(column))
}

private fun parseRow(rowString: String): Int = toBinary(rowString, 'B', 'F')

private fun parseColumn(columnString: String): Int = toBinary(columnString, 'R', 'L')

fun toBinary(encodedBinary: String, highState: Char, lowState: Char): Int =
    encodedBinary
        .replace(highState, '1')
        .replace(lowState, '0')
        .let { Integer.parseInt(it, 2) }