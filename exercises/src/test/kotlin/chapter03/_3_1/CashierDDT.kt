package me.exercises.chapter03._3_1

import com.ubertob.pesticide.core.DDT
import com.ubertob.pesticide.core.DdtActions
import com.ubertob.pesticide.core.DdtActor
import com.ubertob.pesticide.core.DdtProtocol
import com.ubertob.pesticide.core.DomainDrivenTest
import strikt.api.expectThat

enum class Item { carrot, milk }

interface CashierActions: DdtActions<DdtProtocol> {
    fun setupPrices(prices: Map<String, Double>)
    fun totalFor(actorName: String): Double
    fun addItem(actorName: String, qty: Int, item: Item)
}

data class Customer(override val name: String): DdtActor<CashierActions>() {
    fun `can add #qty #item`(quantity: Int, item: Item) = step(quantity, item) {

    }

    fun `check total is #total`(price: Double) {
        expectThat(price).equals(10.0)
    }
}

val allActions = setOf(Item.carrot, Item.milk)

class CashierDDT : DomainDrivenTest<CashierActions>(allActions){
    val alice by NamedActor(::Customer)

    @DDT
    fun `customer can by an item`() = ddtScenario {
        val prices = mapOf(Item.carrot to 2.0, Item.milk to 5.0)
        setUp {
            setupPrices(prices)
        }.thenPlay (
            alice.`can add #qty #item`(3, Item.carrot),
            alice.`can add #qty #item`(1, Item.milk),
            alice.`check total is #total`(11.0)
        )
    }

}
