package me.zettai._03._04

import org.junit.jupiter.api.Test
import strikt.api.expectThat
import strikt.assertions.isEqualTo

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

    private fun createTodoList(listName: String, items: List<String>): ToDoList =
        ToDoList(ListName(listName), items.map { ToDoItem(it)})
}
