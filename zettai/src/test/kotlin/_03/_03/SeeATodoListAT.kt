package me.zettai._03._03

import org.http4k.client.JettyClient
import org.http4k.core.Method
import org.http4k.core.Request
import org.http4k.core.Status
import org.http4k.core.Uri
import org.http4k.core.then
import org.http4k.filter.ClientFilters
import org.http4k.server.Jetty
import org.http4k.server.asServer
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.fail
import strikt.api.expectThrows

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
    fun `List owners can see their lists`() {
        val listName = "shopping"
        val foodToBuy = listOf("carrot", "apples", "milk")
        val frank = ToDoListOwner("frank")
        startTheApplication(frank.name, listName, foodToBuy)
        frank.canSeeTheList(listName, foodToBuy)
    }

    @Test
    fun `Only owners can see their lists`() {
        val listName = "shopping"
        startTheApplication("frank", listName, emptyList())
        expectThrows<AssertionError> {
            getToDoList("bob", listName)
        }
    }

    @Test
    fun `List owners can see their lists by app`() {
        val app = startTheApplication(lists)
        app.runScenario {
            frank.canSeeTheList("shopping", foodToBuy,  it)
            bob.canSeeTheList("gardening", gardenItems, it)
        }
    }

    @Test
    fun `Only owners can see their lists by app`() {
        val app = startTheApplication(lists)
        app.runScenario {
            frank.cannotSeeTheList("gardening", it)
            bob.cannotSeeTheList("shopping", it)
        }
    }

    @Test
    fun `List owners can see their lists with actions`() {
        val app = startTheApplication(lists)
        app.runScenario (
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

    fun getToDoList(user: String, listName: String): ToDoList {
        val client = JettyClient()
        val request = Request(Method.GET, "http://localhost:8081/todo/$user/$listName")
        val response = client(request)
        return if (response.status == Status.OK)
            parseResponse(response.bodyString())
        else
            fail(response.toMessage())
    }

    private fun parseResponse(html: String): ToDoList {
        val nameRegex = "<h2>.*<".toRegex()
        val listName = ListName(extractListName(nameRegex, html))
        val itemsRegex = "<li>.*?<".toRegex()
        val items = itemsRegex.findAll(html).map { ToDoItem(extractItemDesc(it))}.toList()
        return ToDoList(listName, items)
    }

    private fun extractItemDesc(matchResult: MatchResult): String =
        matchResult.value.substringAfter("<td>").dropLast(1)

    private fun extractListName(nameRegex: Regex, html: String): String =
        nameRegex.find(html)?.value
            ?.substringAfter("<h2>")
            ?.dropLast(1)
            .orEmpty()

    fun startTheApplication(user: String, listName: String, items: List<String>) {
        val toDoList = ToDoList(ListName(listName), items.map(::ToDoItem))
        val lists = mapOf(User(user) to listOf(toDoList))
        val server = Zettai(ToDoListHub(lists)).asServer(Jetty(8081))
        server.start()
    }

    fun startTheApplication(lists: Map<User, List<ToDoList>>): ApplicationForAT {
        val port = 8081
        val server = Zettai(ToDoListHub(lists)).asServer(Jetty(port))
        server.start()
        val client = ClientFilters
            .SetBaseUriFrom(Uri.of("http://localhost:$port/"))
            .then(JettyClient())
        return ApplicationForAT(client, server)
    }

    private fun createTodoList(listName: String, items: List<String>): ToDoList =
        ToDoList(ListName(listName), items.map { ToDoItem(it)})
}

