package me.zettai._04.fp

import java.util.UUID

data class EntityId(val raw: UUID) {
    companion object {
        fun mint(): EntityId = EntityId(UUID.randomUUID())
        fun fromRowId(rowId: RowId) = EntityId(UUID.fromString(rowId.id))
    }
}

interface EntityEvent {
    val id: EntityId
}

interface EntityState<in E : EntityEvent> {
    fun combine(event: E): EntityState<E>
}
