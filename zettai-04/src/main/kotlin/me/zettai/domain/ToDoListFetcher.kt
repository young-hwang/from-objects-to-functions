package me.zettai.domain

interface ToDoListUpdatableFetcher : (User, ListName) -> ToDoList? {
    override fun invoke(p1: User, p2: ListName): ToDoList?
    fun assignListToUser(user: User, list: ToDoList): ToDoList?
}
