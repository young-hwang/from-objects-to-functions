package me.zettai._04.events

import me.zettai._04.domain.ListName
import me.zettai._04.domain.ToDoItem
import me.zettai._04.domain.User
import me.zettai._04.fp.EntityEvent
import me.zettai._04.fp.EntityId
import me.zettai._04.fp.EntityState

sealed class ToDoListEvent : EntityEvent
data class ListCreated(override val id: EntityId, val owner: User, val name: ListName) : ToDoListEvent()
data class ItemAdded(override val id: EntityId, val item: ToDoItem) : ToDoListEvent()
data class ItemRemoved(override val id: EntityId, val item: ToDoItem) : ToDoListEvent()
data class ItemModified(override val id: EntityId, val prevItem: ToDoItem, val item: ToDoItem) : ToDoListEvent()

fun Iterable<ToDoListEvent>.fold(): ToDoListState = fold(InitialState as ToDoListState) { acc, e -> acc.combine(e) }

sealed class ToDoListState : EntityState<ToDoListEvent> {
    abstract override fun combine(event: ToDoListEvent): ToDoListState
}

object InitialState : ToDoListState() {
    override fun combine(event: ToDoListEvent): ToDoListState =
        when (event) {
            is ListCreated -> create(event.id, event.owner, event.name, emptyList())
            else -> this
        }
}

data class ActiveToDoList internal constructor(
    val id: EntityId,
    val owner: User,
    val name: ListName,
    val items: List<ToDoItem>
) : ToDoListState() {
    override fun combine(event: ToDoListEvent): ToDoListState =
        when (event) {
            is ItemAdded ->
        }
}

fun InitialState.create(id: EntityId, owner: User, name: ListName, items: List<ToDoItem>) =
    ActiveToDoList(id, owner, name, items)


