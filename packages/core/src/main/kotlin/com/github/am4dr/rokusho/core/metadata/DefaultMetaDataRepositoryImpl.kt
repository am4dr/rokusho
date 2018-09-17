package com.github.am4dr.rokusho.core.metadata

class DefaultMetaDataRepositoryImpl(baseTags: Set<BaseTag> = setOf(), records: Set<Record> = setOf()) : MetaDataRepository {

    private val tags = baseTags.associateByTo(mutableMapOf(), BaseTag::name)
    private val records = records.associateByTo(mutableMapOf(), Record::id)


    override fun getTagNames(): Set<BaseTagName> = tags.keys
    override fun get(name: BaseTagName): BaseTag? = tags[name]
    override fun getTags(): Set<BaseTag> = tags.values.toSet()
    override fun updateTagData(name: BaseTagName, data: TagData): BaseTag? = tags.replace(name, BaseTag(name, data))


    override fun getRecordIDs(): Set<RecordID> = records.keys.toSet()
    override fun getRecords(): Set<Record> = records.values.toSet()
    override fun getRecord(id: RecordID): Record? = records[id]
    override fun updateRecordTags(id: RecordID, tags: Set<RecordTag>): Record? = records.replace(id, Record(id, tags.toSet()))
}
