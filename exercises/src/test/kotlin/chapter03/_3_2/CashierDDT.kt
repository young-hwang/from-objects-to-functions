package me.exercises.chapter03._3_2

import com.ubertob.pesticide.core.DDT
import com.ubertob.pesticide.core.DdtActions
import com.ubertob.pesticide.core.DdtActor
import com.ubertob.pesticide.core.DdtProtocol
import com.ubertob.pesticide.core.DomainDrivenTest
import com.ubertob.pesticide.core.DomainOnly
import com.ubertob.pesticide.core.DomainSetUp
import com.ubertob.pesticide.core.Ready
import me.exercies.chapter03._3_1.Item
import me.exercies.chapter03._3_1.Item.carrot
import me.exercies.chapter03._3_1.Item.milk
import me.exercise.chapter03._3_1.Cashier
import strikt.api.expectThat
import strikt.assertions.isEqualTo

interface CashierActions : DdtActions<DdtProtocol> {
    fun setupPrices(prices: Map<Item, Double>)
    fun setup3x2(item: Item)
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
    override fun setup3x2(item: Item) = cashier.put3x2(item)

    override fun totalFor(actorName: String): Double = cashier.totalFor(actorName)

    override fun addItem(actorName: String, qty: Int, item: Item) =
        cashier.addItem(actorName, qty, item)

    override val protocol: DdtProtocol
        get() = DomainOnly

    override fun prepare(): DomainSetUp = Ready
}

class CashierDDT : DomainDrivenTest<CashierActions>(allActions) {
    val alice by NamedActor(::Customer)

    @DDT
    fun `customer can by an item`() = ddtScenario {
        val prices = mapOf(carrot to 2.0, milk to 5.0)
        setUp {
            setupPrices(prices)
        }.thenPlay(
            alice.`can add #qty #item`(3, carrot),
            alice.`can add #qty #item`(1, milk),
            alice.`check total is #total`(11.0)
        )
    }

    @DDT
    fun `customer can benefit from 3x2 offer`() = ddtScenario {
        val prices = mapOf(carrot to 2.0, milk to 5.0)
        setUp {
            setupPrices(prices)
            setup3x2(milk)
        }.thenPlay(
            alice.`can add #qty #item`(3, carrot),
            alice.`can add #qty #item`(3, milk),
            alice.`check total is #total`(16.0)
        )
    }

}
