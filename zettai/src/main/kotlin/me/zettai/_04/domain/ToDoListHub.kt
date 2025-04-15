package me.zettai._04.domain

import me.zettai._04.commands.ToDoListCommand
import me.zettai._04.commands.ToDoListCommandHandler
import me.zettai._04.events.ToDoListEvent
import me.zettai._04.fp.Outcome
import me.zettai._04.fp.OutcomeError

sealed class ZettaiError : OutcomeError
data class InvalidRequestError(override val msg: String) : ZettaiError()
data class ToDoListCommandError(override val msg: String) : ZettaiError()

interface ZettaiHub {
    fun handle(command: ToDoListCommand): Outcome<ZettaiError, ToDoListCommand>
    fun getList(user: User, listName: ListName): Outcome<ZettaiError, ToDoList>
    fun getLists(user: User): Outcome<ZettaiError, List<ListName>>
    fun whatsNext(user: User): Outcome<ZettaiError, List<ToDoItem>>
}

class ToDoListHub(
    val queryRunner: ToDoListQueryRunner,
    val commandHandler: ToDoListCommandHandler,
    val persistEvents: EventPersister<ToDoListEvent>
) : ZettaiHub {
    override fun handle(command: ToDoListCommand): Outcome<ZettaiError, ToDoListCommand> =
        commandHandler(command).transform(persistEvents).transform { command }

    override fun getList(user: User, listName: ListName): Outcome<ZettaiError, ToDoList> {
        TODO("Not yet implemented")
    }

    override fun getLists(user: User): Outcome<ZettaiError, List<ListName>> {
        TODO("Not yet implemented")
    }

    override fun whatsNext(user: User): Outcome<ZettaiError, List<ToDoItem>> {
        TODO("Not yet implemented")
    }

}
