package me.exercises.chapter03._3_1

import com.ubertob.pesticide.core.DDT
import com.ubertob.pesticide.core.DdtActions
import com.ubertob.pesticide.core.DdtActor
import com.ubertob.pesticide.core.DdtProtocol
import com.ubertob.pesticide.core.DomainDrivenTest
import com.ubertob.pesticide.core.DomainOnly
import com.ubertob.pesticide.core.DomainSetUp
import com.ubertob.pesticide.core.Ready
import me.exercies.chapter03._3_1.Item
import me.exercise.chapter03._3_1.Cashier
import strikt.api.expectThat
import strikt.assertions.isEqualTo

interface CashierActions : DdtActions<DdtProtocol> {
    fun setupPrices(prices: Map<Item, Double>)
    fun totalFor(actorName: String): Double
    fun addItem(actorName: String, qty: Int, item: Item)
}

data class Customer(override val name: String) : DdtActor<CashierActions>() {
    fun `can add #qty #item`(qty: Int, item: Item) = step(qty, item) {
        addItem(name, qty, item)
    }

    fun `check total is #total`(total: Double) = step(total) {
        expectThat(totalFor(name)).isEqualTo(total)
    }
}

val allActions = setOf(DomainActions)

object DomainActions : CashierActions {
    val cashier = Cashier()

    override fun setupPrices(prices: Map<Item, Double>) = cashier.putAll(prices)

    override fun totalFor(actorName: String): Double = cashier.totalFor(actorName)

    override fun addItem(actorName: String, qty: Int, item: Item) = cashier.addItem(actorName, qty, item)

    override val protocol: DdtProtocol
        get() = DomainOnly

    override fun prepare(): DomainSetUp = Ready
}

class CashierDDT : DomainDrivenTest<CashierActions>(allActions) {
    val alice by NamedActor(::Customer)

    @DDT
    fun `customer can by an item`() = ddtScenario {
        val prices = mapOf(Item.carrot to 2.0, Item.milk to 5.0)
        setUp {
            setupPrices(prices)
        }.thenPlay(
            alice.`can add #qty #item`(3, Item.carrot),
            alice.`can add #qty #item`(1, Item.milk),
            alice.`check total is #total`(11.0)
        )
    }

}
