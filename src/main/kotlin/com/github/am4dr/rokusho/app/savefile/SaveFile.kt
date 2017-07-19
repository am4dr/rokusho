package com.github.am4dr.rokusho.app.savefile

import com.github.am4dr.rokusho.app.ImageUrl
import com.github.am4dr.rokusho.core.library.*
import com.github.am4dr.rokusho.javafx.collection.toObservableMap
import javafx.collections.FXCollections
import java.nio.file.Path
import java.nio.file.Paths

class SaveFile(val savefilePath: Path, val data: SaveData) {

    companion object {
        fun fromRegistries(savefilePath: Path, tags: TagRegistry, itemTags: ItemTagDB<ImageUrl>): SaveFile {
            val metaData = itemTags.getKeys().map {
                val path = savefilePath.parent.relativize(Paths.get(it.url.toURI()))
                path to ImageMetaData(itemTags.get(it))
            }.toMap()
            return SaveFile(savefilePath, SaveData(SaveData.Version.VERSION_1, toObservableMap(tags.tags, Tag::id), metaData))
        }
        fun fromMetaDataRegistry(savefilePath: Path, registry: MetaDataRegistry<ImageUrl>): SaveFile {
            val metaData = registry.getAllItems().map {
                val path = savefilePath.parent.relativize(Paths.get(it.key.url.toURI()))
                path to ImageMetaData(it.itemTags)
            }.toMap()
            val data = SaveData(SaveData.Version.VERSION_1, registry.getTags(), metaData)
            return SaveFile(savefilePath, data)
        }
    }

    fun toRegistries(): Pair<TagRegistry, ItemTagDB<ImageUrl>> {
        val tags = DefaultTagRegistry(FXCollections.observableSet(data.tags.values.toMutableSet()))
        val items = data.metaData.map { (path, imageMetaData) ->
            val url = ImageUrl(savefilePath.parent.resolve(path).toUri().toURL())
            url to imageMetaData.tags
        }
        val itemTags = DefaultItemTagDB(items.toMap())
        return Pair(tags, itemTags)
    }
}
