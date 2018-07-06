package com.github.am4dr.rokusho.core.metadata

interface TagRepository {

    fun getTags(): Set<Tag>
    fun updateTagDataDefaultValues(name: TagName, data: TagData): Tag?
}