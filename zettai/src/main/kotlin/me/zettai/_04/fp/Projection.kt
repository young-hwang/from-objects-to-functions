package me.zettai._04.fp

interface Projection<R : Any, E : EntityEvent> {
    val eventProjector: (E) -> List<DeltaRow<R>>
    val eventFetcher: (EventSeq) -> Sequence<StoredEvent<E>>
    fun lastProjectedEvent(): EventSeq
    fun update() {
        eventFetcher(lastProjectedEvent())
            .forEach { storedEvent ->
                applyDelta(
                    storedEvent.eventSeq,
                    eventProjector(storedEvent.event)
                )
            }
    }

    fun applyDelta(eventSeq: EventSeq, deltas: List<DeltaRow<R>>)
}

data class RowId(val id: String) {}

sealed class DeltaRow<R : Any> {}

data class CreateRow<R : Any>(val rowId: RowId, val row: R) : DeltaRow<R>()
data class DeleteRow<R : Any>(val rowId: RowId) : DeltaRow<R>()
data class UpdateRow<R : Any>(val rowId: RowId, val updateRow: R.() -> R) : DeltaRow<R>()

fun <T : Any> DeltaRow<T>.toSingle(): List<DeltaRow<T>> = listOf(this)
