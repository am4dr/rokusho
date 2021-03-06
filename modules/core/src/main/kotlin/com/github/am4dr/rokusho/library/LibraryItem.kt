package com.github.am4dr.rokusho.library

/**
 * ある[Library]におけるある時点での[Item]とそれが持つ[Tag]の集合を表すデータ
 */
class LibraryItem<T : Any> private constructor(
    private val id: Any,
    val item: Item<T>,
    val tags: Set<ItemTag>
) : Entity<LibraryItem<*>> {

    constructor(item: Item<T>, tags: Set<ItemTag> = setOf()) : this(Any(), item, tags.toSet())


    fun update(tags: Set<ItemTag>): LibraryItem<T> = LibraryItem(id, item, tags)
    fun update(tag: ItemTag): LibraryItem<T> {
        val newTags = tags.toMutableSet()
        newTags.add(tag)
        return update(tags=newTags)
    }
    fun update(tag: Tag): LibraryItem<T> {
        val newTags = tags.map { itemTag ->
            if (itemTag.has(tag)) itemTag.update(tag) else itemTag
        }.toSet()
        return update(tags=newTags)
    }

    fun has(tag: ItemTag): Boolean = tags.contains(tag)
    fun has(tag: Tag): Boolean = tags.any { it.has(tag) }

    override fun isSameEntity(other: LibraryItem<out Any>): Boolean =
        other.id === id

    override fun hashCode(): Int = id.hashCode()

    override fun equals(other: Any?): Boolean =
        other is LibraryItem<*> && isSameEntity(other)

    override fun toString(): String = "LibraryItem(${item.data}, $tags)"
}
