package com.github.am4dr.rokusho.core.library

import com.github.am4dr.rokusho.core.item.ItemID
import com.github.am4dr.rokusho.core.metadata.Tag
import kotlin.reflect.KClass

interface Library<T : Any> {

    val type: KClass<T>

    fun getIDs(): Set<ItemID>
    fun get(id: ItemID): TaggedItem<out T>?

    fun getTags(): Set<Tag>
}