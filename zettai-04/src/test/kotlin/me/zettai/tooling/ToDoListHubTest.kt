package me.zettai.tooling

import me.zettai.domain.ListName
import me.zettai.domain.ToDoItem
import me.zettai.domain.ToDoList
import me.zettai.domain.User
import me.zettai.domain.ZettaiHub
import me.zettai.tools.ToDoListOwner
import org.junit.jupiter.api.Test
import strikt.api.expectThat
import strikt.assertions.isEqualTo
import java.time.LocalDate

class ToDoListHubTest {
    val frank = ToDoListOwner("frank")
    val foodToBuy = listOf("carrot", "apples", "milk")
    val frankList = createTodoList("shopping", foodToBuy)

    val bob = ToDoListOwner("bob")
    val gardenItems = listOf("fix the fence", "mowing the lawn")
    val bobList = createTodoList("gardening", gardenItems)

    val listMap = mapOf(
        frank.asUser() to listOf(frankList),
        bob.asUser() to listOf(bobList),
    )

    fun ToDoListOwner.asUser(): User = User(name)

    @Test
    fun `get list by user and name`() {
        val hub = ToDoListHub(listMap)
        val myList = hub.getList(frank.asUser(), frankList.listName)
        expectThat(myList).isEqualTo(frankList)
    }

    private fun createTodoList(
        listName: String,
        items: List<String>
    ): ToDoList =
        ToDoList(
            ListName(listName),
            items.map { ToDoItem(it, LocalDate.now()) })
}

class ToDoListHub(val lists: Map<User, List<ToDoList>>) : ZettaiHub {
    override fun getList(user: User, listName: ListName): ToDoList? =
        lists[user]?.firstOrNull { it.listName == listName }

    override fun addItemToList(
        user: User,
        listName: ListName,
        item: ToDoItem
    ): ToDoList? {
        TODO("Not yet implemented")
    }
}
