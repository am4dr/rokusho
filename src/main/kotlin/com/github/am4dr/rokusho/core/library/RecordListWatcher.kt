package com.github.am4dr.rokusho.core.library

import javafx.beans.property.ReadOnlyListProperty
import javafx.beans.property.ReadOnlyListWrapper
import javafx.beans.property.ReadOnlyMapProperty
import javafx.collections.FXCollections
import javafx.collections.MapChangeListener
import javafx.collections.WeakMapChangeListener

// TODO test
class RecordListWatcher<T>(
        private val tags: ReadOnlyMapProperty<String, Tag>,
        private val itemTags: ReadOnlyMapProperty<T, List<ItemTag>>) {

    fun watch(records: List<Record<T>>): Records = Records(records)

    /**
     * [RecordListWatcher]が変更を監視している[Record]のリスト[records]をもつ。
     *
     * [records]に含まれる[Record]インスタンスのいずれかに変更があった場合には、その要素は変更を反映した新しい[Record]のインスタンスで置換される。
     */
    inner class Records(records: List<Record<T>>) {

        private val _records = ReadOnlyListWrapper(FXCollections.observableArrayList<Record<T>>(records))
        val records: ReadOnlyListProperty<Record<T>> = _records.readOnlyProperty

        private val tagsListener = MapChangeListener<String, Tag> { c ->
            _records.filter { it.itemTags.any { it.tag.id == c.key } }.forEach { recreateRecord(it) }
        }
        private val itemTagsListener = MapChangeListener<T, List<ItemTag>> { c ->
            _records.filter { it.key == c.key }.forEach { recreateRecord(it) }
        }
        private fun recreateRecord(record: Record<T>) = _records.set(_records.indexOfFirst { it === record }, record.copy(itemTags = itemTags[record.key] ?: listOf()))

        init {
            tags.addListener(WeakMapChangeListener(tagsListener))
            itemTags.addListener(WeakMapChangeListener(itemTagsListener))
        }
    }
}