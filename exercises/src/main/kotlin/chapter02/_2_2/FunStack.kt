package me.exercises.chapter02._2_2

class FunStack<T>(private val elements: List<T> = emptyList()) {
    fun push(element: T): FunStack<T> = FunStack(listOf(element).plus(elements))
    fun size(): Int = elements.size
    fun pop(): Pair<T, FunStack<T>> = elements.first() to FunStack(elements.drop(1))
}
