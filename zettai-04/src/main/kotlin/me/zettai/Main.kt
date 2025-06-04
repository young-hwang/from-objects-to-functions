package me.zettai

import me.zettai.domain.ListName
import me.zettai.domain.ToDoItem
import me.zettai.domain.ToDoList
import me.zettai.domain.ToDoListFetchFromMap
import me.zettai.domain.ToDoListHub
import me.zettai.domain.ToDoStatus.Done
import me.zettai.domain.ToDoStatus.InProgress
import me.zettai.domain.User
import org.http4k.core.HttpHandler
import org.http4k.server.Jetty
import org.http4k.server.asServer
import java.time.LocalDate

fun main() {
    val fetcher = ToDoListFetchFromMap(storeWithExampleData())
    val hub = ToDoListHub(fetcher)
    val app: HttpHandler = Zettai(hub)
    app.asServer(Jetty(8080)).start()
    println("Server started at http://localhost:8080/todo/young/book")
}

fun storeWithExampleData(): MutableMap<User, MutableMap<ListName, ToDoList>> =
    mutableMapOf(
        User("young") to
                mutableMapOf(exampleToDoList().listName to exampleToDoList())
    )

private fun exampleToDoList(): ToDoList =
    ToDoList(
        listName = ListName("book"),
        items = listOf(
            ToDoItem("prepare the diagram", LocalDate.now().plusDays(1), Done),
            ToDoItem("rewrite explanations", LocalDate.now().plusDays(2), InProgress),
            ToDoItem("finish the chapter"),
            ToDoItem("draft next chapter")
        )
    )
