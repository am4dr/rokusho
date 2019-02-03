package com.github.am4dr.rokusho.library2

class ItemTag(
    private val id: Any,
    val tag: Tag,
    val data: TagData
) : Entity<ItemTag> {

    constructor(tag: Tag, data: TagData) : this(Any(), tag, data)


    fun update(tag: Tag): ItemTag =
        ItemTag(id, tag, data)

    fun has(tag: Tag): Boolean =
        tag.isSameEntity(tag)

    override fun isSameEntity(other: ItemTag): Boolean =
        other.id === id
}