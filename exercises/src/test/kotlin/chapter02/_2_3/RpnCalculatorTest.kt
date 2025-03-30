package me.exercises.chapter02._2_3

import me.exercises.chapter02._2_3.RpnCalculator.calc
import org.junit.jupiter.api.Test
import strikt.api.expectThat
import strikt.assertions.isEqualTo

class RpnCalculatorTest {

    @Test
    fun `reverse polish notation calculate`() {
        val res1 = calc("4 5 +")
        val res2 = calc("6 2 /")
        val res3 = calc("5 6 2 1 + / *")
        val res4 = calc("2 5 * 4 + 3 2 * 1 + /")
        expectThat(res1).isEqualTo(9.0)
        expectThat(res2).isEqualTo(3.0)
        expectThat(res3).isEqualTo(10.0)
        expectThat(res4).isEqualTo(2.0)
    }
}

