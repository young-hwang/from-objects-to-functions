package me.zettai.tools

import com.ubertob.pesticide.core.DdtProtocol
import com.ubertob.pesticide.core.DomainSetUp
import com.ubertob.pesticide.core.Http
import com.ubertob.pesticide.core.Ready
import me.zettai.Zettai
import me.zettai.domain.ListName
import me.zettai.domain.ToDoItem
import me.zettai.domain.ToDoList
import me.zettai.domain.ToDoListFetchFromMap
import me.zettai.domain.ToDoListHub
import me.zettai.domain.ToDoStatus
import me.zettai.domain.User
import me.zettai.ui.HtmlPage
import org.http4k.client.JettyClient
import org.http4k.core.Method
import org.http4k.core.Request
import org.http4k.core.Response
import org.http4k.core.Status
import org.http4k.core.body.Form
import org.http4k.core.body.toBody
import org.http4k.server.Jetty
import org.http4k.server.asServer
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import strikt.api.expectThat
import strikt.assertions.isEqualTo
import java.time.LocalDate

data class HttpActions(val env: String = "local") : ZettaiActions {

    private val fetcher = ToDoListFetchFromMap(mutableMapOf())
    private val hub = ToDoListHub(fetcher)

    val port = 8081
    val server = Zettai(hub).asServer(Jetty(port))
    val client = JettyClient()

    override val protocol: DdtProtocol = Http(env)

    override fun prepare(): DomainSetUp {
        server.start()
        return Ready
    }

    override fun tearDown(): HttpActions = also { server.stop() }

    override fun getToDoList(user: User, listName: ListName): ToDoList? {
        val response = callZettai(Method.GET, "/todo/$user/$listName")

        if (response.status == Status.NOT_FOUND)
            return null

        expectThat(response.status).isEqualTo(Status.OK)

        val html = HtmlPage(response.bodyString())

        val items = extractItemsFromPage(html)
        return ToDoList(listName, items)
    }

    private fun extractItemsFromPage(html: HtmlPage) =
        html.parse()
            .select("tr")
            .filter { it.select("td").size == 3 }
            .map {
                Triple<String, LocalDate?, ToDoStatus>(
                    it.select("td")[0].text().orEmpty(),
                    it.select("td")[1].text().toIsoLocalDate(),
                    it.select("td")[2].text().orEmpty().toStatus()
                )
            }
//            .map { (name, date, status) ->
//                ToDoItem(name, date, status)
//            }

    private fun HtmlPage.parse(): Document = Jsoup.parse(raw)

    override fun addListItem(user: User, listName: ListName, item: ToDoItem) {
        val response = submitToZettai(
            todoListUrl(user, listName),
            listOf("itemname" to item.description, "itemdue" to item.dueDate?.toString())
        )
        expectThat(response.status).isEqualTo(Status.SEE_OTHER)
    }

    override fun ToDoListOwner.`starts with a list`(listName: String, items: List<String>) {
        TODO("Not yet implemented")
    }

    private fun parseResponse(html: String): ToDoList {
        val nameRegex = "<h2>.*<".toRegex()
        val listName = ListName(extractListName(nameRegex, html))
        val itemsRegex = "<td>.*?<".toRegex()
        val items = itemsRegex.findAll(html)
            .map { ToDoItem(extractItemDesc(it)) }.toList()
        return ToDoList(listName, items)
    }

    private fun todoListUrl(user: User, listName: ListName): String = "todo/${user.name}/${listName.name}"

    private fun submitToZettai(path: String, webForm: Form): Response =
        client(log(Request(Method.POST, "http://localhost:$port/$path").body(webForm.toBody())))

    private fun callZettai(method: Method, path: String): Response =
        client(log(Request(Method.GET, "http://localhost:$port/$path")))

    fun <T> log(something: T): T {
        println("--- $something")
        return something
    }
}
