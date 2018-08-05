package com.github.am4dr.rokusho.core.metadata

class DefaultMetaDataRepositoryImpl(tags: Set<Tag> = setOf(), records: Set<Record> = setOf()) : MetaDataRepository {

    private val tags = tags.associateByTo(mutableMapOf(), Tag::name)
    private val records = records.associateByTo(mutableMapOf(), Record::id)


    override fun getTagNames(): Set<TagName> = tags.keys
    override fun get(name: TagName): Tag? = tags[name]
    override fun getTags(): Set<Tag> = tags.values.toSet()
    override fun updateTagDataDefaultValues(name: TagName, data: TagData): Tag? = tags.replace(name, Tag(name, data))


    override fun getRecordIDs(): Set<RecordID> = records.keys.toSet()
    override fun getRecords(): Set<Record> = records.values.toSet()
    override fun getRecord(id: RecordID): Record? = records[id]
    override fun updateRecordTags(id: RecordID, tags: Set<RecordTag>): Record? = records.replace(id, Record(id, tags.toSet()))
}
