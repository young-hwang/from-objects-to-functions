package me.zettai._04.ddt.tooling

import com.ubertob.pesticide.core.DdtProtocol
import com.ubertob.pesticide.core.DomainSetUp
import me.zettai._04.domain.ListName
import me.zettai._04.domain.User

data class HttpActions(val env: String = "local") : ZettaiActions {
    override fun ToDoListOwner.`starts with a lists`(listName: String, items: List<String>) {
        TODO("Not yet implemented")
    }

    override fun getToDoList(user: User, listName: ListName): ZettaiOutcome<ToDoList> {
        TODO("Not yet implemented")
    }

    override val protocol: DdtProtocol
        get() = TODO("Not yet implemented")

    override fun prepare(): DomainSetUp {
        TODO("Not yet implemented")
    }
}
