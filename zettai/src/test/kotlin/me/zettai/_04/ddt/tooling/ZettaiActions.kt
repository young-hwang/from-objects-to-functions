package me.zettai._04.ddt.tooling

import com.ubertob.pesticide.core.DdtActions
import com.ubertob.pesticide.core.DdtProtocol
import me.zettai._04.domain.ListName
import me.zettai._04.domain.ToDoItem
import me.zettai._04.domain.ToDoList
import me.zettai._04.domain.User
import me.zettai._04.domain.ZettaiError
import me.zettai._04.fp.Outcome

interface ZettaiActions : DdtActions<DdtProtocol> {
    fun ToDoListOwner.`starts with a lists`(listName: String, items: List<String>)
    fun ToDoListOwner.`starts with some lists`(lists: Map<String, List<String>>) =
        lists.forEach { (listName, items) -> `starts with a lists`(listName, items) }

    fun getToDoList(user: User, listName: ListName): Outcome<ZettaiError, ToDoList>
    fun addListItem(user: User, listName: ListName, item: ToDoItem)
    fun allUserLists(user: User): Outcome<ZettaiError, List<ListName>>
    fun whatsNext(user: User): Outcome<ZettaiError, List<ToDoItem>>
}
