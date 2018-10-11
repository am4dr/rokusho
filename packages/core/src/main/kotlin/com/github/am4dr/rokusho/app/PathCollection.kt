package com.github.am4dr.rokusho.app

import com.github.am4dr.rokusho.core.item.Item
import com.github.am4dr.rokusho.core.item.ItemCollection
import java.nio.file.Files
import java.nio.file.Path
import java.util.stream.Collectors

class PathCollection(val collectionRoot: Path, val path: Path = collectionRoot) : ItemCollection<Path> {

    companion object {
        const val idPathSeparator: String = "/"
    }
    private val paths: MutableMap<Item.ID, Item<out Path>> by lazy {
        Files.list(path)
                .filter { Files.isRegularFile(it) }
                .map { pathToItem(it) }
                .collect(Collectors.toMap(Item<out Path>::id, { it }))
    }
    private fun pathToItem(path: Path): Item<Path> =
            Item(Item.ID(collectionRoot.relativize(path).joinToString(idPathSeparator)), path)


    override val ids: Set<Item.ID> get() = paths.keys
    override val items: Set<Item<out Path>> get() = paths.values.toSet()

    override fun get(id: Item.ID): Item<out Path>? = paths[id]
    override fun has(id: Item.ID): Boolean = paths.containsKey(id)
}