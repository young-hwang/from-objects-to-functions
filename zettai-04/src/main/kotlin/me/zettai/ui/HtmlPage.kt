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
                <tbody>${toDoList.renderItems()}</tbody>
            </table>
        </body>
        </html>
    """.trimIndent()
    )

private fun ToDoList.renderItems() = items.joinToString("", transform = ::renderItem)

private fun renderItem(it: ToDoItem) = """<tr><td>${it.description}</td></tr>""".trimIndent()

