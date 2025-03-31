import me.zettai.ListName
import me.zettai.ToDoItem
import me.zettai.ToDoList
import me.zettai.User
import me.zettai.Zettai
import org.http4k.client.JettyClient
import org.http4k.core.Uri
import org.http4k.core.then
import org.http4k.filter.ClientFilters
import org.http4k.server.Jetty
import org.http4k.server.asServer
import org.junit.jupiter.api.Test
import strikt.api.expectThat
import strikt.api.expectThrows
import strikt.assertions.isEqualTo

class SeeATodoListAT {
    val frank = ToDoListOwner("frank")
    val shoppingItems = listOf("carrot", "apples", "milk")
    val frankList = createList("shopping", shoppingItems)

    val bob = ToDoListOwner("bob")
    val gardenItems = listOf("fix the fence", "mowing the lawn")
    val bobList = createList("gardening", gardenItems)

    val lists = mapOf(
        frank.asUser() to listOf(frankList),
        bob.asUser() to listOf(bobList)
    )

    fun ToDoListOwner.asUser(): User = User(name)

    @Test
    fun `List owners can see their lists`() {
        val app = startTheApplication(lists)
        app.runScenario {
            frank.canSeeTheList("shopping", shoppingItems, it)
            bob.canSeeTheList("gardening", gardenItems, it)
        }
    }

    @Test
    fun `Only owners can see their lists`() {
        val app = startTheApplication(lists)
        app.runScenario {
            frank.cannotSeeTheList("gardening", it)
            bob.cannotSeeTheList("shopping", it)
        }
    }

    fun startTheApplication(lists: Map<User, List<ToDoList>>): ApplicationForAT {
        val port = 8081
        val server = Zettai(lists).asServer(Jetty(port))
        server.start()
        val client = ClientFilters
            .SetBaseUriFrom(Uri.of("http://localhost:$port/"))
            .then(JettyClient())
        return ApplicationForAT(client, server)
    }

    fun createList(listName: String, items: List<String>): ToDoList =
        ToDoList(ListName(listName), items.map(::ToDoItem))

    interface ScenarioActor {
        val name: String
    }

    class ToDoListOwner(override val name: String) : ScenarioActor {
        fun canSeeTheList(listName: String, items: List<String>, app: ApplicationForAT) {
            val expectedList = createList(listName, items)
            val list = app.getToDoList(name, listName)
            expectThat(list).isEqualTo(expectedList)
        }

        fun cannotSeeTheList(listName: String, app: ApplicationForAT) {
            expectThrows<AssertionError> {
                app.getToDoList(name, listName)
            }
        }

        fun createList(listName: String, items: List<String>): ToDoList =
            ToDoList(ListName(listName), items.map(::ToDoItem))
    }

}
