package com.github.am4dr.rokusho.core.library

import com.github.am4dr.rokusho.javafx.collection.ObservableSubMap
import com.github.am4dr.rokusho.javafx.collection.toObservableList
import javafx.collections.ObservableList

class ChangeAwareRecords<T>(records: List<Record<T>>, private val library: Library<T>) : ObservableList<Record<T>> by toObservableList(ObservableSubMap(library.records, records.map(Record<T>::key)))