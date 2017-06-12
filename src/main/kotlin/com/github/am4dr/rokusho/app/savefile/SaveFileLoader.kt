package com.github.am4dr.rokusho.app.savefile

import com.github.am4dr.rokusho.app.ImageUrl
import com.github.am4dr.rokusho.core.YamlSaveFileParser
import com.github.am4dr.rokusho.core.library.DefaultLibrary
import com.github.am4dr.rokusho.core.library.ItemTag
import com.github.am4dr.rokusho.core.library.Library
import com.github.am4dr.rokusho.core.library.SimpleItemTagDB
import java.nio.file.Path

class SaveFileLoader {
    fun load(savefile: Path): Library<ImageUrl> {
        val parsed = YamlSaveFileParser().parse(savefile)
        val tags = parsed.getTags().toMutableList()
        val items = parsed.getItemMetaData().map {
            val url = ImageUrl(parsed.savefilePath.parent.resolve(it.id).toUri().toURL())
            val itemTags = it.tags.map { ItemTag(it.id, it.data["value"]?.toString() ?: it.id) }
            url to itemTags
        }
        return DefaultLibrary(tags, SimpleItemTagDB(items.toMap()))
    }
}