package com.github.am4dr.rokusho.core

import com.github.am4dr.rokusho.core.item.ItemCollection
import com.github.am4dr.rokusho.core.metadata.MetaDataRepository

class Library(val descriptor: LibraryDescriptor,
              metaDataRepository: MetaDataRepository,
              itemCollection: ItemCollection) : MetaDataRepository by metaDataRepository, ItemCollection by itemCollection {

    override fun equals(other: Any?): Boolean = other is Library && other.descriptor == descriptor
    override fun hashCode(): Int = descriptor.hashCode()
}
