package com.github.am4dr.rokusho.core.metadata

interface MetaDataRepository {

    fun getTagNames(): Set<BaseTag.Name>
    fun getTags(): Set<BaseTag>
    fun get(name: BaseTag.Name): BaseTag?
    fun add(tag: BaseTag): BaseTag?
    fun remove(name: BaseTag.Name): BaseTag?
    fun has(name: BaseTag.Name): Boolean

    fun getRecordKeys(): Set<Record.Key>
    fun getRecords(): Set<Record>
    fun get(key: Record.Key): Record?
    fun add(record: Record): Record?
    fun remove(key: Record.Key): Record?
    fun has(key: Record.Key): Boolean
}