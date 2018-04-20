package com.github.am4dr.rokusho.gui.thumbnail

import com.github.am4dr.rokusho.core.library.ItemTag
import com.github.am4dr.rokusho.gui.tag.TagView
import com.github.am4dr.rokusho.javafx.collection.ConcatenatedList
import com.github.am4dr.rokusho.javafx.collection.TransformedList
import javafx.beans.binding.Bindings
import javafx.beans.binding.Bindings.createObjectBinding
import javafx.beans.property.ObjectProperty
import javafx.beans.property.ReadOnlyBooleanProperty
import javafx.beans.property.SimpleObjectProperty
import javafx.collections.FXCollections.observableArrayList
import javafx.collections.ObservableList
import javafx.scene.layout.Region
import javafx.scene.layout.StackPane
import java.util.concurrent.Callable

// TODO extract tag collection editor
class OverlayThumbnailDecorator(private val content: ThumbnailFlowPane.Thumbnail,
                                tagParser: (String) -> ItemTag,
                                tagNodeFactory: (ItemTag) -> TagView) : StackPane(), ThumbnailFlowPane.Thumbnail {

    override val view: OverlayThumbnailDecorator = this
    override val loadedProperty: ReadOnlyBooleanProperty get() = content.loadedProperty

    val tags: ObservableList<ItemTag> = observableArrayList()
    val onEditEndedProperty: ObjectProperty<((List<ItemTag>) -> Unit)> = SimpleObjectProperty({ _ -> })

    private val pendingTags = observableArrayList<ItemTag>()
    private val allTags = ConcatenatedList.concat(tags, pendingTags)
    private val tagNodes = TransformedList(allTags) {
        tagNodeFactory(it).apply {
            onRemoved = {
                pendingTags.remove(it) || tags.remove(it)
                invokeOnEditEnded()
            }
        }
    }
    private val overlay = ThumbnailOverlay().apply {
        Bindings.bindContent(tagNodes, this@OverlayThumbnailDecorator.tagNodes)
        visibleProperty().bind(this@OverlayThumbnailDecorator.hoverProperty().or(inputFocusedProperty))
        val contentBounds = content.view.boundsInParentProperty()
        prefWidthProperty().bind(Bindings.select(contentBounds, "width"))
        prefHeightProperty().bind(Bindings.select(contentBounds, "height"))
        setMaxSize(Region.USE_PREF_SIZE, Region.USE_PREF_SIZE)
        onInputCommittedProperty.set { pendingTags.add(tagParser(it)) }
        onEditEndedProperty.bind(createObjectBinding(Callable { { invokeOnEditEnded() } }, this@OverlayThumbnailDecorator.onEditEndedProperty))
    }
    init {
        children.addAll(content.view, overlay)
    }

    private fun invokeOnEditEnded() {
        onEditEndedProperty.get().invoke(allTags)
    }
}