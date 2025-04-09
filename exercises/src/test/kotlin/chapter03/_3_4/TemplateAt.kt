package me.exercise.chapter03._3_4

import org.junit.jupiter.api.Test
import strikt.api.expectThat
import strikt.assertions.isEqualTo

data class StringTag(val text: String)

infix fun String.tag(value: String): Pair<String, StringTag> = this to StringTag(value)

fun renderTemplate(template: String, data: Map<String, StringTag>) =
    data.entries.fold(template) { acc, item -> acc.replace("{${item.key}}", item.value.text) }

class TemplateAt {

    @Test
    fun `String template test`() {
        val template = """
            Happy Birthday {name} {surname}!
            from {sender}
        """.trimIndent()

        val data = mapOf("name" tag "John", "surname" tag "Smith", "sender" tag "Bob")

        val actual = renderTemplate(template, data)

        val expected = """
            Happy Birthday John Smith!
            from Bob
        """.trimIndent()

        expectThat(actual).isEqualTo(expected)
    }
}
