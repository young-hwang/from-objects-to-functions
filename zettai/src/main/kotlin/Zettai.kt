package me.zettai

import org.http4k.core.*
import org.http4k.routing.bind
import org.http4k.routing.path
import org.http4k.routing.routes

class Zettai() : HttpHandler {
    val routes = routes(
        "/todo/{user}/{list}" bind (Method.GET) to ::showList
    )

    override fun invoke(request: Request): Response = routes(request)

    fun showList(req: Request): Response {
        val user: String? = req.path("user")
        val list: String? = req.path("list")
        val htmlPage = """
        <!DOCTYPE html>
        <html>
        <head></head>
        <body>
            <h1>Zettai</h1>
            <p>Here is the list <b>$list</b> of user <b>${user}</b></p>
        </body>
        </html>"""
        return Response(Status.OK).body(htmlPage)
    }
}
