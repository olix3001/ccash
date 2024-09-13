parser grammar CCashParser;

options { tokenVocab = CCashLexer; }

ccashFile
    : NL* item* EOF
    ;

item
    : annotation* (
        functionDeclaration
    );

functionValueParam
    : parameter
    ;

functionValueParameters
    : LPAREN NL* (functionValueParam (NL* COMMA NL* functionValueParam)* (NL* COMMA)?)? NL* RPAREN
    ;

functionDeclaration
    : FUNC NL* Identifier
      NL* functionValueParameters
      (NL* ARROW NL* type)?
      (NL* functionBody)
    ;

functionBody
    : block
    | ASSIGN NL* expression
    ;

block
    : LCURLY NL* statements NL* RCURLY
    ;

statements
    : (statement (semis statement)*)? semis?
    ;

statement
    : item | expression
    ;

expression
    : primaryExpression
    ;

primaryExpression
    : Identifier
    ;

annotation
    : (AT Identifier NL*)+
    ;

parameter
    : Identifier NL* COLON NL* type
    ;

type
    : OBJECT
    | INT_TYPE_LITERAL
    | FLOAT_TYPE_LITERAL
    ;

semi: (SEMICOLON | NL) NL*;
semis: (SEMICOLON | NL)+;