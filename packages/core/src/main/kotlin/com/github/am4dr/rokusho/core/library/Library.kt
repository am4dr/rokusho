package com.github.am4dr.rokusho.core.library

import com.github.am4dr.rokusho.core.item.ItemID
import com.github.am4dr.rokusho.core.metadata.BaseTag
import kotlin.reflect.KClass

interface Library<T : Any> {

    val type: KClass<T>

    fun getIDs(): Set<ItemID>
    fun get(id: ItemID): LibraryItem<out T>?
    fun add(item: LibraryItem<out T>): LibraryItem<out T>?
    fun remove(id: ItemID): LibraryItem<out T>?
    fun has(id: ItemID): Boolean

    fun getTags(): Set<BaseTag>
}