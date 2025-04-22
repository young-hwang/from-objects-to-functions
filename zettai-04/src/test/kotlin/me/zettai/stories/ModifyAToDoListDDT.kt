package me.zettai.stories

import com.ubertob.pesticide.core.DDT
import com.ubertob.pesticide.core.DomainDrivenTest
import me.zettai.tools.ToDoListOwner
import me.zettai.tools.ZettaiActions
import me.zettai.tools.allActions
import java.time.LocalDate

class ModifyAToDoListDDT : DomainDrivenTest<ZettaiActions>(allActions()) {

    val ann by NamedActor(::ToDoListOwner)

    @DDT
    fun `the list owner can add new items`() = ddtScenario {
        setUp {
            ann.`starts with a list`("diy", emptyList())
        }.thenPlay(
            ann.`can add #item to #listname`("paint the shelf", "diy"),
            ann.`can add #item to #listname`("fix the gate", "diy"),
            ann.`can add #item to #listname`("change the lock", "diy"),
            ann.`can see #listname with #itemnames`(
                "diy", listOf(
                    "fix the gate", "paint the shelf", "change the lock"
                )
            )
        ).wip(LocalDate.of(2021, 1, 1), "Not implemented yet")
    }
}
