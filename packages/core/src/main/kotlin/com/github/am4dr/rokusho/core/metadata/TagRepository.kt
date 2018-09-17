package com.github.am4dr.rokusho.core.metadata

import com.github.am4dr.rokusho.core.util.DataObject

interface TagRepository {

    fun getTags(): Set<BaseTag>
    fun getTagNames(): Set<BaseTagName>
    fun get(name: BaseTagName): BaseTag?
    fun updateTagData(name: BaseTagName, data: DataObject): BaseTag?
}