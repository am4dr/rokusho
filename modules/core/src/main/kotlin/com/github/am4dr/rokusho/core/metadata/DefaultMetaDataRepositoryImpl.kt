package com.github.am4dr.rokusho.core.metadata

import com.github.am4dr.rokusho.core.datastore.DataStore
import com.github.am4dr.rokusho.core.datastore.NullDataStore

class DefaultMetaDataRepositoryImpl(
    baseTags: Set<BaseTag> = setOf(),
    records: Set<Record> = setOf(),
    private val store: DataStore<MetaDataRepository> = NullDataStore()
) : MetaDataRepository {

    private val tags = baseTags.associateByTo(mutableMapOf(), BaseTag::name)
    private val records = records.associateByTo(mutableMapOf(), Record::key)


    override fun getTagNames(): Set<BaseTag.Name> = tags.keys
    override fun getTags(): Set<BaseTag> = tags.values.toSet()
    override fun get(name: BaseTag.Name): BaseTag? = tags[name]
    override fun add(tag: BaseTag): BaseTag? {
        val old = tags.put(tag.name, tag)
        if (tag.data != old?.data) {
            save()
        }
        return tag
    }
    override fun remove(name: BaseTag.Name): BaseTag? {
        return tags.remove(name)?.also { save() }
    }
    override fun has(name: BaseTag.Name): Boolean = tags.containsKey(name)

    override fun getRecordKeys(): Set<Record.Key> = records.keys.toSet()
    override fun getRecords(): Set<Record> = records.values.toSet()
    override fun get(key: Record.Key): Record? = records[key]
    override fun add(record: Record): Record? {
        // TODO test
        val newBaseTags =
            record.tags
                .filter { !tags.containsKey(it.base.name) }
                .map(PatchedTag::base)
        newBaseTags.forEach { add(it) }
        val old = records.put(record.key, record)
        if (record.tags != old?.tags) {
            save()
        }
        return record
    }
    override fun remove(key: Record.Key): Record? {
        return records.remove(key)?.also { save() }
    }
    override fun has(key: Record.Key): Boolean = records.containsKey(key)

    private fun save() {
        store.save(this)
    }
}
