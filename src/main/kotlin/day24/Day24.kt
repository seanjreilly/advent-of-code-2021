package day24

import java.util.ArrayDeque
import utils.readInput
import kotlin.IllegalArgumentException
import kotlin.reflect.KFunction1

fun main() {
    val input = readInput("Day24")
    println(part1(input))
    println(part2(input))
}

fun part1(input: List<String>): Long {
    val monadProgram = parse(input)
    val operation = List<Int>::maxOrNull

    return solveSerialNumber(monadProgram, operation)
}

fun part2(input: List<String>): Long {
    val monadProgram = parse(input)
    val operation = List<Int>::minOrNull

    return solveSerialNumber(monadProgram, operation)
}

private fun solveSerialNumber(
    monadProgram: Program,
    operation: KFunction1<List<Int>, Int?>
): Long {
    //program is repeated blocks of 18 instructions
    val parameters = monadProgram.chunked(18).map { instructions ->
        Parameters(
            ((instructions[5] as AluInstruction.Add).b as Number).value,
            ((instructions[15] as AluInstruction.Add).b as Number).value
        )
    }
    val stack = ArrayDeque<StackFrame>()
    val result = IntArray(14)
    //the multiply by base 26 stuff is just comparing the digits in reverse order using a*26 + b (and then in reverse by division)
    //find the answer by finding the digit in each case that keeps the result in 1 to 9 so z == 0 at the end
    parameters.forEachIndexed { index, parametersForIndex ->
        if (parametersForIndex.xRegisterValue >= 10) {
            //when there's a big x value it's ignored and the y value is used later
            stack.push(StackFrame(index, parametersForIndex.yRegisterValue))
        } else {
            //when there's a small x value it's combined with the previous y
            val popped = stack.pop()
            val xPlusOldY = popped.value + parametersForIndex.xRegisterValue
            //solve by finding the largest (or smallest) digit that can be added to x + y and be within 1 to 9
            val solvedDigit = operation((1..9).filter { it + xPlusOldY in 1..9 })!!
            result[index] = solvedDigit + xPlusOldY
            result[popped.sourceIndex] = solvedDigit
        }
    }
    return result.joinToString("") { it.toString() }.toLong()
}

class Parameters(val xRegisterValue: Int, val yRegisterValue: Int)
class StackFrame(val sourceIndex: Int, val value: Int)

internal fun parse(input: List<String>): Program {
    fun parseVariable(input: String) : Variable {
        return when (input) {
            "w" -> Variable.W
            "x" -> Variable.X
            "y" -> Variable.Y
            "z" -> Variable.Z
            else -> { throw IllegalArgumentException("unknown variable '${input}'") }
        }
    }
    fun parsePlaceholder(input:String) : Placeholder = input.toIntOrNull()?.let(::Number) ?: parseVariable(input)

    return input.map {
        val split = it.split(" ")
        when (split[0]) {
            "inp" -> AluInstruction.Inp(parseVariable(split[1]))
            "add" -> AluInstruction.Add(parseVariable(split[1]), parsePlaceholder(split[2]))
            "mul" -> AluInstruction.Mul(parseVariable(split[1]), parsePlaceholder(split[2]))
            "div" -> AluInstruction.Div(parseVariable(split[1]), parsePlaceholder(split[2]))
            "mod" -> AluInstruction.Mod(parseVariable(split[1]), parsePlaceholder(split[2]))
            "eql" -> AluInstruction.Eql(parseVariable(split[1]), parsePlaceholder(split[2]))
            else -> {
                throw IllegalArgumentException("unknown operation '${split[0]}'")
            }
        }
    }
}

internal sealed class AluInstruction(val a: Variable) {
    fun execute(alu:Alu) = a.setValue(executeInternal(alu), alu)
    abstract fun executeInternal(alu:Alu): Int

    internal class Inp(a: Variable): AluInstruction(a) {
        override fun executeInternal(alu: Alu) = alu.readInput()
    }

    internal class Add(a: Variable, val b: Placeholder): AluInstruction(a) {
        override fun executeInternal (alu: Alu) = a.getValue(alu) + b.getValue(alu)
    }

    internal class Mul(a: Variable, val b: Placeholder): AluInstruction(a) {
        override fun executeInternal(alu: Alu): Int = a.getValue(alu) * b.getValue(alu)
    }

    internal class Div(a: Variable, val b: Placeholder): AluInstruction(a) {
        override fun executeInternal(alu: Alu): Int = a.getValue(alu) / b.getValue(alu)
    }

    internal class Mod(a: Variable, val b: Placeholder): AluInstruction(a) {
        override fun executeInternal(alu: Alu): Int = a.getValue(alu) % b.getValue(alu)
    }

    internal class Eql(a: Variable, val b: Placeholder): AluInstruction(a) {
        override fun executeInternal(alu: Alu): Int {
            return if (a.getValue(alu) == b.getValue(alu)) {
                1
            } else {
                0
            }
        }
    }
}

internal typealias Program = List<AluInstruction>

internal class Alu(_inputs: List<Int>, val program: Program) {
    val inputs = _inputs.toMutableList()
    fun readInput(): Int {
        return inputs.removeFirst()
    }

    fun execute() {
        program.forEach { instruction -> instruction.execute(this) }
    }

    var w: Int = 0
    var x: Int = 0
    var y: Int = 0
    var z: Int = 0
}

internal sealed class Placeholder {
    abstract fun getValue(alu:Alu): Int
}

internal data class Number(val value:Int) : Placeholder() {
    override fun getValue(alu: Alu): Int {
        return value
    }
}
internal sealed class Variable : Placeholder() {
    abstract override fun getValue(alu:Alu): Int
    abstract fun setValue(value: Int, alu:Alu)

    object W : Variable() {
        override fun getValue(alu: Alu): Int = alu.w
        override fun setValue(value: Int, alu: Alu) {
            alu.w = value
        }
    }

    object X : Variable() {
        override fun getValue(alu: Alu): Int = alu.x
        override fun setValue(value: Int, alu: Alu) {
            alu.x = value
        }
    }
    object Y : Variable() {
        override fun getValue(alu: Alu): Int = alu.y
        override fun setValue(value: Int, alu: Alu) {
            alu.y = value
        }
    }

    object Z : Variable() {
        override fun getValue(alu: Alu): Int = alu.z
        override fun setValue(value: Int, alu: Alu) {
            alu.z = value
        }
    }
}

internal fun monad(input: Long, program: Program): Boolean {
    val programInput = input.toString().toCharArray().map { it.toString().toInt() }
    val alu = Alu(programInput, program).apply { execute() }
    return alu.z == 0
}