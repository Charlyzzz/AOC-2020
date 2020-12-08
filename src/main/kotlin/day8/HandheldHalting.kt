package day8

fun main() {
    val instructions = parseInstructions(rawDeviceBootCode)
    runProgram(instructions).also { println(it.accum) }
    accumAfterFixingTheLoop(instructions).also { println(it) }
}

interface Result {
    val accum: Int
}

data class Successful(override val accum: Int) : Result

data class LoopDetected(override val accum: Int) : Result

interface Instruction

data class Nop(val n: Int) : Instruction

data class Jump(val positions: Int) : Instruction

data class Acc(val diff: Int) : Instruction

fun accumAfterFixingTheLoop(instructions: List<Instruction>): Int {
    val wipProgram = instructions.toTypedArray()
    instructions.forEachIndexed { index, instruction ->
        swapInstruction(instruction).also {
            wipProgram[index] = it
            val result = runProgram(wipProgram.toList())
            if (result is Successful) return result.accum
        }
        wipProgram[index] = instruction
    }
    error("no successful swap found")
}

fun swapInstruction(instruction: Instruction): Instruction {
    return when (instruction) {
        is Jump -> Nop(instruction.positions)
        is Nop -> Jump(instruction.n)
        else -> instruction
    }
}

fun runProgram(instructions: List<Instruction>): Result {
    var pc = 0
    var acc = 0
    var instructionsVisited = emptySet<Int>()
    var instruction = instructions[0]
    while (!instructionsVisited.contains(pc)) {
        instructionsVisited = instructionsVisited + pc
        var pcInc = 1
        when (instruction) {
            is Acc -> acc += instruction.diff
            is Jump -> pcInc = instruction.positions
        }
        pc += pcInc

        if (pc == instructions.size) {
            return Successful(acc)
        }

        instruction = instructions[pc]
    }
    return LoopDetected(acc)
}

fun parseInstructions(rawTestCode: String): List<Instruction> =
    rawTestCode.lines().map {
        when {
            it.startsWith("nop") -> Nop(toSignedInt(it))
            it.startsWith("acc") -> Acc(toSignedInt(it))
            it.startsWith("jmp") -> Jump(toSignedInt(it))
            else -> error("unknown op: $it")
        }
    }

private fun toSignedInt(string: String) = string.drop(3).trim().toInt()