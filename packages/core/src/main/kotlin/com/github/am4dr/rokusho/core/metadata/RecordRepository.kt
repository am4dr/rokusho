package com.github.am4dr.rokusho.core.metadata

interface RecordRepository {

    fun getRecordIDs(): Set<RecordID>
    fun getRecords(): Set<Record>
    fun getRecord(id: RecordID): Record?
    fun updateRecordTags(id: RecordID, tags: Set<RecordTag>): Record?
}