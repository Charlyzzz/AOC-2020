package day11

import print

const val EMPTY_SEAT = 'L'
const val OCCUPIED_SEAT = '#'
const val FLOOR = '.'

fun main() {
    val layout = buildLayout(fullSeatLayout)
    occupiedSeatsAfterStabilizing(layout).print()
    occupiedSeatsAfterStabilizing(layout.withWiderRange()).print()
}

data class Layout(
    val seatMap: Map<Position, Seat>,
    val cols: Int,
    val rows: Int,
    val longRangeAdjacent: Boolean = false,
    val adjacentCriteria: Int = 4
) {

    private fun seats(): Collection<Seat> = seatMap.values

    fun add(seat: Seat): Layout = copy(seatMap = seatMap + (seat.pos to seat))

    fun mutate(): Layout {
        val newSeatMap = seats().fold(mutableMapOf<Position, Seat>()) { map, seat ->
            val newSeat = seat.mutate(this)
            map.also {
                it[newSeat.pos] = newSeat
            }
        }
        return copy(seatMap = newSeatMap)
    }

    fun countOccupiedSeats(): Int = seats().count { it.isOccupied }

    fun adjacentOccupied(seat: Seat): Int =
        listOfNotNull(
            usingRange(left(seat.pos)),
            usingRange(right(seat.pos)),
            usingRange(up(seat.pos)),
            usingRange(down(seat.pos)),
            usingRange(topLeft(seat.pos)),
            usingRange(topRight(seat.pos)),
            usingRange(bottomLeft(seat.pos)),
            usingRange(bottomRight(seat.pos))
        )
            .map { seatMap.getValue(it) }
            .count { it.isOccupied }

    private fun usingRange(sequence: Sequence<Position>): Position? =
        when {
            longRangeAdjacent -> sequence.firstOrNull { !seatMap.getValue(it).isFloor }
            else -> sequence.firstOrNull()
        }

    private fun move(x: Int, y: Int, pos: Position): Sequence<Position> {
        return generateSequence(pos) {
            validate(it.copy(first = it.first + y, second = it.second + x))
        }.drop(1)
    }

    private fun left(pos: Position): Sequence<Position> = move(-1, 0, pos)

    private fun right(pos: Position): Sequence<Position> = move(1, 0, pos)

    private fun up(pos: Position): Sequence<Position> = move(0, -1, pos)

    private fun down(pos: Position): Sequence<Position> = move(0, 1, pos)

    private fun topLeft(pos: Position): Sequence<Position> = move(-1, -1, pos)

    private fun topRight(pos: Position): Sequence<Position> = move(1, -1, pos)

    private fun bottomLeft(pos: Position): Sequence<Position> = move(-1, 1, pos)

    private fun bottomRight(pos: Position): Sequence<Position> = move(1, 1, pos)

    private fun validate(position: Position): Position? {
        val (row, col) = position
        return when {
            row < 0 || col < 0 -> null
            row > rows - 1 || col > cols - 1 -> null
            else -> position
        }
    }

    fun withWiderRange(): Layout = copy(longRangeAdjacent = true, adjacentCriteria = 5)

}

typealias Position = Pair<Int, Int>

fun occupiedSeatsAfterStabilizing(layout: Layout) =
    generateSequence(layout) { it.mutate() }
        .chunked(2)
        .first { it[0] == it[1] }
        .first()
        .countOccupiedSeats()

data class Seat(val pos: Pair<Int, Int>, val status: Char) {
    val isOccupied = OCCUPIED_SEAT == status
    val isFloor = FLOOR == status

    fun mutate(layout: Layout): Seat {
        return when (status) {
            EMPTY_SEAT -> {
                if (layout.adjacentOccupied(this) == 0)
                    copy(status = OCCUPIED_SEAT)
                else this
            }
            OCCUPIED_SEAT -> {
                if (layout.adjacentOccupied(this) >= layout.adjacentCriteria)
                    copy(status = EMPTY_SEAT)
                else this
            }
            else -> this
        }
    }
}

private fun buildLayout(stringLayout: String): Layout {
    val lines = stringLayout.lines()
    return lines.foldIndexed(Layout(emptyMap(), lines.first().length, lines.size)) { rowIndex, layoutSoFar, seats ->
        seats.toCharArray()
            .foldIndexed(layoutSoFar) { columnIndex, floorLayout, seatStatus ->
                floorLayout.add(Seat(Pair(rowIndex, columnIndex), seatStatus))
            }
    }
}
