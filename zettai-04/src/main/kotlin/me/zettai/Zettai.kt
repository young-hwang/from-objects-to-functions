package me.zettai

import me.zettai.domain.ListName
import me.zettai.domain.ToDoList
import me.zettai.domain.User
import me.zettai.domain.ZettaiHub
import me.zettai.ui.HtmlPage
import me.zettai.ui.renderPage
import org.http4k.core.HttpHandler
import org.http4k.core.Method
import org.http4k.core.Request
import org.http4k.core.Response
import org.http4k.core.Status
import org.http4k.routing.bind
import org.http4k.routing.path
import org.http4k.routing.routes

typealias FUN<A, B> = (A) -> B

infix fun <A, B, C> FUN<A, B>.antThen(other: FUN<B, C>): FUN<A, C> =
    { a: A -> other(this(a)) }


data class Zettai(val hub: ZettaiHub) : HttpHandler {
    val routes = routes(
        "/todo/{user}/{list}" bind (Method.GET) to ::showList
    )

    override fun invoke(request: Request): Response = routes(request)

    // 스포크
    fun extractListData(request: Request): Pair<User, ListName> =
        User(request.path("user").orEmpty()) to ListName(
            request.path("list").orEmpty()
        )

    // 허브
    fun fetchListContent(listId: Pair<User, ListName>): ToDoList =
        hub.getList(listId.first, listId.second) ?: error("List unknown")

    // 스포크
    fun createResponse(html: HtmlPage): Response =
        Response(Status.OK).body(html.raw)

    fun showList(request: Request): Response {
        val user = request.path("user").orEmpty().let(::User)
        val listName = request.path("list").orEmpty()
            .let(ListName.Companion::fromUntrusted)

        return listName
            ?.let({ hub.getList(user, it) })
            ?.let(::renderPage)
            ?.let(::createResponse)
            ?: Response(Status.NOT_FOUND)
    }
}
