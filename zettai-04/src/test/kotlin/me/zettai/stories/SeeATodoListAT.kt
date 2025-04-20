package me.zettai.stories

import me.zettai.HtmlPage
import me.zettai.domain.ListName
import me.zettai.domain.ToDoItem
import me.zettai.domain.ToDoList
import me.zettai.domain.User
import org.http4k.client.JettyClient
import org.http4k.core.HttpHandler
import org.http4k.core.Method
import org.http4k.core.Request
import org.http4k.core.Response
import org.http4k.core.Status
import org.http4k.core.Uri
import org.http4k.core.then
import org.http4k.filter.ClientFilters
import org.http4k.routing.bind
import org.http4k.routing.path
import org.http4k.routing.routes
import org.http4k.server.Jetty
import org.http4k.server.asServer
import org.junit.jupiter.api.Test
import strikt.api.expectThat
import strikt.api.expectThrows
import strikt.assertions.isEqualTo
import kotlin.test.DefaultAsserter

class SeeATodoListAT {
    val frank = ToDoListOwner("frank")
    val foodToBuy = listOf("carrot", "apples", "milk")
    val frankList = createTodoList("shopping", foodToBuy)

    val bob = ToDoListOwner("bob")
    val gardenItems = listOf("fix the fence", "mowing the lawn")
    val bobList = createTodoList("gardening", gardenItems)

    val lists = mapOf(
        frank.asUser() to listOf(frankList),
        bob.asUser() to listOf(bobList),
    )

    fun ToDoListOwner.asUser(): User = User(name)

    @Test
    fun `List owners can see their lists with actions`() {
        val app = startTheApplication(lists)
        app.runScenario(
            frank.canSeeTheListStep("shopping", foodToBuy),
            bob.canSeeTheListStep("gardening", gardenItems)
        )
    }

    @Test
    fun `Only owners can see their lists with actions`() {
        val app = startTheApplication(lists)
        app.runScenario(
            frank.cannotSeeTheList("gardening"),
            bob.cannotSeeTheList("shopping")
        )
    }

    private fun startTheApplication(lists: Map<User, List<ToDoList>>): ApplicationForAT {
        val port = 8081
        val server = Zettai(lists).asServer(Jetty(port))
        server.start()
        val client = ClientFilters
            .SetBaseUriFrom(Uri.of("http://localhost:$port/"))
            .then(JettyClient())
        return ApplicationForAT(client, server)
    }

    private fun createTodoList(listName: String, items: List<String>): ToDoList =
        ToDoList(ListName(listName), items.map { ToDoItem(it) })
}

interface ScenarioActor {
    val name: String
}

interface Actions {
    fun getToDoList(user: String, listName: String): ToDoList?
}

typealias Step = Actions.() -> Unit

class ApplicationForAT(val client: HttpHandler, val server: AutoCloseable) : Actions {
    override fun getToDoList(user: String, listName: String): ToDoList {
        val request = Request(Method.GET, "/todo/$user/$listName")
        val response = client(request)
        return if (response.status == Status.OK)
            parseResponse(response.bodyString())
        else
            DefaultAsserter.fail(response.toMessage())
    }

    fun runScenario(vararg steps: Step) {
        server.use {
            steps.forEach { step -> step(this) }
        }
    }

    private fun parseResponse(html: String): ToDoList {
        val nameRegex = "<h2>.*<".toRegex()
        val listName = ListName(extractListName(nameRegex, html))
        val itemsRegex = "<td>.*?<".toRegex()
        val items = itemsRegex.findAll(html)
            .map { ToDoItem(extractItemDesc(it)) }.toList()
        return ToDoList(listName, items)
    }

    private fun extractListName(nameRegex: Regex, html: String): String =
        nameRegex.find(html)?.value
            ?.substringAfter("<h2>")
            ?.dropLast(1).orEmpty()

    private fun extractItemDesc(matchResult: MatchResult): String =
        matchResult.value.substringAfter("<td>").dropLast(1)
}

class ToDoListOwner(override val name: String) : ScenarioActor {
    fun canSeeTheListStep(listName: String, items: List<String>): Step = {
        val expectedList = createTodoList(listName, items)
        val list = getToDoList(name, listName)
        expectThat(list).isEqualTo(expectedList)
    }

    fun cannotSeeTheList(listName: String): Step = {
        expectThrows<AssertionError> {
            getToDoList(name, listName)
        }
    }

    private fun createTodoList(listName: String, items: List<String>): ToDoList =
        ToDoList(ListName(listName), items.map { ToDoItem(it) })
}

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
