package me.zettai.domain

class ToDoListFetchFromMap(private val store: MutableMap<User, MutableMap<ListName, ToDoList>>) :
    ToDoListUpdatableFetcher {
    override fun invoke(user: User, listName: ListName): ToDoList? = store[user]?.get(listName)

    override fun assignListToUser(user: User, list: ToDoList): ToDoList? =
        store.compute(user) { _, value ->
            val listMap = value ?: mutableMapOf()
            listMap.apply { put(list.listName, list) }
        }?.let { list }
}
