package com.github.am4dr.rokusho.app.savefile.yaml

import com.github.am4dr.rokusho.app.ImageUrl
import com.github.am4dr.rokusho.core.library.DefaultMetaDataRegistry
import com.github.am4dr.rokusho.core.library.ItemTag
import com.github.am4dr.rokusho.core.library.MetaDataRegistry
import com.github.am4dr.rokusho.core.library.SimpleItemTagDB
import java.nio.file.Files
import java.nio.file.Path

class YamlSaveFileLoader {
    companion object {
        const val DEFAULT_SAVEFILE_NAME = "rokusho.yaml"
    }
    // TODO SavefileLoaderはSaveFileを返すべき
    fun load(savefilePath: Path): MetaDataRegistry<ImageUrl> {
        val savefile = YamlSaveFileParser().parse(savefilePath)
        val tags = savefile.tags.values.toMutableList()
        val items = savefile.metaData.map { (path, imageMetaData) ->
            val url = ImageUrl(savefilePath.parent.resolve(path).toUri().toURL())
            val itemTags = imageMetaData.tags.map { ItemTag(it.id, it.data["value"]?.toString() ?: it.id) }
            url to itemTags
        }
        return DefaultMetaDataRegistry(tags, SimpleItemTagDB(items.toMap()))
    }

    fun locateSaveFilePath(directory: Path): Path? =
            directory.resolve(DEFAULT_SAVEFILE_NAME).takeIf { Files.exists(it) }
                    ?: directory.parent?.let { locateSaveFilePath(it) }

    fun getDefaultSavefilePath(directory: Path): Path = directory.normalize().resolve(DEFAULT_SAVEFILE_NAME)
}