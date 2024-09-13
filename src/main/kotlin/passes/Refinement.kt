package xyz.olix3001.passes

import xyz.olix3001.ast.*
import xyz.olix3001.grammar.CCashParser
import xyz.olix3001.grammar.CCashParserBaseVisitor

class Refinement : CCashParserBaseVisitor<ASTNode>() {
    override fun visitCcashFile(ctx: CCashParser.CcashFileContext?): ASTNode =
        ASTModule(ctx?.item()?.map { it.accept(this) as ASTItem }!!)

    override fun visitFunctionDeclaration(ctx: CCashParser.FunctionDeclarationContext?): ASTNode {
        val params = ctx?.functionValueParameters()?.functionValueParam()
            ?.map { it.accept(this) as ValueParameter }
        return FunctionDefinition(
            ctx?.Identifier()?.text!!,
            params!!,
            ctx.type()?.accept(this) as ASTType,
            ctx.functionBody().accept(this)
        )
    }

    override fun visitFunctionValueParam(ctx: CCashParser.FunctionValueParamContext?): ASTNode {
        val param = ctx?.parameter()?.accept(this) as Parameter
        return ValueParameter(param.name, param.type, IntType(32)) // TODO: Replace with initializer
    }

    override fun visitPrimaryExpression(ctx: CCashParser.PrimaryExpressionContext?): ASTNode {
        return IdentExpression(ctx?.Identifier()?.text!!)
    }

    override fun visitParameter(ctx: CCashParser.ParameterContext?): ASTNode =
        Parameter(ctx?.Identifier()?.text!!, ctx.type().accept(this) as ASTType)

    override fun visitType(ctx: CCashParser.TypeContext?): ASTNode? {
        if (ctx?.INT_TYPE_LITERAL() != null) {
            val width = ctx.INT_TYPE_LITERAL()?.text?.substring(3)?.toInt()!!
            return IntType(width)
        } else if (ctx?.FLOAT_TYPE_LITERAL() != null) {
            val width = ctx.FLOAT_TYPE_LITERAL()?.text?.substring(5)?.toInt()
        }
        return null
    }
}