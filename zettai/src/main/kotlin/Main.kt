package me.zettai

import org.http4k.core.*
import org.http4k.routing.bind
import org.http4k.routing.path
import org.http4k.routing.routes
import org.http4k.server.Jetty
import org.http4k.server.asServer

fun main() {
    val app: HttpHandler = routes(
        "/todo/{user}/{list}" bind(Method.GET) to ::showList
    )
    app.asServer(Jetty(8080)).start()
}

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
        </html>
    """
    return Response(Status.OK).body(htmlPage)
}
