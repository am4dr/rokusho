package com.github.am4dr.rokusho.core.metadata

class Record(val key: Key, val tags: Set<PatchedTag> = setOf()) {

    override fun equals(other: Any?): Boolean = other is Record && other.key == key
    override fun hashCode(): Int = key.hashCode()
    override fun toString(): String = "Record($key, tags=$tags)"

    data class Key(val id: String) {
        override fun toString(): String = "Key($id)"
    }
}