package me.zettai.ui

import me.zettai.domain.ToDoItem
import me.zettai.domain.ToDoList

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
                <tbody>${renderItems(toDoList.items)}</tbody>
            </table>
        </body>
        </html>
    """.trimIndent()
    )

private fun ToDoList.renderItems() = {}

fun renderItems(items: List<ToDoItem>): String = items.map {
    """<tr><td>${it.description}</td></tr>""".trimIndent()
}.joinToString("")

