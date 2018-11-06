package com.github.am4dr.rokusho.core.util

// TODO StringTagData, MapTagDataからなるsealed class化
class DataObject(data: Map<String, String> = mapOf()) {

    private val data: Map<String, String> = data.toMap()
    fun asMap(): Map<String, String> = data

    val keys: Set<String> get() = data.keys
    val entries: Set<Pair<String, String>> by lazy { data.entries.map { it.key to it.value }.toSet() }

    operator fun get(key: String): String? = data[key]
    fun getEntries(keys: Set<String>): Set<Pair<String, String>> = keys.filter(data::containsKey).map { it to data[it]!! }.toSet()


    fun put(key: String, value: String): DataObject = if (data[key] != value) modify { it[key] = value } else this
    fun remove(key: String): DataObject = if (data.containsKey(key)) modify { it.remove(key) } else this
    fun merge(other: DataObject): DataObject = modify { it.putAll(other.data) }
    private fun modify(modifier: (MutableMap<String, String>) -> Unit): DataObject = DataObject(data.toMutableMap().apply(modifier))


    fun diff(other: DataObject): Diff = Diff.of(this, other)

    data class Diff(val leftOnlyKeys: Set<String>, val rightOnlyKeys: Set<String> , val conflictedKeys: Set<String>) {
        companion object {
            fun of(left: DataObject, right: DataObject): Diff {
                val lk = left.keys
                val rk = right.keys
                return Diff(lk.subtract(rk), rk.subtract(lk), lk.intersect(rk).filter { left[it] != right[it] }.toSet())
            }
        }
        fun isFound(): Boolean = !isNotFound()
        fun isNotFound(): Boolean = leftOnlyKeys.isEmpty() && rightOnlyKeys.isEmpty() && conflictedKeys.isEmpty()
    }

    override fun equals(other: Any?): Boolean {
        other as? DataObject ?: return false
        return diff(other).isNotFound()
    }

    override fun hashCode(): Int {
        return data.hashCode()
    }

    override fun toString(): String = data.toString()
}
