package com.github.am4dr.rokusho.app

import com.github.am4dr.rokusho.core.item.Item
import com.github.am4dr.rokusho.core.item.ItemCollection
import com.github.am4dr.rokusho.core.item.ItemID
import java.nio.file.Files
import java.nio.file.Path
import java.util.stream.Collectors

class PathCollection(val collectionRoot: Path, val path: Path = collectionRoot) : ItemCollection<Path> {

    companion object {
        const val idPathSeparator: String = "/"
    }
    private val paths: Map<ItemID, Item<Path>> by lazy {
        Files.list(path)
                .filter { Files.isRegularFile(it) }
                .map { it.toItem() }
                .collect(Collectors.toUnmodifiableMap(Item<Path>::id, { it }))
    }

    override val ids: Set<ItemID> by lazy { paths.keys }
    override val items: Set<Item<Path>> by lazy { paths.values.toSet() }

    override fun get(id: ItemID): Item<Path>? = paths[id]

    private fun Path.toItem(): Item<Path> = Item(ItemID(collectionRoot.relativize(this).joinToString(idPathSeparator)), this)
}