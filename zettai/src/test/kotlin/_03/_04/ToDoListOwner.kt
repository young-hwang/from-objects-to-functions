package me.zettai._03._04

import org.http4k.client.JettyClient
import org.http4k.core.Method
import org.http4k.core.Request
import org.http4k.core.Status
import org.junit.jupiter.api.fail
import strikt.api.expectThat
import strikt.api.expectThrows
import strikt.assertions.isEqualTo

interface ScenarioActor {
    val name: String
}

class ToDoListOwner(override val name: String): ScenarioActor {
    fun canSeeTheList(listName: String, items: List<String>) {
        val expectedList = createTodoList(listName, items)
        val list = getToList(name, listName)
        expectThat(list).isEqualTo(expectedList)
    }

    fun canSeeTheList(listName: String, items: List<String>, app: ApplicationForAT) {
        val expectedList = createTodoList(listName, items)
        val list = app.getToDoList(name, listName)
        expectThat(list).isEqualTo(expectedList)
    }

    fun cannotSeeTheList(listName: String, app: ApplicationForAT) {
        expectThrows<AssertionError> {
            app.getToDoList(name, listName)
        }
    }

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
