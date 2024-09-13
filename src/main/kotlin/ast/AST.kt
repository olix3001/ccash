package xyz.olix3001.ast

interface ASTNode
interface ASTItem : ASTNode
interface ASTExpr : ASTNode
interface ASTStmt : ASTExpr

class ASTModule(
    val items: List<ASTItem>,
) : ASTItem