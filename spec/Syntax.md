# C$ Syntax specification
This document describes the syntax of C$ programming language.
It is separated into language components starting from most primitive to the more advanced ones.

# Introduction
C$ is a modern LLVM-backed compiled programming language. Despite being native It is not meant
for neither low-level nor systems-programming purposes.
It uses ARC (Automatic Reference Counting) to manage memory under the hood,
so that you don't need to worry about allocations and memory safety.

# Functions
Functions are the most basic component of C$ language as It is required for every
program to have `main` function defined.

They are defined using the following syntax:
```
<attributes...>
<modifiers...> func <name>(<arguments...>) -> <return type> {
    <code>
}
```

For example, we can define `print_hello` function like this:
```
func print_hello(times: int32) {
    // implementation
}
```

Available modifiers for a function are:
- `public` - Mark function as public so that It is available from other parts of your program.
- `private` - Explicitly mark function as private. (this is the default)
- `infix` - Marks function as infix operator so It can be used using `<object a> <name> <object b>` syntax.
- `async` - Makes function asynchronous.
- `native` - Tells compiler that this function is declared elsewhere and will be linked later on.

Function arguments can also have attributes and are defined in a following format:
```
<attributes...> <name>: <type>
```

# Structures
C$ is not following the OOP trend, so It does not have classes, but there are structures instead.
They are different in a way that they only group some data together, but do not
hold metadata and cannot be inherited.

They are defined using the following syntax:
```
<attributes...>
<modifiers...> struct <name> [: <bases>] {
    <fields...>
}
```

Let's start from the beginning: modifiers. 
Structs also allow visibility modifiers `public` and `private` as described in
`Functions` section.
There are also modifiers that are only available on structs:
- `packed` - Ensures that data takes only minimum required memory to hold Its fields. More about this later on.

### Structure's bases
This feature may seem similar to OOP languages at a first glance, but It is not exactly same.
Structures may extend other structures, but It comes with limitations.
Extending a struct will put all base structs fields into the new one, but this means
there can be no conflict between field names in those structs.

```
struct Foo {
    field_a: int32,
    field_b: int32
}

// This is fine
struct Bar : Foo {
    field_c: int32
}

// Error: struct 'Baz' tries to extend `Foo`, but field `field_a` conflicts with Its base.
struct Baz : Foo {
    field_a: int32
}
```

Extending a struct will also allow you to call functions defined on base struct, but
It is recommended to use traits for such behavior.

### Packed structs
Sometimes your application needs to hold huge amounts of data or runs
in an environment with not much memory. You can use `packed` modifier on a structure
to minimize memory used, but It comes at a cost of more computations needed to extract
data. For example using normal structures this:
```
struct Foo {
    field_a: int32, // 4 bytes
    field_b: boolean, // 1 byte
    field_c: int4, // 1 byte
    field_d: float12 // 2 bytes
}
// I know, strange types, but this is an example
```
will take 8 bytes, which is not much, but if we have a lot of those objects It may stack up.

However, using packed structs we get this:
```
packed struct Foo {
    field_a: int32, // 4 bytes
    field_b: boolean, // 1 bit
    field_c: int4, // 4 bits
    field_d: float12 // 12 bits
}
```

In this case `field_b`, `field_c` and `field_d` will be combined into one `int17` value,
meaning our struct will take only 4 bytes giving us 50% memory reduction!

