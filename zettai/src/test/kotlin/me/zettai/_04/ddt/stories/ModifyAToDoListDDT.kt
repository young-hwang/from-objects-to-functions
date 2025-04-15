package me.zettai._04.ddt.stories

import com.ubertob.pesticide.core.DDT
import com.ubertob.pesticide.core.DomainDrivenTest
import me.zettai._04.ddt.tooling.ToDoListOwner
import me.zettai._04.ddt.tooling.ZettaiActions
import me.zettai._04.ddt.tooling.allActions

class ModifyAToDoListDDT : DomainDrivenTest<ZettaiActions>(allActions()) {
    val ann by NamedActor(::ToDoListOwner)

    @DDT
    fun `The list owner can add new items`() = ddtScenario {
        setUp {
            ann.`starts with a lists`("diy", emptyList())
        }.thenPlay(
            ann.`can add #item to the #listname`("paint the shelf", "diy"),
            ann.`can add #item to the #listname`("fix the gate", "diy"),
            ann.`can add #item to the #listname`("change the lock", "diy"),
            ann.`can see #listname with #itemnames`(
                "diy",
                listOf("fix the gate", "paint the shelf", "fix the gate")
            )

        )
    }

}
