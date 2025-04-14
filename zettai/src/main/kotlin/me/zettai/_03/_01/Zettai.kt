package me.zettai._03._01

import org.http4k.core.HttpHandler
import org.http4k.core.Method
import org.http4k.core.Request
import org.http4k.core.Response
import org.http4k.core.Status
import org.http4k.routing.bind
import org.http4k.routing.path
import org.http4k.routing.routes

typealias FUN<A, B> = (A) -> B

infix fun <A, B, C> FUN<A, B>.antThen(other: FUN<B, C>): FUN<A, C> = { a: A -> other(this(a)) }

data class ToDoList(val listName: ListName, val items: List<ToDoItem>)

data class ListName(val name: String)

data class User(val name: String)

data class ToDoItem(val description: String)

data class HtmlPage(val raw: String)

data class Zettai(val lists: Map<User, List<ToDoList>>) : HttpHandler {
    val routes = routes(
        "/todo/{user}/{list}" bind (Method.GET) to ::showList
    )

    override fun invoke(request: Request): Response = routes(request)

    // 스포크
    fun extractListData(request: Request): Pair<User, ListName> =
        User(request.path("user").orEmpty()) to ListName(request.path("list").orEmpty())

    // 허브
    fun fetchListContent(listId: Pair<User, ListName>): ToDoList =
        lists[listId.first]?.firstOrNull {
            it.listName == listId.second
        } ?: error("List unknown")

    // 스포크
    fun renderHtml(toDoList: ToDoList): HtmlPage = HtmlPage(
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

    fun renderItems(items: List<ToDoItem>): String = items.map {
        """<tr><td>${it.description}</td></tr>""".trimIndent()
    }.joinToString("")

    // 스포크
    fun createResponse(html: HtmlPage): Response = Response(Status.OK).body(html.raw)

    fun showList(request: Request): Response =
        request
            .let(::extractListData)
            .let(::fetchListContent)
            .let(::renderHtml)
            .let(::createResponse)
}
