package me.zettai.domain

import java.time.LocalDate

val pathElementPattern = Regex("""[a-zA-Z0-9\-]+""")

data class ToDoList(val listName: ListName, val items: List<ToDoItem>) {
    companion object {
        fun build(listName: String, items: List<String>): ToDoList =
            ToDoList(ListName(listName), items.map { ToDoItem(it) })
    }
}

data class ListName(val name: String) {
    companion object {
        fun fromUntrustedOrThrow(name: String): ListName =
            fromUntrusted(name) ?: throw IllegalArgumentException("Invalid list name $name")

        fun fromUntrusted(name: String): ListName? =
            if (name.matches(pathElementPattern) && name.length in 1..40) fromTrusted(name) else null

        fun fromTrusted(name: String): ListName = ListName(name)
    }
}

data class ToDoItem(val description: String, val dueDate: String)
