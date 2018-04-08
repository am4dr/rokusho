package com.github.am4dr.rokusho.core.library.helper

import com.github.am4dr.rokusho.core.library.Record
import com.github.am4dr.rokusho.javafx.collection.ObservableSubMap
import com.github.am4dr.rokusho.javafx.collection.toObservableList
import javafx.collections.ObservableList

class ChangeAwareRecords<T>(records: List<Record<T>>, private val librarySupport: LibrarySupport<T>) : ObservableList<Record<T>> by toObservableList(ObservableSubMap(librarySupport.records, records.map(Record<T>::key)))