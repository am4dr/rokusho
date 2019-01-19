package com.github.am4dr.rokusho.core.datastore.file

interface Serializer<in T> {
    fun serialize(data: T): ByteArray
}
