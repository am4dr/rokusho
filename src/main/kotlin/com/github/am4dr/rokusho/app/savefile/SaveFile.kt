package com.github.am4dr.rokusho.app.savefile

import com.github.am4dr.rokusho.app.ImageUrl
import com.github.am4dr.rokusho.core.library.*
import java.nio.file.Path
import java.nio.file.Paths

class SaveFile(val savefilePath: Path, val data: SaveData) {

    companion object {
        fun fromMetaDataRegistry(savefilePath: Path, registry: MetaDataRegistry<ImageUrl>): SaveFile {
            val registeredTags = registry.getTags()
            val dummyTag = SimpleTag("dummy", TagType.TEXT)
            val metaData = registry.getAllItems().map {
                val path = savefilePath.parent.relativize(Paths.get(it.key.url.toURI()))
                val tags = it.itemTags.map {
                    val tag = registeredTags[it.name] ?: dummyTag
                    val tagData = tag.data.toMutableMap()
                    tagData["value"] = it.value
                    SimpleTag(it.name, tag.type, tagData)
                }
                path to ImageMetaData(tags)
            }.toMap()
            val data = SaveData(SaveData.Version.VERSION_1, registry.getTags(), metaData)
            return SaveFile(savefilePath, data)
        }
    }

    fun toMetaDataRegistry(): MetaDataRegistry<ImageUrl> {
        val tags = data.tags.values.toMutableList()
        val items = data.metaData.map { (path, imageMetaData) ->
            val url = ImageUrl(savefilePath.parent.resolve(path).toUri().toURL())
            val itemTags = imageMetaData.tags.map { ItemTag(it.id, it.data["value"]?.toString() ?: it.id) }
            url to itemTags
        }
        return DefaultMetaDataRegistry(tags, SimpleItemTagDB(items.toMap()))
    }
}
