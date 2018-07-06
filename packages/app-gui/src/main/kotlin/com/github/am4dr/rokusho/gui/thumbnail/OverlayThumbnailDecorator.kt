package com.github.am4dr.rokusho.gui.thumbnail

import com.github.am4dr.rokusho.gui.tag.TagNode
import com.github.am4dr.rokusho.gui.tag.TagView
import com.github.am4dr.rokusho.javafx.collection.ConcatenatedList
import com.github.am4dr.rokusho.javafx.collection.TransformedList
import com.github.am4dr.rokusho.old.core.library.ItemTag
import com.github.am4dr.rokusho.old.core.library.Tag
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
import java.util.concurrent.atomic.AtomicBoolean

// TODO extract tag collection editor
class OverlayThumbnailDecorator(private val content: ThumbnailFlowPane.Thumbnail,
                                applyImmediately: Boolean = true) : StackPane(), ThumbnailFlowPane.Thumbnail {

    override val view: OverlayThumbnailDecorator = this
    override val loadedProperty: ReadOnlyBooleanProperty get() = content.loadedProperty

    val tags: ObservableList<ItemTag> = observableArrayList()
    val onEditEndedProperty: ObjectProperty<((List<ItemTag>) -> Unit)> = SimpleObjectProperty({ _ -> })
    val tagParserProperty: ObjectProperty<(String) -> ItemTag> = SimpleObjectProperty { text: String -> ItemTag(Tag(text, Tag.Type.TEXT, mapOf("value" to text)), null) }
    val tagNodeFactoryProperty: ObjectProperty<(ItemTag) -> TagView> = SimpleObjectProperty { tag: ItemTag -> TagNode(tag).view }

    private val pendingTags = observableArrayList<ItemTag>()
    private val allTags = ConcatenatedList.concat(tags, pendingTags)
    private val tagNodes by lazy { TransformedList(allTags) {
        tagNodeFactoryProperty.get().invoke(it).apply {
            onRemoved = {
                pendingTags.remove(it) || tags.remove(it)
                invokeOnEditEnded()
            }
        }
    } }
    private val overlay by lazy { ThumbnailOverlay().apply {
        Bindings.bindContent(tagNodes, this@OverlayThumbnailDecorator.tagNodes)
        visibleProperty().bind(this@OverlayThumbnailDecorator.hoverProperty().or(inputFocusedProperty))
        val contentBounds = content.view.boundsInParentProperty()
        prefWidthProperty().bind(Bindings.select(contentBounds, "width"))
        prefHeightProperty().bind(Bindings.select(contentBounds, "height"))
        setMaxSize(Region.USE_PREF_SIZE, Region.USE_PREF_SIZE)
        onInputCommittedProperty.set { pendingTags.add(tagParserProperty.get().invoke(it)) }
        onEditEndedProperty.bind(createObjectBinding(Callable { { invokeOnEditEnded() } }, this@OverlayThumbnailDecorator.onEditEndedProperty))
    } }
    private val overlayApplied = AtomicBoolean(false)
    private fun applyOverlay() {
        if (overlayApplied.get()) return

        synchronized(overlayApplied) {
            children.add(overlay)
            overlayApplied.set(true)
        }
    }
    init {
        children.addAll(content.view)
        if (applyImmediately) {
            applyOverlay()
        }
        else {
            setOnMouseEntered {
                applyOverlay()
            }
        }
    }

    private fun invokeOnEditEnded() {
        onEditEndedProperty.get().invoke(allTags)
    }
}