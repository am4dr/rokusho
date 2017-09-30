package com.github.am4dr.rokusho.core.library

import javafx.collections.FXCollections
import javafx.collections.MapChangeListener
import javafx.collections.ObservableMap

class Library<T>(records: List<Record<T>> = listOf(), tags: List<Tag> = listOf()) {
    val records: ObservableMap<T, Record<T>> = records.associateByTo(FXCollections.observableHashMap(), Record<T>::key)
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

        this.records.forEach { _, r ->
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
        this.records[record.key] = record.copy(itemTags = record.itemTags.map {
            if (it.tag.id == updated.id) {
                it.copy(tag = updated)
            } else {
                it
            }
        })
    }
    private fun removeTagInRecords(record: Record<T>, removed: Tag) {
        this.records[record.key] = record.copy(itemTags = record.itemTags.filter { it.tag != removed })
    }
    init {
        this.tags.addListener(tagsChangeListener)
        this.records.addListener(recordsChangeListener)
    }
}