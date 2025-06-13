package me.zettai.tools

import com.ubertob.pesticide.core.DdtActions
import com.ubertob.pesticide.core.DdtProtocol
import me.zettai.domain.ListName
import me.zettai.domain.ToDoItem
import me.zettai.domain.ToDoList
import me.zettai.domain.User

interface ZettaiActions : DdtActions<DdtProtocol> {
    fun ToDoListOwner.`starts with a list`(
        listName: String,
        items: List<String>
    )

    fun getToDoList(user: User, listName: ListName): ToDoList?
    fun addListItem(user: User, listName: ListName, item: ToDoItem)
}

fun allActions() = setOf(
    DomainOnlyActions(),
    HttpActions()
)
