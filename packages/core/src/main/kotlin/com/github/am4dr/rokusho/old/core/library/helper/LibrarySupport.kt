package com.github.am4dr.rokusho.old.core.library.helper

import com.github.am4dr.rokusho.javafx.collection.toObservableList
import com.github.am4dr.rokusho.old.core.library.ItemTag
import com.github.am4dr.rokusho.old.core.library.Record
import com.github.am4dr.rokusho.old.core.library.Tag
import javafx.collections.FXCollections
import javafx.collections.MapChangeListener
import javafx.collections.ObservableList
import javafx.collections.ObservableMap

class LibrarySupport<T>(records: List<Record<T>> = listOf(), tags: List<Tag> = listOf()) {
    val recordMap: ObservableMap<T, Record<T>> = records.associateByTo(FXCollections.observableHashMap(), Record<T>::key)
    val records: ObservableList<Record<T>> = toObservableList(recordMap)
    val tags: ObservableMap<String, Tag> = tags.associateByTo(FXCollections.observableHashMap(), Tag::id)

    private val recordsChangeListener = MapChangeListener<T, Record<T>> { c ->
        if (c.wasAdded()) {
            c.valueAdded.itemTags.map(ItemTag::tag).forEach {
                if (!this.tags.containsKey(it.id) || this.tags[it.id] != it) {
                    this.tags[it.id] = it
                }
            }
        }
    }
    private val tagsChangeListener = MapChangeListener<String, Tag> { c ->
        if (!c.wasRemoved()) return@MapChangeListener

        this.recordMap.forEach { _, r ->
            if (r.itemTags.any { it.tag == c.valueRemoved }) {
                if (c.wasAdded()) {
                    updateTagInRecords(r, c.valueAdded)
                }
                else {
                    removeTagInRecords(r, c.valueRemoved)
                }
            }
        }
    }
    private fun updateTagInRecords(record: Record<T>, updated: Tag) {
        this.recordMap[record.key] = record.copy(itemTags = record.itemTags.map {
            if (it.tag.id == updated.id) {
                it.copy(tag = updated)
            } else {
                it
            }
        })
    }
    private fun removeTagInRecords(record: Record<T>, removed: Tag) {
        this.recordMap[record.key] = record.copy(itemTags = record.itemTags.filter { it.tag != removed })
    }
    init {
        this.tags.addListener(tagsChangeListener)
        this.recordMap.addListener(recordsChangeListener)
    }

    fun updateItemTags(key: T, itemTags: Iterable<ItemTag>) {
        recordMap[key] = Record(key, itemTags.toList())
    }
}