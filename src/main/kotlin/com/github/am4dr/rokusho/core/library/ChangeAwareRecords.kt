package com.github.am4dr.rokusho.core.library

import javafx.collections.MapChangeListener
import javafx.collections.ObservableList
import javafx.collections.WeakMapChangeListener

class ChangeAwareRecords<T>(records: ObservableList<Record<T>>, library: Library<T>): ObservableList<Record<T>> by create(records, library) {
    companion object {
        private fun <T> create(records: ObservableList<Record<T>>, library: Library<T>): ObservableList<Record<T>> {
            return object : ObservableList<Record<T>> by records {
                val tagsListener = MapChangeListener<String, Tag> { c ->
                    records.forEachIndexed { index, record ->
                        if (record.itemTags.any { it.tag.id == c.key }) {
                            library.records[record.key]?.let { set(index, it) }
                        }
                    }
                }
                val recordListener = MapChangeListener<T, Record<T>> { c ->
                    records.forEachIndexed { index, record ->
                        if (record.key == c.key) {
                            library.records[record.key]?.let { set(index, it) }
                        }
                    }
                }
                init {
                    library.tags.addListener(WeakMapChangeListener(tagsListener))
                    library.records.addListener(WeakMapChangeListener(recordListener))
                }
            }
        }
    }
}
