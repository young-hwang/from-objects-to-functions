package me.zettai._03._03

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
