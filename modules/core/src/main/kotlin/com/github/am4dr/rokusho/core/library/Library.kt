package com.github.am4dr.rokusho.core.library

import com.github.am4dr.rokusho.core.item.Item
import com.github.am4dr.rokusho.core.metadata.BaseTag
import com.github.am4dr.rokusho.core.metadata.PatchedTag
import javafx.beans.property.ReadOnlyListProperty
import javafx.beans.property.ReadOnlySetProperty
import kotlin.reflect.KClass

interface Library<T : Any> {

    val type: KClass<T>
    val name: String
    val shortName: String

    fun getItems(): ReadOnlyListProperty<LibraryItem<out T>>
    fun getIDs(): Set<Item.ID>
    fun get(id: Item.ID): LibraryItem<out T>?
    fun update(id: Item.ID, tags: Set<PatchedTag>): Boolean
    fun has(id: Item.ID): Boolean

    fun getTags(): ReadOnlySetProperty<BaseTag>
    fun parseTag(text: String): PatchedTag?
}