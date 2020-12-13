package day12

import print
import kotlin.math.abs

fun main() {
    val route = ferryInput.lines().map { parseInstruction(it) }

    sailRoute(route, Ferry()).print()
    sailRoute(route, Waypoint()).print()
}

private fun sailRoute(route: List<Instruction>, ship: Ship) =
    route.fold(ship) { s, instruction ->
        s.perform(instruction)
    }.manhattanDistance()

sealed class Instruction

data class North(val n: Int) : Instruction()
data class South(val n: Int) : Instruction()
data class East(val n: Int) : Instruction()
data class West(val n: Int) : Instruction()
data class Forward(val n: Int) : Instruction()
data class Left(val degrees: Int) : Instruction()
data class Right(val degrees: Int) : Instruction()

fun parseInstruction(line: String): Instruction = when {
    line.startsWith("N") -> North(amount(line))
    line.startsWith("S") -> South(amount(line))
    line.startsWith("E") -> East(amount(line))
    line.startsWith("W") -> West(amount(line))
    line.startsWith("F") -> Forward(amount(line))
    line.startsWith("L") -> Left(amount(line))
    line.startsWith("R") -> Right(amount(line))
    else -> error("$line is not a valid instruction")
}

fun amount(rawInstruction: String): Int = rawInstruction.drop(1).toInt()

const val EAST = 0

interface Ship {
    fun manhattanDistance(): Int
    fun perform(instruction: Instruction): Ship
}


data class Ferry(val angle: Int, val x: Int, val y: Int) : Ship {
    constructor() : this(EAST, 0, 0)

    override fun perform(instruction: Instruction): Ferry =
        when (instruction) {
            is Right -> rotate(-instruction.degrees)
            is Left -> rotate(instruction.degrees)
            is North -> north(instruction.n)
            is South -> south(instruction.n)
            is East -> east(instruction.n)
            is West -> west(instruction.n)
            is Forward -> move(instruction.n)
        }

    private fun west(n: Int) = copy(x = x - n)

    private fun east(n: Int) = copy(x = x + n)

    private fun south(n: Int) = copy(y = y - n)

    private fun north(n: Int) = copy(y = y + n)

    private fun move(n: Int): Ferry = when (angle) {
        0 -> east(n)
        90 -> north(n)
        180 -> west(n)
        270 -> south(n)
        else -> error("$angle was not expected")
    }

    private fun rotate(degrees: Int): Ferry {
        val newAngle = (angle + degrees) % 360
        return when {
            newAngle >= 0 -> copy(angle = newAngle)
            else -> copy(angle = 360 + newAngle)
        }
    }

    override fun manhattanDistance(): Int = abs(x) + abs(y)
}

data class Waypoint(val x: Int, val y: Int, val ferry: Ferry) : Ship by ferry {

    constructor() : this(10, 1, Ferry())

    override fun perform(instruction: Instruction): Waypoint = when (instruction) {
        is Right -> rotateCW(instruction.degrees)
        is Left -> rotateCCW(instruction.degrees)
        is North -> north(instruction.n)
        is South -> south(instruction.n)
        is East -> east(instruction.n)
        is West -> west(instruction.n)
        is Forward -> commandShip(instruction)
    }

    private fun commandShip(instruction: Forward): Waypoint {
        val northAccordingToWaypoint = North(y * instruction.n)
        val eastAccordingToWaypoint = East(x * instruction.n)
        return copy(ferry = ferry.perform(northAccordingToWaypoint).perform(eastAccordingToWaypoint))
    }

    private fun rotateCW(degrees: Int): Waypoint = when (degrees) {
        0 -> this
        90 -> copy(x = y, y = -x)
        180 -> copy(x = -x, y = -y)
        270 -> copy(x = -y, y = x)
        else -> error("$degrees was not expected")
    }

    private fun rotateCCW(degrees: Int): Waypoint = rotateCW(360 - degrees)

    private fun north(n: Int): Waypoint = copy(y = y + n)

    private fun south(n: Int): Waypoint = copy(y = y - n)

    private fun east(n: Int): Waypoint = copy(x = x + n)

    private fun west(n: Int): Waypoint = copy(x = x - n)

}
