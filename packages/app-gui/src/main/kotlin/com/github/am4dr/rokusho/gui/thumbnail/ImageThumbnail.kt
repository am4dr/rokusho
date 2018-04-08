package com.github.am4dr.rokusho.gui.thumbnail

import com.github.am4dr.rokusho.core.library.ItemTag
import com.github.am4dr.rokusho.gui.tag.TagView
import com.github.am4dr.rokusho.javafx.collection.TransformedList
import javafx.beans.binding.Bindings
import javafx.beans.property.ReadOnlyBooleanProperty
import javafx.beans.property.ReadOnlyBooleanWrapper
import javafx.beans.property.ReadOnlyListProperty
import javafx.beans.property.SimpleListProperty
import javafx.collections.FXCollections
import javafx.collections.ObservableList
import javafx.scene.Node
import javafx.scene.image.Image
import javafx.scene.image.ImageView
import javafx.scene.layout.Pane

class ImageThumbnail(private val image: Image,
                     private val tagParser: (String) -> ItemTag,
                     private val tagNodeFactory: (ItemTag) -> TagView): ThumbnailFlowPane.Thumbnail {

    override val view: ThumbnailView
    override val loadedProperty: ReadOnlyBooleanProperty = ReadOnlyBooleanWrapper(false).apply {
        bind(image.widthProperty().isNotEqualTo(0).and(image.heightProperty().isNotEqualTo(0)))
    }.readOnlyProperty

    private val _tags: ObservableList<ItemTag> = FXCollections.observableArrayList()
    val tags: ReadOnlyListProperty<ItemTag> = SimpleListProperty(FXCollections.observableArrayList())
    private fun syncTags() { tags.setAll(_tags) }
    fun setTags(tags: Iterable<ItemTag>) {
        _tags.addAll(tags)
        syncTags()
    }

    private val tagNodes: ObservableList<Node> = TransformedList(_tags) { tag ->
        tagNodeFactory(tag).apply {
            onRemoved = {
                this@ImageThumbnail._tags.remove(tag)
                syncTags()
            }
        }
    }
    init {
        view = ThumbnailView(Pane(ImageView(image))).apply {
            onInputCommitted = { text: String -> _tags.add(tagParser(text)) }
            onEditEnded = this@ImageThumbnail::syncTags

            Bindings.bindContent(tagNodes, this@ImageThumbnail.tagNodes)
        }
    }
}
