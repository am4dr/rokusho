package com.github.am4dr.rokusho.gui

import com.github.am4dr.image.tagger.app.TagNodeFactory
import com.github.am4dr.image.tagger.core.ImageMetaData
import com.github.am4dr.image.tagger.core.Picture
import com.github.am4dr.image.tagger.core.TagInfo
import com.github.am4dr.image.tagger.core.URLImageLoader
import com.github.am4dr.image.tagger.util.createEmptyListProperty
import com.github.am4dr.rokusho.core.ImagePathLibrary
import com.github.am4dr.rokusho.core.TagAdaptor
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
    private val _tags = SimpleMapProperty(observableMap(mutableMapOf<String, TagInfo>()))
    override val picturesProperty: ReadOnlyListProperty<Picture> get() = _pictures
    override val tagsProperty: ReadOnlyMapProperty<String, TagInfo> get() = _tags
    override val tagNodeFactory: TagNodeFactory = TagNodeFactory(_tags)
    override fun setLibrary(path: Path) {
        val lib = ImagePathLibrary(path)
        lib.getTags().map {
            Pair(it.id, TagInfo(it.type, it.data))
        }.toMap(_tags)
        val pictures = lib.images.map { img ->
            Picture(URLImageLoader(img.url), img.tags.map(::TagAdaptor).let(::ImageMetaData))
        }
        _pictures.setAll(pictures)
    }
    override fun updateMetaData(picture: Picture, metaData: ImageMetaData) {
        throw UnsupportedOperationException("not implemented") //To change body of created functions use File | Settings | File Templates.
    }
    override fun updateTagInfo(name: String, info: TagInfo) {
        throw UnsupportedOperationException("not implemented") //To change body of created functions use File | Settings | File Templates.
    }
    override fun save() {
        throw UnsupportedOperationException("not implemented") //To change body of created functions use File | Settings | File Templates.
    }
}
