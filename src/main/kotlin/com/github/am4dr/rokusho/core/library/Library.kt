package com.github.am4dr.rokusho.core.library

import com.github.am4dr.rokusho.app.savefile.Tag    // TODO パッケージ再編 app.savefile.Tag をここで参照したくない
import javafx.beans.property.ReadOnlyMapProperty

/**
 * [Item]のコレクション
 *
 * [Item]は[Item.key]をキーとして識別可能。
 */
interface Library<T> {
    fun getItem(key: T): Item<T>
    fun getItemSet(list: Iterable<T>): ItemSet<T>
    fun getTags(): ReadOnlyMapProperty<String, Tag>
    fun updateItemTags(key: T, tags: Iterable<ItemTag>)
}