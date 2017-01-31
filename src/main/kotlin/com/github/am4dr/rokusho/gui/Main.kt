package com.github.am4dr.rokusho.gui

import com.github.am4dr.image.tagger.app.TagNodeFactory
import com.github.am4dr.image.tagger.core.ImageMetaData
import com.github.am4dr.image.tagger.core.Picture
import com.github.am4dr.image.tagger.core.URLImageLoader
import com.github.am4dr.image.tagger.util.createEmptyListProperty
import com.github.am4dr.rokusho.core.*
import javafx.application.Application
import javafx.beans.property.ReadOnlyListProperty
import javafx.beans.property.ReadOnlyMapProperty
import javafx.beans.property.SimpleMapProperty
import javafx.collections.FXCollections.observableMap
import java.nio.file.Path
import com.github.am4dr.image.tagger.app.Main as OldMain
import com.github.am4dr.image.tagger.app.MainModel as OldMainModel

fun main(args: Array<String>) = Application.launch(OldMain::class.java, *args)

interface MainModel {
    val libraries: ReadOnlyListProperty<ImagePathLibrary>
    fun addLibrary(path: Path)
    fun saveLibraries()
}
class DefaultMainModel : MainModel {
    private val _libraries = createEmptyListProperty<ImagePathLibrary>()
    override val libraries: ReadOnlyListProperty<ImagePathLibrary> get() = _libraries
    override fun addLibrary(path: Path) {
        _libraries.add(ImagePathLibrary(path))
    }
    override fun saveLibraries() {
        TODO() // TODO
    }
}

class AdaptedDefaultMainModel : OldMainModel {
    private val _pictures = createEmptyListProperty<Picture>()
    private val _tags = SimpleMapProperty(observableMap(mutableMapOf<String, Tag>()))
    override val picturesProperty: ReadOnlyListProperty<Picture> get() = _pictures
    override val tagsProperty: ReadOnlyMapProperty<String, Tag> get() = _tags
    override val tagNodeFactory: TagNodeFactory = TagNodeFactory(_tags)

    private val picToItemMap = mutableMapOf<Picture, ImageItem>()
    private val picToLibMap = mutableMapOf<Picture, ImagePathLibrary>()
    override fun setLibrary(path: Path) {
        val lib = ImagePathLibrary(path)
        lib.getTags().map {
            Pair(it.id, SimpleTag(it.id, it.type, it.data))
        }.toMap(_tags)
        val pictures = lib.images.map { img ->
             img.toPicture() to img
        }.toMap(picToItemMap)
        pictures.keys.forEach { picToLibMap[it] = lib }
        _pictures.setAll(pictures.keys)
    }
    override fun updateMetaData(picture: Picture, metaData: ImageMetaData) {
        val item = picToItemMap[picture] ?: throw IllegalStateException()
        val lib = picToLibMap[picture] ?: throw IllegalStateException()
        val newItem = SimpleImage(item.id, item.url, metaData.tags)
        val newPic = picture.copy(metaData = newItem.tags.let(::ImageMetaData))
        lib.updateItemMetaData(SimpleLibraryItemMetaData(newItem.id, newItem.tags))
        picToItemMap[newPic] = newItem
        picToLibMap[newPic] = lib
        _pictures.run {
            set(indexOf(picture), newPic)
        }
    }
    private fun ImageItem.toPicture(): Picture =
            Picture(URLImageLoader(url), tags.let(::ImageMetaData))
    override fun updateTagInfo(name: String, info: Tag) {
        throw UnsupportedOperationException("not implemented")
    }
    override fun save() {
        throw UnsupportedOperationException("not implemented")
    }
}
