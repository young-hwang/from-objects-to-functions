package me.zettai._04.ddt.tooling

import com.ubertob.pesticide.core.DdtActor
import me.zettai._04.domain.ListName
import me.zettai._04.domain.ToDoItem
import me.zettai._04.domain.User
import strikt.api.expectThat

data class ToDoListOwner(override val name: String) : DdtActor<ZettaiActions>() {
    val user = User(name)

    fun `can see #item to #listname`(itemName: String, listName: String) =
        step(itemName, listName) {
            val item = ToDoItem(itemName)
            addListItem(user, ListName(listName), item)
        }

    fun `cannot see #listname`(listName: String) = step(listName) {
        val lists = allUserLists(user).expectSuccess()
        expectThat(lists.map { it.name }).doesNotContain(listName)
    }

    fun `cannot see `
}
