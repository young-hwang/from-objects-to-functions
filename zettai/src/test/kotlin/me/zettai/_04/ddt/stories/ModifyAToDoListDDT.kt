package me.zettai._04.ddt.stories

import com.ubertob.pesticide.core.DDT
import com.ubertob.pesticide.core.DomainDrivenTest
import me.zettai._04.ddt.tooling.ToDoListOwner
import me.zettai._04.ddt.tooling.ZettaiActions
import me.zettai._04.ddt.tooling.allActions

class ModifyAToDoListDDT : DomainDrivenTest<ZettaiActions>(allActions()){
    val ann by NamedActor(::ToDoListOwner)

    @DDT
    fun `The list owner can add new items`() = ddtScenario {
        setUp {
            ann.`starts with a lists`("diy", emptyList())
        }.thenPlay(
        )
    }

}
