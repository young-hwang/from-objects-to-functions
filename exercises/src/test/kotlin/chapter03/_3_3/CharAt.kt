package me.exercise.chapter03._3_3

import org.junit.jupiter.api.Test
import strikt.api.expectThat
import strikt.assertions.isEqualTo

class CharAt {
    fun buildCharAtPos(s: String): (Int) -> Char = { i -> s.get(i) }

    @Test
    fun `Character retrieval by index`() {
        val myCharAtPos = buildCharAtPos("Kotlin")
        expectThat(myCharAtPos(0)).isEqualTo('K')
    }
}
