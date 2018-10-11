package com.github.am4dr.rokusho.core.metadata

import com.github.am4dr.rokusho.core.datastore.DataStore
import com.github.am4dr.rokusho.core.datastore.NullDataStore

// TODO use store to save the changes
class DefaultMetaDataRepositoryImpl(baseTags: Set<BaseTag> = setOf(),
                                    records: Set<Record> = setOf(),
                                    store: DataStore<MetaDataRepository> = NullDataStore()) : MetaDataRepository {

    private val tags = baseTags.associateByTo(mutableMapOf(), BaseTag::name)
    private val records = records.associateByTo(mutableMapOf(), Record::key)


    override fun getTagNames(): Set<BaseTag.Name> = tags.keys
    override fun getTags(): Set<BaseTag> = tags.values.toSet()
    override fun get(name: BaseTag.Name): BaseTag? = tags[name]
    override fun add(tag: BaseTag): BaseTag? = tag.takeIf { tags.put(tag.name, tag) != null } ?: get(tag.name)
    override fun remove(name: BaseTag.Name): BaseTag? = tags.remove(name)
    override fun has(name: BaseTag.Name): Boolean = tags.containsKey(name)

    override fun getRecordIDs(): Set<Record.Key> = records.keys.toSet()
    override fun getRecords(): Set<Record> = records.values.toSet()
    override fun get(key: Record.Key): Record? = records[key]
    override fun add(record: Record): Record? = record.takeIf { records.put(record.key, record) != null } ?: get(record.key)
    override fun remove(key: Record.Key): Record? = records.remove(key)
    override fun has(key: Record.Key): Boolean = records.containsKey(key)
}
