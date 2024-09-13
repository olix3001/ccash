lexer grammar CCashLexer;

import UnicodeChars;

InlineComment
    : '/*' ( InlineComment | . )*? '*/' -> channel(HIDDEN);
LineComment
    : '//' ~[\r\n]* -> channel(HIDDEN);

WS: [\u0020\u0009\u000C] -> channel(HIDDEN);
NL: '\n' | '\r' '\n'?;

fragment Hidden: InlineComment | LineComment | WS;

// ==< Separators & Operators >==
DOT: '.';
COMMA: ',';
LPAREN: '(';
RPAREN: ')';
LBRACKET: '[';
RBRACKET: ']';
LCURLY: '{';
RCURLY: '}';

STAR: '*';
MOD: '%';
DIV: '/';
PLUS: '+';
MINUS: '-';
INC: '++';
DEC: '--';
CONJ: '&&';
DISJ: '||';
COLON: ':';
SEMICOLON: ';';
ASSIGN: '=';
ADD_ASSIGN: '+=';
SUB_ASSIGN: '-=';
MUL_ASSIGN: '*=';
DIV_ASSIGN: '/=';
MOD_ASSIGN: '%=';

ARROW: '->';
FAT_ARROW: '=>';
RANGE: '..';
RANGE_INCLUSIVE: '..=';
EXPAND: '...';
HASH: '#';
AT: '@';
AMPERSAND: '&';
PIPE: '|';

LANGLE: '<';
RANGLE: '>';
LE: '<=';
GE: '>=';
NE: '!=';
EQ: '==';

// ==< Keywords >==
FUNC: 'func';

OBJECT: 'object';
INT_TYPE_LITERAL: 'int' [0-9]+;
FLOAT_TYPE_LITERAL: 'float' [0.9]+;

// ==< Modifiers >==

// ==< Other >==
fragment Letter
    : UNICODE_CLASS_LU
    | UNICODE_CLASS_LL
    | UNICODE_CLASS_LT
    | UNICODE_CLASS_LM
    | UNICODE_CLASS_LO
    ;

fragment Digit: UNICODE_CLASS_ND;

Identifier
    : (Letter | '_') (Letter | '_' | Digit)*
    ;