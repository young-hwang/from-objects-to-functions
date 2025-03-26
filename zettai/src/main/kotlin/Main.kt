package me

import org.http4k.core.*
import org.http4k.routing.bind
import org.http4k.routing.routes
import org.http4k.server.Jetty
import org.http4k.server.asServer

val app: HttpHandler = routes(
    "/greetings" bind(Method.GET) to ::greetings,
    "/data" bind(Method.GET) to ::receiveData
)

fun greetings(req: Request): Response = Response(Status.OK).body(htmlPage)

fun receiveData(req: Request): Response = Response(Status.CREATED).body("Received: ${req.bodyString()}")

val htmlPage = """
        <!DOCTYPE html>
        <html>
        <head></head>
        <body>
            <h1 style="text-align:center;font-size:3em">
                Hello Functional World!
            </h1>
        </body>
        </html>
    """


fun main() {
    app.asServer(Jetty(8080)).start()
}
