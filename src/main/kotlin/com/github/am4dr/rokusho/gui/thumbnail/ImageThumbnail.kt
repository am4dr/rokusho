package com.github.am4dr.rokusho.gui.thumbnail

import com.github.am4dr.rokusho.core.library.ItemTag
import com.github.am4dr.rokusho.gui.TagNode
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

class ImageThumbnail(val image: Image,
                     private val tagParser: (String) -> ItemTag,
                     private val tagNodeFactory: (ItemTag) -> TagNode): ThumbnailFlowPane.Thumbnail {

    override val node: ThumbnailView
    override val loadedProperty: ReadOnlyBooleanProperty = ReadOnlyBooleanWrapper(false).apply {
        bind(image.widthProperty().isNotEqualTo(0).and(image.heightProperty().isNotEqualTo(0)))
    }.readOnlyProperty

    private val _tags: ObservableList<ItemTag> = FXCollections.observableArrayList()
    val tags: ReadOnlyListProperty<ItemTag> = SimpleListProperty(FXCollections.observableArrayList())
    private fun syncTags() = tags.setAll(_tags)
    fun setTags(tags: Iterable<ItemTag>) {
        _tags.addAll(tags)
        syncTags()
    }

    private val tagNodes: ObservableList<Node> = TransformedList(_tags) { tag ->
        tagNodeFactory(tag).apply { onRemovedProperty.set({
            this@ImageThumbnail._tags.remove(tag)
            syncTags()
        }) }
    }
    init {
        val onInputCommitted: (String) -> Unit = { text: String ->
            _tags.add(tagParser(text))
        }
        val onEditEnded: () -> Unit = {
            syncTags()
        }
        node = ThumbnailView(Pane(ImageView(image)), onInputCommitted, onEditEnded).apply {
            Bindings.bindContent(tagNodes, this@ImageThumbnail.tagNodes)
        }
    }
}