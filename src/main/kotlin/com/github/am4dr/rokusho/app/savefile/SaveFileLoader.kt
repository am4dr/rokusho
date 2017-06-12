package com.github.am4dr.rokusho.app.savefile

import com.github.am4dr.rokusho.app.ImageUrl
import com.github.am4dr.rokusho.core.library.DefaultLibrary
import com.github.am4dr.rokusho.core.library.ItemTag
import com.github.am4dr.rokusho.core.library.Library
import com.github.am4dr.rokusho.core.library.SimpleItemTagDB
import java.nio.file.Path

class SaveFileLoader {
    companion object {
        const val SAVEFILE_NAME = "rokusho.yaml"
    }
    fun load(savefilePath: Path): Library<ImageUrl> {
        val savefile = YamlSaveFileParser().parse(savefilePath)
        val tags = savefile.tags.values.toMutableList()
        val items = savefile.metaData.map { (path, imageMetaData) ->
            val url = ImageUrl(savefilePath.parent.resolve(path).toUri().toURL())
            val itemTags = imageMetaData.tags.map { ItemTag(it.id, it.data["value"]?.toString() ?: it.id) }
            url to itemTags
        }
        return DefaultLibrary(tags, SimpleItemTagDB(items.toMap()))
    }
}