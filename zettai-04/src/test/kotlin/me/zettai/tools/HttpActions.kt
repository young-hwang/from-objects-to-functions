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
import me.zettai.domain.User
import org.http4k.client.JettyClient
import org.http4k.core.Method
import org.http4k.core.Request
import org.http4k.core.Response
import org.http4k.core.Status
import org.http4k.core.body.Form
import org.http4k.core.body.toBody
import org.http4k.server.Jetty
import org.http4k.server.asServer
import strikt.api.expectThat
import strikt.assertions.isEqualTo

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

    override fun getToDoList(user: User, listName: ListName): ToDoList? = TODO()

    override fun addListItem(user: User, listName: ListName, item: ToDoItem) {
        val response = submitToZettai(
            todoListUrl(user, listName),
            listOf("itemname" to item.description, "itemdue" to item.dueDate?.toString())
        )
        expectThat(response.status).isEqualTo(Status.SEE_OTHER)
    }

    private fun todoListUrl(user: User, listName: ListName): String = "todo/${user.name}/${listName.name}"

    private fun submitToZettai(path: String, webForm: Form): Response =
        client(log(Request(Method.POST, "http://localhost:$port/$path").body(webForm.toBody())))

    fun <T> log(something: T): T {
        println("--- $something")
        return something
    }

    override fun ToDoListOwner.`starts with a list`(listName: String, items: List<String>) {
        TODO("Not yet implemented")
    }
}
