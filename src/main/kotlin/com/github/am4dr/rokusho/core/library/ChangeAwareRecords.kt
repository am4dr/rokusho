package com.github.am4dr.rokusho.core.library

import javafx.collections.FXCollections
import javafx.collections.MapChangeListener
import javafx.collections.ObservableList
import javafx.collections.WeakMapChangeListener

class ChangeAwareRecords<T>(records: List<Record<T>>, private val library: Library<T>) : ObservableList<Record<T>> by FXCollections.observableArrayList(records) {

    private val tagsListener = MapChangeListener<String, Tag> { c ->
        updateRecordsAll { it.itemTags.any { it.tag.id == c.key } }
    }
    private val recordListener = MapChangeListener<T, Record<T>> { c ->
        updateRecordsAll { it.key == c.key }
    }
    private fun updateRecordsAll(start: Int = 0, predicate: (Record<T>)->Boolean) {
        (start..lastIndex).forEach { index ->
            val record = get(index).takeIf(predicate) ?: return@forEach
            val original = library.records[record.key]
            if (original === record) return@forEach

            if (original == null) {
                removeAt(index)
                return updateRecordsAll(index, predicate)
            }
            set(index, original)
        }
    }

    init {
        library.tags.addListener(WeakMapChangeListener(tagsListener))
        library.records.addListener(WeakMapChangeListener(recordListener))
    }
}
