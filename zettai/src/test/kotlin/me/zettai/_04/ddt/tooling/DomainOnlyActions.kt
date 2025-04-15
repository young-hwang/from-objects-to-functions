package me.zettai._04.ddt.tooling

import com.ubertob.pesticide.core.DdtProtocol
import com.ubertob.pesticide.core.DomainSetUp
import me.zettai._04.domain.ListName
import me.zettai._04.domain.ToDoItem
import me.zettai._04.domain.ToDoList
import me.zettai._04.domain.User
import me.zettai._04.domain.ZettaiError
import me.zettai._04.fp.Outcome

class DomainOnlyActions : ZettaiActions{

    private val hub = prepareToDoListHubForTests()

    override fun getToDoList(user: User, listName: ListName): Outcome<ZettaiError, ToDoList> = hub.getList(user, listName)

    override fun addListItem(user: User, listName: ListName, item: ToDoItem) = hub.

    override fun allUserLists(user: User): Outcome<ZettaiError, List<ListName>> {
        TODO("Not yet implemented")
    }

    override fun whatsNext(user: User): Outcome<ZettaiError, List<ToDoItem>> {
        TODO("Not yet implemented")
    }

    override val protocol: DdtProtocol
        get() = TODO("Not yet implemented")

    override fun prepare(): DomainSetUp {
        TODO("Not yet implemented")
    }

    override fun ToDoListOwner.`starts with a lists`(listName: String, items: List<String>);

}
