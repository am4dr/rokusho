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
    private val paths: MutableMap<ItemID, Item<out Path>> by lazy {
        Files.list(path)
                .filter { Files.isRegularFile(it) }
                .map { pathToItem(it) }
                .collect(Collectors.toMap(Item<out Path>::id, { it }))
    }
    private fun pathToItem(path: Path): Item<Path> =
            Item(ItemID(collectionRoot.relativize(path).joinToString(idPathSeparator)), path)


    override val ids: Set<ItemID> get() = paths.keys
    override val items: Set<Item<out Path>> get() = paths.values.toSet()

    override fun get(id: ItemID): Item<out Path>? = paths[id]
    override fun add(item: Item<out Path>): Item<out Path>? = item.also { if (paths[it.id]?.get() != item.get()) paths[it.id] = it }
    override fun remove(id: ItemID): Item<out Path>? = paths.remove(id)
    override fun has(id: ItemID): Boolean = paths.containsKey(id)
}