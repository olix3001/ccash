package xyz.olix3001.ast

public interface ASTType : ASTNode

public data class IntType(val width: Int) : ASTType