package me.exercises.chapter02._2_3

import me.exercises.chapter02._2_2.FunStack

object RpnCalculator {
    val operators = mapOf<String, (Double, Double) -> Double>(
        "+" to { a: Double, b: Double -> a + b },
        "-" to { a: Double, b: Double -> a - b },
        "*" to { a: Double, b: Double -> a * b },
        "/" to { a: Double, b: Double -> a / b },
    )

    val stack: FunStack<Double> = FunStack()

    fun calculate(stack: FunStack<Double>, token: String): FunStack<Double> {
        if (operators.containsKey(token)) {
            val (a, funStack1) = stack.pop()
            val (b, funStack2) = funStack1.pop()
            return funStack2.push(operators[token]?.invoke(b, a) ?: error("Undefined operator $token"))
        } else {
            return stack.push(token.toDouble())
        }
    }

    fun calc(expression: String): Double {
        val split = expression.split(" ")
            .fold(FunStack(), ::calculate)
            .pop()
        return split.first
    }
}
