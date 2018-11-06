package com.github.am4dr.rokusho.core.datastore.file

interface Deserializer<T> {

    fun deserialize(bytes: ByteArray): Result<T>

    data class Result<out T>(val result: T? = null, val errors: List<String> = listOf())
}
