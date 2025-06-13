package me.zettai.ui

import me.zettai.domain.ToDoItem
import me.zettai.domain.ToDoList
import me.zettai.domain.ToDoStatus
import me.zettai.function.unlessNullOrEmpty
import java.time.LocalDate
import java.time.format.DateTimeFormatter

data class HtmlPage(val raw: String)

// 스포크
fun renderPage(toDoList: ToDoList): HtmlPage =
    HtmlPage(
        """
        <!DOCTYPE html>
        <html>
        <body>
            <h1>Zettai</h1>
            <h2>${toDoList.listName.name}</h2>
            <table>
                <tbody>${toDoList.renderItems()}</tbody>
            </table>
        </body>
        </html>
    """.trimIndent()
    )

private fun ToDoList.renderItems() = items.map(::renderItem).joinToString("")


private fun renderItem(it: ToDoItem): String = """<tr>
        <td>${it.description}</td>
        <td>${it.dueDate?.toIsoString().orEmpty()}</td>
        <td>${it.status}</td>
    </tr>""".trimIndent()


fun LocalDate.toIsoString(): String = format(DateTimeFormatter.ISO_LOCAL_DATE)

fun String?.toIsoLocalDate(): LocalDate? = unlessNullOrEmpty {
    LocalDate.parse(
        this,
        DateTimeFormatter.ISO_LOCAL_DATE
    )
}

fun String.toStatus(): ToDoStatus = ToDoStatus.valueOf(this)
