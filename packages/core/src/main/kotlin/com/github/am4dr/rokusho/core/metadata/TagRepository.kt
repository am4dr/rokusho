package com.github.am4dr.rokusho.core.metadata

interface TagRepository {

    fun getTags(): Set<Tag>
    fun getTagNames(): Set<TagName>
    fun get(name: TagName): Tag?
    fun updateTagDataDefaultValues(name: TagName, data: TagData): Tag?
}