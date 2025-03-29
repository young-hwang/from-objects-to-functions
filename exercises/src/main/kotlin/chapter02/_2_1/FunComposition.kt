package me.exercises.chapter02._2_1

typealias FUN<A, B> = (A) -> B
infix fun<A, B, C> FUN<A, B>.antThen(other: FUN<B, C>): FUN<A, C> = { a: A -> other(this(a)) }
