package com.github.am4dr.rokusho.library


/**
 * [Library]によって管理されるタグのデータを表す
 *
 * 実質的に不変
 */
class Tag private constructor(
    private val id: Any,
    val name: String,
    val data: DataObject
) : Entity<Tag> {

    constructor(data: TagData) : this(Any(), data.name, data.obj)
    constructor(name: String, data: DataObject) : this(Any(), name, data)


    fun update(data: TagData): Tag =
        Tag(id, data.name, data.obj)

    operator fun get(key: String): String? = data[key]

    fun isSameName(other: Tag): Boolean =
        other.name == name

    override fun isSameEntity(other: Tag): Boolean =
        other.id === id

    override fun hashCode(): Int = id.hashCode()

    override fun equals(other: Any?): Boolean =
        other is Tag && isSameEntity(other)

    override fun toString(): String = "Tag($name, $data)"
}