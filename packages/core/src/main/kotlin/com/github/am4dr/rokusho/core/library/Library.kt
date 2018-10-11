package com.github.am4dr.rokusho.core.library

import com.github.am4dr.rokusho.core.item.ItemID
import com.github.am4dr.rokusho.core.metadata.BaseTag
import kotlin.reflect.KClass

interface Library<T : Any> {

    val type: KClass<T>

    fun getIDs(): Set<ItemID>
    fun get(id: ItemID): LibraryItem<out T>?
    fun update(id: ItemID, tags: Set<LibraryItemTag>): Boolean
    fun has(id: ItemID): Boolean

    fun getTags(): Set<BaseTag>
}