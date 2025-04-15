package me.zettai._04.commands

import me.zettai._04.domain.ListName
import me.zettai._04.domain.ToDoItem
import me.zettai._04.domain.User
import me.zettai._04.fp.EntityId

sealed class ToDoListCommand {}

data class CreateToDoList(val user: User, val name: ListName) : ToDoListCommand() {
    val id: EntityId = EntityId.mint()
}

data class AddToDoItem(val user: User, val name: ListName, val item: ToDoItem) : ToDoListCommand() {}

