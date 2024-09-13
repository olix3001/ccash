package xyz.olix3001.ast

open class Parameter(
    val name: String,
    val type: ASTType
) : ASTNode

class ValueParameter(
    name: String,
    type: ASTType,
    val initializer: ASTNode // TODO: Change to expression
) : Parameter(name, type)

class FunctionDefinition(
    val name: String,
    val parameters: List<ValueParameter>,
    val returnType: ASTType,
    val body: ASTNode
) : ASTItem