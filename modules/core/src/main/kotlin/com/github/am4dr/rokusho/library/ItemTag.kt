package com.github.am4dr.rokusho.library

class ItemTag(
    private val id: Any,
    val tag: Tag,
    val data: DataObject
) : Entity<ItemTag> {


    constructor(tag: Tag, data: DataObject) : this(Any(), tag, data)
    constructor(tag: Tag, data: Map<String, String>) : this(Any(), tag, DataObject(data))
    constructor(tag: Tag) : this(tag, DataObject())


    fun update(tag: Tag): ItemTag =
        ItemTag(id, tag, data)

    fun has(tag: Tag): Boolean =
        tag.isSameEntity(tag)

    operator fun get(key: String): String? {
        return data[key] ?: tag[key]
    }

    override fun isSameEntity(other: ItemTag): Boolean =
        other.id === id

    override fun hashCode(): Int = id.hashCode()

    override fun equals(other: Any?): Boolean =
        other is ItemTag && isSameEntity(other)

    override fun toString(): String = "ItemTag(${tag.name}, $data)"
}