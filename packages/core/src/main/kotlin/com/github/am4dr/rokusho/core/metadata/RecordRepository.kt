package com.github.am4dr.rokusho.core.metadata

interface RecordRepository {

    fun getRecordIDs(): Set<RecordID>
    fun getRecords(): Set<Record>
    fun get(id: RecordID): Record?
    fun add(record: Record): Record?
    fun remove(id: RecordID): Record?
    fun has(id: RecordID): Boolean
}