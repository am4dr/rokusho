package com.github.am4dr.rokusho.core.metadata

interface BaseTagRepository {

    fun getTagNames(): Set<BaseTagName>
    fun getTags(): Set<BaseTag>
    fun get(name: BaseTagName): BaseTag?
    fun add(tag: BaseTag): BaseTag?
    fun remove(name: BaseTagName): BaseTag?
    fun has(name: BaseTagName): Boolean
}