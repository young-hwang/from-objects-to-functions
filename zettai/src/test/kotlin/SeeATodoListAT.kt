import me.zettai.ToDoList
import me.zettai.Zettai
import org.http4k.client.JettyClient
import org.http4k.core.Method
import org.http4k.core.Request
import org.http4k.core.Response
import org.http4k.core.Status
import org.http4k.server.Jetty
import org.http4k.server.asServer
import org.junit.jupiter.api.Test
import strikt.api.expectThat
import strikt.assertions.isEqualTo
import kotlin.test.DefaultAsserter.fail

class SeeATodoListAT {
    @Test
    fun `List owners can see their lists`() {
        val user = "frank"
        val listName = "shopping"
        val foodToBuy = listOf("carrot", "apples", "milk")
        startTheApplication(user, listName, foodToBuy)
        val list = getToDoList(user, listName)
        expectThat(list.listName.name).isEqualTo(listName)
        expectThat(list.items.map {it.description}).isEqualTo(foodToBuy)
    }

    fun getToDoList(user: String, listName: String): ToDoList {
        val client = JettyClient()
        val request = Request(Method.GET, "http://localhost:8080/todo/$user/$listName")
        val response = client(request)
        return if (response.status == Status.OK)
                parseResonse(response)
            else
                fail(response.toMessage())
    }

    fun parseResonse(html: Response): ToDoList = TODO("parse the response")

    fun startTheApplication(user: String, listName: String, foodToBuy: List<String>) {
        val server = Zettai().asServer(Jetty(8081)).start()
        // 사용자 목록을 설정
    }
}
