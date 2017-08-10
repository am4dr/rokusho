package com.github.am4dr.rokusho.app.savefile

import com.github.am4dr.rokusho.app.ImageUrl
import com.github.am4dr.rokusho.core.library.*
import javafx.beans.property.ReadOnlyMapProperty
import javafx.collections.FXCollections
import java.nio.file.Path
import java.nio.file.Paths

class SaveFile(val savefilePath: Path, val data: SaveData) {

    companion object {
        fun fromRegistries(savefilePath: Path, tags: ReadOnlyMapProperty<String, Tag>, itemTags: ReadOnlyMapProperty<ImageUrl, List<ItemTag>>): SaveFile {
            val metaData = itemTags.keys.map {
                val path = savefilePath.parent.relativize(Paths.get(it.url.toURI()))
                path to ImageMetaData(itemTags.getOrDefault(it, mutableListOf()))
            }.toMap()
            return SaveFile(savefilePath, SaveData(SaveData.Version.VERSION_1, tags, metaData))
        }
    }

    fun toRegistries(): Pair<TagRegistry, ItemTagRegistry<ImageUrl>> {
        val tags = DefaultTagRegistry(FXCollections.observableSet(data.tags.values.toMutableSet()))
        val items = data.metaData.map { (path, imageMetaData) ->
            val url = ImageUrl(savefilePath.parent.resolve(path).toUri().toURL())
            url to imageMetaData.tags
        }
        val itemTags = DefaultItemTagRegistry(items.toMap())
        return Pair(tags, itemTags)
    }
}
