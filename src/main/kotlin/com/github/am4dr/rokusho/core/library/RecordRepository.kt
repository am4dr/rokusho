package com.github.am4dr.rokusho.core.library

interface RecordRepository<T> {
    fun getRecord(key: T): Record<T>
    fun getRecordList(list: Iterable<T>): ObservableRecordList<T>
    fun getAllRecords(): Set<Record<T>>
}