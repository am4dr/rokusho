package com.github.am4dr.rokusho.core.metadata

class DefaultMetaDataRepositoryImpl(baseTags: Set<BaseTag> = setOf(), records: Set<Record> = setOf()) : MetaDataRepository {

    private val tags = baseTags.associateByTo(mutableMapOf(), BaseTag::name)
    private val records = records.associateByTo(mutableMapOf(), Record::id)


    override fun getTagNames(): Set<BaseTagName> = tags.keys
    override fun getTags(): Set<BaseTag> = tags.values.toSet()
    override fun get(name: BaseTagName): BaseTag? = tags[name]
    override fun add(tag: BaseTag): BaseTag? = tag.takeIf { tags.put(tag.name, tag) != null } ?: get(tag.name)
    override fun remove(name: BaseTagName): BaseTag? = tags.remove(name)
    override fun has(name: BaseTagName): Boolean = tags.containsKey(name)

    override fun getRecordIDs(): Set<RecordID> = records.keys.toSet()
    override fun getRecords(): Set<Record> = records.values.toSet()
    override fun get(id: RecordID): Record? = records[id]
    override fun add(record: Record): Record? = record.takeIf { records.put(record.id, record) != null } ?: get(record.id)
    override fun remove(id: RecordID): Record? = records.remove(id)
    override fun has(id: RecordID): Boolean = records.containsKey(id)
}
