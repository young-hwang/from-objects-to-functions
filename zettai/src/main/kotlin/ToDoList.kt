package me.zettai

data class ToDoList(val listName: ListName, val items: List<ToDoItem>)

data class ListName(val listName: String)

data class User(val name: String)

data class ToDoItem(val description: String)

enum class ToDoStatus { Todo, InProgress, Done, Blocked }
