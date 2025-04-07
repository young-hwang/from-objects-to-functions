package me.zettai._03._01

import org.eclipse.jetty.http.HttpTester.parseResponse
import org.http4k.client.JettyClient
import org.http4k.core.Method
import org.http4k.core.Request
import org.http4k.core.Status
import org.http4k.server.Jetty
import org.http4k.server.asServer
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.fail
import strikt.api.expectThat
import strikt.api.expectThrows
import strikt.assertions.containsExactly
import strikt.assertions.isEqualTo

interface ScenarioActor {
    val name: String
}

class ToDoListOwner(override val name: String): ScenarioActor {
    fun canTheList(listName: String, items: List<String>) {
        val expectedList = createTodoList(listName, items)
        val list = getToList(name, listName)
        expectThat(list).isEqualTo(expectedList)
    }

    private fun getToList(name: String, listName: String): ToDoList {
        val client = JettyClient()
        val request = Request(Method.GET, "http://localhost:8081/todo/$name/$listName")
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

    private fun createTodoList(listName: String, items: List<String>): ToDoList =
        ToDoList(ListName(listName), items.map { ToDoItem(it)})
}

class SeeATodoListAT {
    @Test
    fun `List owners can see their lists`() {
        val listName = "shopping"
        val foodToBuy = listOf("carrot", "apples", "milk")
        val frank = ToDoListOwner("frank")
        startTheApplication(frank.name, listName, foodToBuy)
        frank.canTheList(listName, foodToBuy)
    }

    @Test
    fun `Only owners can see their lists`() {
        val listName = "shopping"
        startTheApplication("frank", listName, emptyList())
        expectThrows<AssertionError> {
            getToDoList("bob", listName)
        }
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
        val server = Zettai(lists).asServer(Jetty(8081))
        server.start()
    }
}

