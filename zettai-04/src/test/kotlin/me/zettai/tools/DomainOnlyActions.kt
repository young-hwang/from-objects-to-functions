package me.zettai.tools

import com.ubertob.pesticide.core.DdtProtocol
import com.ubertob.pesticide.core.DomainOnly
import com.ubertob.pesticide.core.DomainSetUp
import com.ubertob.pesticide.core.Ready
import me.zettai.domain.ListName
import me.zettai.domain.ToDoItem
import me.zettai.domain.ToDoList
import me.zettai.domain.ToDoListFetchFromMap
import me.zettai.domain.ToDoListHub
import me.zettai.domain.User

class DomainOnlyActions : ZettaiActions {
    override val protocol: DdtProtocol = DomainOnly
    override fun prepare(): DomainSetUp = Ready

    private val store: MutableMap<User, MutableMap<ListName, ToDoList>> =
        mutableMapOf()
    private val fetcher = ToDoListFetchFromMap(store)
    private val hub = ToDoListHub(fetcher)

    override fun ToDoListOwner.`starts with a list`(
        listName: String,
        items: List<String>
    ) {
        val newList = ToDoList.build(listName, items)
        fetcher.assignListToUser(user, newList)
    }

    override fun getToDoList(user: User, listName: ListName): ToDoList? =
        hub.getList(user, listName)

    override fun addListItem(user: User, listName: ListName, item: ToDoItem) {
        hub.addItemToList(user, listName, item)
    }
}
