package com.github.am4dr.rokusho.core.metadata

// TODO StringTagData, MapTagDataからなるsealed class化
class TagData(data: Map<String, String>) {

    private val data: Map<String, String> = data.toMap()

    val keys: Set<String> get() = data.keys
    val entries: Set<Pair<String, String>> by lazy { data.entries.map { it.key to it.value }.toSet() }

    operator fun get(key: String): String? = data[key]
    fun getEntries(keys: Set<String>): Set<Pair<String, String>> = keys.filter(data::containsKey).map { it to data[it]!! }.toSet()


    fun put(key: String, value: String): TagData = if (data[key] != value) modify { it[key] = value } else this
    fun remove(key: String): TagData = if (data.containsKey(key)) modify { it.remove(key) } else this
    fun merge(other: TagData): TagData = modify { it.putAll(other.data) }
    private fun modify(modifier: (MutableMap<String, String>) -> Unit): TagData = TagData(data.toMutableMap().apply(modifier))


    fun diff(other: TagData): Diff = Diff.of(this, other)

    data class Diff(val leftOnlyKeys: Set<String>, val rightOnlyKeys: Set<String> , val conflictedKeys: Set<String>) {
        companion object {
            fun of(left: TagData, right: TagData): Diff {
                val lk = left.keys
                val rk = right.keys
                return Diff(lk.subtract(rk), rk.subtract(lk), lk.intersect(rk).filter { left[it] != right[it] }.toSet())
            }
        }
    }

    override fun toString(): String = data.toString()
}
