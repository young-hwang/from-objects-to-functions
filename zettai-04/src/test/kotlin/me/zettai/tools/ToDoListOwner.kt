package me.zettai.tools

import com.ubertob.pesticide.core.DdtActor
import com.ubertob.pesticide.core.DdtStep
import me.zettai.domain.ListName
import me.zettai.domain.ToDoItem
import me.zettai.domain.User
import strikt.api.expectThat
import strikt.api.expectThrows
import strikt.assertions.containsExactlyInAnyOrder
import strikt.assertions.isNotNull

class ToDoListOwner(override val name: String) : DdtActor<ZettaiActions>() {
    val user = User(name)

    fun `can see #listname with #itemnames`(
        listName: String,
        expectedItems: List<String>
    ): DdtStep<ZettaiActions, Unit> =
        step(listName, expectedItems) {
            val list = getToDoList(user, ListName.fromUntrustedOrThrow(listName))
            expectThat(list)
                .isNotNull()
                .get { items.map { it.description } }
                .containsExactlyInAnyOrder(expectedItems)
        }

    fun `cannot see #listname`(listName: String): DdtStep<ZettaiActions, Unit> =
        step(listName) {
            expectThrows<AssertionError> {
                getToDoList(user, ListName.fromUntrustedOrThrow(listName))
            }
        }

    fun `can add #item to #listname`(itemName: String, listName: String): DdtStep<ZettaiActions, Unit> =
        step(itemName, listName) {
            val item = ToDoItem(itemName)
            addListItem(user, ListName.fromUntrustedOrThrow(listName), item)
        }
}
