package com.github.am4dr.rokusho.core.metadata

class Record(val id: RecordID, val tags: Set<RecordTag> = setOf()) {

    override fun equals(other: Any?): Boolean = other is Record && other.id == id
    override fun hashCode(): Int = id.hashCode()
    override fun toString(): String = "Record(id=$id, tags=$tags)"
}