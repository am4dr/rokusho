package com.github.am4dr.rokusho.adapter

import com.github.am4dr.rokusho.core.item.Item
import com.github.am4dr.rokusho.core.library.Library
import com.github.am4dr.rokusho.core.metadata.BaseTag
import javafx.beans.property.ReadOnlyListProperty
import javafx.beans.property.ReadOnlyListWrapper
import javafx.beans.property.ReadOnlyMapProperty
import javafx.beans.property.ReadOnlyMapWrapper
import javafx.collections.FXCollections
import com.github.am4dr.rokusho.old.core.library.ItemTag as OldItemTag
import com.github.am4dr.rokusho.old.core.library.Library as OldLibrary
import com.github.am4dr.rokusho.old.core.library.Record as OldRecord
import com.github.am4dr.rokusho.old.core.library.Tag as OldTag


/**
 * 新しいLibraryインターフェースを古いLibraryインターフェースへ変換するラッパー。
 * 移行中の一時的な実装なのでObservableな機能はない。
 * TODO 移行中の一時的な実装なので機能不足であり、いち早く削除する
 */
class OldLibraryWrapper<T : Any>(val library: Library<T>) : OldLibrary<T> {

    override val tags: ReadOnlyMapProperty<String, OldTag> =
            ReadOnlyMapWrapper(library.getTags().associateByTo(
                    FXCollections.observableHashMap(),
                    { it.name.name },
                    { baseTagToOldTag(it) }))

    override val records: ReadOnlyListProperty<OldRecord<T>> =
            ReadOnlyListWrapper(library.getIDs().mapNotNullTo(FXCollections.observableArrayList(), this::getOldRecordByItemID))

    private val baseTags = library.getTags().associateBy(BaseTag::name)
    override fun updateItemTags(key: T, tags: Iterable<OldItemTag>) {
        val patchedTags = tags.mapNotNull { oldItemTagToPatchedTag(baseTags, it) }.toSet()
        val id = library.getIDs().find { library.get(it)?.get() == key } ?: return
        library.update(id , patchedTags)
    }
    private fun getOldRecordByItemID(id: Item.ID): OldRecord<T>? {
        val item = library.get(id) ?: throw IllegalStateException()
        val tags = item.tags.mapNotNull { patchedTagToOldItemTag(tags::get, it) }
        return OldRecord(item.get(), tags)
    }
}