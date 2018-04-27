package com.github.am4dr.rokusho.app.datastore

interface Serializer<in T> {
    fun serialize(data: T): ByteArray
}
