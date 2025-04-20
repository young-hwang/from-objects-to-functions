package me.zettai.stories

import com.ubertob.pesticide.core.DDT
import com.ubertob.pesticide.core.DomainDrivenTest
import me.zettai.tools.ToDoListOwner
import me.zettai.tools.ZettaiActions
import me.zettai.tools.allActions

class SeeATodoListDDT : DomainDrivenTest<ZettaiActions>(allActions()) {
    val frank by NamedActor(::ToDoListOwner)
    val shoppingListName = "shopping"
    val foodToBuy = listOf("carrot", "apples", "milk")

    val bob = ToDoListOwner("bob")
    val gardenListName = "gardening"
    val gardenItems = listOf("fix the fence", "mowing the lawn")

    @DDT
    fun `List owners can see their lists`() = ddtScenario {
        setUp {
            frank.`starts with a list`(shoppingListName, foodToBuy)
            bob.`starts with a list`(gardenListName, gardenItems)
        }.thenPlay(
            frank.`can see #listName with #itemnames`("shopping", foodToBuy),
            bob.`can see #listName with #itemnames`("gardening", gardenItems)
        )
    }

    @DDT
    fun `Only owners can see their lists`() = ddtScenario {
        setUp {
            frank.`starts with a list`(shoppingListName, foodToBuy)
            bob.`starts with a list`(gardenListName, gardenItems)
        }.thenPlay(
            frank.`cannot see #listname`(gardenListName),
            bob.`cannot see #listname`(shoppingListName)
        )
    }
}
