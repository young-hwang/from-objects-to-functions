package me.zettai

import me.zettai.domain.ListName
import me.zettai.domain.ToDoItem
import me.zettai.domain.ToDoList
import me.zettai.domain.ToDoListHub
import me.zettai.domain.User
import org.http4k.core.HttpHandler
import org.http4k.server.Jetty
import org.http4k.server.asServer

fun main() {
    val items = listOf("write chapter", "insert code", "draw diagrams")
    val toDoList = ToDoList(ListName("book"), items.map(::ToDoItem))
    val lists = mapOf(User("young") to listOf(toDoList))
    val app: HttpHandler = Zettai(ToDoListHub(lists))
    app.asServer(Jetty(8080)).start()
    println("Server started at http://localhost:8080/todo/young/book")
}
