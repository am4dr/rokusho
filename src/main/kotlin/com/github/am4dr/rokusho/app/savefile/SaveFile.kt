package com.github.am4dr.rokusho.app.savefile

import com.github.am4dr.rokusho.app.ImageUrl
import com.github.am4dr.rokusho.core.library.DefaultMetaDataRegistry
import com.github.am4dr.rokusho.core.library.MetaDataRegistry
import com.github.am4dr.rokusho.core.library.SimpleItemTagDB
import java.nio.file.Path
import java.nio.file.Paths

class SaveFile(val savefilePath: Path, val data: SaveData) {

    companion object {
        fun fromMetaDataRegistry(savefilePath: Path, registry: MetaDataRegistry<ImageUrl>): SaveFile {
            val metaData = registry.getAllItems().map {
                val path = savefilePath.parent.relativize(Paths.get(it.key.url.toURI()))
                path to ImageMetaData(it.itemTags)
            }.toMap()
            val data = SaveData(SaveData.Version.VERSION_1, registry.getTags(), metaData)
            return SaveFile(savefilePath, data)
        }
    }

    fun toMetaDataRegistry(): MetaDataRegistry<ImageUrl> {
        val tags = data.tags.values.toMutableList()
        val items = data.metaData.map { (path, imageMetaData) ->
            val url = ImageUrl(savefilePath.parent.resolve(path).toUri().toURL())
            url to imageMetaData.tags
        }
        return DefaultMetaDataRegistry(tags, SimpleItemTagDB(items.toMap()))
    }
}
