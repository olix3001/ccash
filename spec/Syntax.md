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
- `getter` - This is only available in method functions. It basically allows you to call 
function without need for parenthesis after call.
- `setter` - Allows you to use `a.b = c` instead of `a.setB(c)`.

Function arguments can also have attributes and are defined in a following format:
```
<attributes...> <name>: <type> [= <default>]
```

### Compact functions
If your function is a single line, you may want to use shorthand syntax to define It.
Let's say you have a function which adds two numbers. There is no point in
having brackets around It, so you can use this syntax instead:
```
func add(a: int32, b: int32) = a + b
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

### Ordinal structs
You may not want to specify field names sometimes. Then you can use
ordinal structs, which are just syntax sugar for defining fields with names 0, 1, 2, etc...

Example of such struct would be `struct Foo(int32, int64, float16)`. 
Its fields can then be accessed using `value.0`, `value.1`, etc... syntax.

### Marker structs
Sometimes you also just want to use structs as kind of markers, you can totally skip
defining fields by putting semicolon after structs name: `struct FooBar;`

The compiler will try to get rid of this during compilation, so except for passing
vtables It should be zero-sized!

# Enums
C$ enums are made to be a powerful tool, just like in rust, 
they are actually tagged unions, meaning each variant can contain some arbitrary
values.

You can define enum using the following syntax:
```
<attributes...>
<modifiers...> enum <name> {
    <variants...>
}
```

Available modifiers are exactly the same as on structs.

Variants are defined as markers (they are then treated just like integers with minimum size to fit all the variants):
```
enum Foo {
    bar,
    baz
}
```

Or as struct variants (Its size is the size of largest variant + minimum integer type to fit all variants):
```
enum Foo {
    bar(int32, float16),
    baz {
        field_a: int32,
        field_b: boolean
    }
}
```

# Implementations
Separating methods from values and types is beneficial, as It allows you
to split complex data structures into multiple files.
That's why implementing methods on structs, enums and so on has special syntax.

Let's say you want to have `greet` method on your `User(String)` struct.
You can use the following syntax to implement It:
```
implement User {
    func greet(self) {
        println("Hi " + self.0 + "!")
    }
}
```

As you can see method in this implementation takes `self` argument. This
means that It takes instance to be called on, if you don't take this
argument in your method, It becomes static.

# Interfaces
Interfaces allow you to define common behavior among multiple structs, enums and other types.
They are similar to rust traits and can be implemented using `implement`.

Let's say you have two structs `Cat` and `Dog`, and you want them to have a method for printing the sound
those animals make. You may implement method on both of them like usually,
but you may then want to have a function which takes any animal.

You can do this properly using interfaces:
```
struct Cat;
struct Dog;

interface Animal {
    func make_sound(self) -> String
}

implement Animal on Cat {
    func make_sound(self) = "Meow!"
}

implement Animal on Dog {
    func make_sound(self) = "Bark!"
}
```

After doing this you can make a function which takes any animal!
```
func print_sound(animal: Animal) =
    println(animal.make_sound())
```

# Code blocks
Now we get to the juicy parts of the language meaning function calls, variables, etc...

## Variables
Variables are essential to every programming language, in C$ they use the following syntax:
```
<modifiers...> var <name> [: <type>] = <value>
```

As you may have noticed there is no option to skip initializer, as in C$ there are no `null`s.
Instead, we use `Option<T>` enum which has two variants `some(T)` and `none`. There is also `?` syntax
to make using It easier, so `Option<int32>` is the same as `int32?`

## Struct and enum literals
There are no constructors in C$, so creating a struct or enum variant uses specialized
literal syntax:
```
struct Foo(int32, int32)
func test_foo() = Foo(1, 2)

// Or

struct Bar {
    field_a: int32,
    field_b: int32
}
func test_bar() = Bar {
    field_a: 1,
    field_b: 2
}
```

However, compiler is kinda smart, so It can sometimes derive struct or enum type from context.
In those situations (like function calls or variable assignment) you may skip struct or enum name
and replace it with a dot:
```
// Lets use Bar from previous example.
// And lets say we have somewhere `func print_bar(bar: Bar)`
func main() {
    print_bar(.{
        field_a: 1,
        field_b: 2
    })
}
```
The same goes with enums:
```
let hello: int32? = .some(5) // You can also use Option.some(5) or Option.<int32>.some(5)
```

## Function calls
Function calls have been used in previous examples, so you probably already know something about them.
When calling a function you can either use ordered syntax (`call(1, 2, 3)`) or named syntax 
(`call(a: 1, b: 2, c: 3)`) for default arguments.

If the last thing that comes in a function is a closure, you may use trailing closure syntax:
```
call(1, 2) {
    // Some random code inside closure
}
```

## More to come as I make the language
I also plan on making iterators, easy async/await, threading, match expressions and more