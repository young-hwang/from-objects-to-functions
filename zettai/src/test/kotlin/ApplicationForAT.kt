import me.zettai.ListName
import me.zettai.ToDoItem
import me.zettai.ToDoList
import org.eclipse.jetty.http.HttpTester.parseResponse
import org.http4k.core.HttpHandler
import org.http4k.core.Method
import org.http4k.core.Request
import org.http4k.core.Status
import kotlin.test.DefaultAsserter.fail

interface Actions {
    fun getToDoList(user: String, listName: String): ToDoList?
}

typealias Step = Actions.() -> Unit

class ApplicationForAT(val client: HttpHandler, val server: AutoCloseable): Actions {
    fun runScenario(vararg steps: Step) {
        server.use {
            steps.forEach{ step -> step(this)}
        }
    }

    override fun getToDoList(user: String, listName: String): ToDoList {
        val request = Request(Method.GET, "/todo/$user/$listName")
        val response = client(request)
        return if (response.status == Status.OK)
            parseResponse(response.bodyString())
        else
            fail(response.toMessage())
    }

    private fun parseResponse(html: String): ToDoList {
        val nameRegex = "<h2>.*<".toRegex()
        val listName = ListName(extractListName(nameRegex, html))
        val itemsRegex = "<td>.*?<".toRegex()
        val items = itemsRegex.findAll(html)
            .map { ToDoItem(extractItemDesc(it)) }.toList()
        return ToDoList(listName, items)
    }

    private fun extractListName(nameRegex: Regex, html: String): String =
        nameRegex.find(html)?.value
            ?.substringAfter("<h2>")
            ?.dropLast(1).orEmpty()

    private fun extractItemDesc(matchResult: MatchResult): String =
        matchResult.value.substringAfter("<td>").dropLast(1)
}
