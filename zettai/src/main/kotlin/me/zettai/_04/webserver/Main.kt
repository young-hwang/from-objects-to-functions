package me.zettai._04.webserver

import me.zettai._04.commands.ToDoListCommandHandler
import me.zettai._04.events.ToDoListEventStore
import me.zettai._04.events.ToDoListEventStreamer

fun main() {
    val streamer = ToDoListEventStreamer()
    val eventStop = ToDoListEventStore(streamer)

    val commandHandler = ToDoListCommandHandler(eventStop)
}
