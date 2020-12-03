package day3

import java.io.File

data class Terrain(val pattern: String) {
    fun treeAt(position: Int): Boolean {
        return pattern[position % pattern.length] == '#'
    }
}

data class SlopeRun(val position: Int, val treesHit: Int)

fun main() {
    val slope = File("src/main/kotlin/day3/input").readLines().map { Terrain(it) }
    println(countTreesStartingAt(3, 1, slope))


    println(
        // Force Long multiplication
        1L
                * countTreesStartingAt(1, 1, slope)
                * countTreesStartingAt(3, 1, slope)
                * countTreesStartingAt(5, 1, slope)
                * countTreesStartingAt(7, 1, slope)
                * countTreesStartingAt(1, 2, slope)
    )
}

private fun countTreesStartingAt(xIncrement: Int, yIncrement: Int, slope: List<Terrain>): Int {
    return slope.foldIndexed(SlopeRun(0, 0)) { index, slopeRun, terrain ->
        if (index % yIncrement != 0) {
            return@foldIndexed slopeRun
        }
        val newPosition = slopeRun.position + xIncrement
        if (terrain.treeAt(slopeRun.position)) {
            slopeRun.copy(position = newPosition, treesHit = slopeRun.treesHit + 1)
        } else {
            slopeRun.copy(position = newPosition)
        }
    }.treesHit
}