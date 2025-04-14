package me.zettai._04.domain

interface ToDoListRetriever {
    fun retrieveByName(user: User, listName: ListName): ToDoListState?
}
