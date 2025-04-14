package me.zettai._04.commands

import me.zettai._04.domain.ToDoListRetriever
import me.zettai._04.fp.Outcome
import me.zettai._04.fp.OutcomeError

class ToDoListCommandHandler(val entityRetriever: ToDoListRetriever) :
        (ToDoListCommand) -> Outcome<OutcomeError, List<ToDoListEvent>> {

    override fun invoke(command: ToDoListCommand): Outcome<OutcomeError, List<ToDoListEvent>> =
        when (command) {
            is CreateToDoList -> command.execute()
            is AddToDoItem -> command.execute()
        }

    private fun CreateToDoList.execute(): Outcome<OutcomeError, List<ToDoListEvent>> {
        val listState = entityRetriever.retrieveByName(user, name) ?: InitialState

    }
}
