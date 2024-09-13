package xyz.olix3001

import org.antlr.v4.runtime.CharStreams
import org.antlr.v4.runtime.CommonTokenStream
import xyz.olix3001.grammar.CCashLexer
import xyz.olix3001.grammar.CCashParser
import xyz.olix3001.passes.Refinement

val code = """
    func hello(a: int32) -> int32 = a
""".trimIndent()

fun main() {
    val lexer = CCashLexer(CharStreams.fromString(code))
    val tokenStream = CommonTokenStream(lexer)
    val parser = CCashParser(tokenStream)

    val ast = Refinement().visit(parser.ccashFile())
}