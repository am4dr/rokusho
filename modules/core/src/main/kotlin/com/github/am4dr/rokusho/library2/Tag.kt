package com.github.am4dr.rokusho.library2


/**
 * [Library]によって管理されるタグのデータを表す
 *
 * 実質的に不変
 */
class Tag private constructor(
    private val id: Any,
    val data: TagData
) : Entity<Tag> {

    constructor(data: TagData) : this(Any(), data)

    val name: String get() = data.name

    fun update(data: TagData): Tag =
        Tag(id, data)

    operator fun get(key: String): String? {
        return data.obj[key]
    }

    fun isSameName(other: Tag): Boolean =
        other.name == name

    override fun isSameEntity(other: Tag): Boolean =
        other.id === id

    override fun toString(): String = "Tag(${data.name}, ${data.obj})"
}