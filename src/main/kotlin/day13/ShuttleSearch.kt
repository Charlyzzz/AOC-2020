package day13

import print

fun main() {
    val notes = parseBusNotes(busNotes)
    notes.findBus().print()
    notes.findContestTimestamp().print()
}

data class BusNotes(val earliestDeparture: Long, val availableLines: List<Pair<Long, Long>>) {
    fun findBus(): Long =
        availableLines
            .map { closestToDeparture(it.second) }
            .minByOrNull { it.second }
            ?.let { magicNumber(it) } ?: error("closest departure not found")

    private fun magicNumber(winner: Pair<Long, Long>): Long =
        (winner.second - earliestDeparture) * winner.first

    private fun closestToDeparture(line: Long): Pair<Long, Long> {
        val closestDeparture = ((earliestDeparture / line) + 1) * line
        return Pair(line, closestDeparture)
    }

    fun findContestTimestamp(): Long =
        availableLines.fold(Pair(1L, 1L)) { p, (offset, line) ->
            var (res, mod) = p
            while ((res + offset) % line != 0L) {
                res += mod
            }
            Pair(res, mod * line)
        }.first
}

fun parseBusNotes(busNotesSmall: String): BusNotes {
    val (timestamp, availableLines) = busNotesSmall.lines()
    val lines = availableLines.split(",")
        .mapIndexedNotNull { index, s ->
            s.toLongOrNull()?.let {
                Pair(index.toLong(), it)
            }
        }
    return BusNotes(timestamp.toLong(), lines)
}
