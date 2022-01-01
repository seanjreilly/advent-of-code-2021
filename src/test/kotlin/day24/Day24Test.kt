package day24

import org.junit.jupiter.api.Test
import utils.readInput

class Day24Test {
    @Test
    fun `parse should generate a program given a list of instructions`() {
        val sourceCode = """
            inp x
            mul x -1
        """.trimIndent().lines()

        val program: Program  = parse(sourceCode)

        assert(program.size == 2)

        val instruction0 = program[0] as? AluInstruction.Inp
        assert(instruction0 != null)
        assert(instruction0?.a == Variable.X)

        val instruction1 = program[1] as? AluInstruction.Mul
        assert(instruction1 != null)
        assert(instruction1?.a == Variable.X)
        assert(instruction1?.b == Number(-1))
    }

    @Test
    fun `parse should generate programs that work`() {
        val sourceCode = """
            inp z
            inp x
            mul z 3
            eql z x
        """.trimIndent().lines()

        val program = parse(sourceCode)
        val alu = Alu(listOf(1, 3), program)
        alu.execute()
        assert(alu.z == 1)

        val alu2 = Alu(listOf(2, 3), program)
        alu2.execute()
        assert(alu2.z == 0)
    }

    @Test
    fun `parse should generate programs that use all instructions`() {
        val sourceCode = """
            inp w
            add z w
            mod z 2
            div w 2
            add y w
            mod y 2
            div w 2
            add x w
            mod x 2
            div w 2
            mod w 2
        """.trimIndent().lines()

        val program = parse(sourceCode)
        val alu = Alu(listOf(9), program).apply { execute() }
        assert(alu.w == 1)
        assert(alu.x == 0)
        assert(alu.y == 0)
        assert(alu.z == 1)
    }

    @Test
    fun `the alu should run the MONAD program on a 14 digit integer`() {
        val monadProgram = parse(readInput("Day24"))
        assert(monad(99893999291967L, monadProgram)) //part 1 solution
        assert(!monad(11111111111111L, monadProgram)) //silly input
        assert(!monad(13579246899999L, monadProgram)) //input from the AoC question
    }

    @Test
    fun `the number returned by part 1 should pass when executed by the monad program`() {
        val input = readInput("Day24")
        val monadProgram = parse(input)
        assert(monad(part1(input), monadProgram))
    }
}