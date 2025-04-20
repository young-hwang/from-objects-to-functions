package me.zettai.domain

interface ZettaiHub {
    fun getList(user: User, listName: ListName): ToDoList?
}

//class ToDoListHub(val lists: Map<User, List<ToDoList>>) : ZettaiHub {
class ToDoListHub(private val fetcher: ToDoListUpdatableFetcher) : ZettaiHub {
    override fun getList(user: User, listName: ListName): ToDoList? =
        fetcher(user, listName)
}
