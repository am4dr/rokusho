package com.github.am4dr.rokusho.javafx.thumbnail

import com.github.am4dr.rokusho.javafx.collection.ConcatenatedList
import com.github.am4dr.rokusho.javafx.collection.TransformedList
import com.github.am4dr.rokusho.javafx.control.RemovableTag
import javafx.beans.binding.Bindings
import javafx.beans.property.ObjectProperty
import javafx.beans.property.ReadOnlyBooleanProperty
import javafx.beans.property.SimpleObjectProperty
import javafx.collections.FXCollections
import javafx.collections.ObservableList
import javafx.scene.Node
import javafx.scene.layout.StackPane
import java.util.concurrent.Callable

// TODO ThumbnailOverlayとの役割分担が不明瞭なのを改善する
class ThumbnailTagEditor<T> : StackPane() {

    val tags: ObservableList<T> = FXCollections.observableArrayList()
    val onEditEndedProperty: ObjectProperty<((List<T>) -> Unit)?> = SimpleObjectProperty({ _ -> })
    val inputParserProperty: ObjectProperty<((String) -> T?)?> = SimpleObjectProperty { _ -> null }
    val tagNodeFactoryProperty: ObjectProperty<((T) -> Node)?> = SimpleObjectProperty { tag ->
        RemovableTag().apply {
            textProperty().set(tag.toString())
            onRemoved.set { remove(tag) }
        }
    }

    private val pendingTags = FXCollections.observableArrayList<T>()
    private val allTags = ConcatenatedList.concat(tags, pendingTags)
    private val tagNodes = Bindings.createObjectBinding(Callable {
        TransformedList(allTags) { tagNodeFactoryProperty.get()?.invoke(it) }
    }, tagNodeFactoryProperty)

    private val overlay = ThumbnailOverlay().apply {
        Bindings.bindContent(tagNodes, this@ThumbnailTagEditor.tagNodes.value)
        this@ThumbnailTagEditor.tagNodes.addListener { _ ->
            Bindings.bindContent(tagNodes, this@ThumbnailTagEditor.tagNodes.value)
        }
        onInputCommittedProperty.set { inputParserProperty.get()?.invoke(it)?.let { item -> pendingTags.add(item) } }
        onEditEndedProperty.bind(Bindings.createObjectBinding(Callable { { invokeOnEditEnded() } },
                this@ThumbnailTagEditor.onEditEndedProperty))
    }
    fun inputFocusedProperty(): ReadOnlyBooleanProperty = overlay.inputFocusedProperty

    init {
        children.add(overlay)
    }

    fun remove(item: T) {
        pendingTags.remove(item) || tags.remove(item)
        invokeOnEditEnded()
    }

    private fun invokeOnEditEnded() {
        this@ThumbnailTagEditor.onEditEndedProperty.get()?.invoke(allTags)
    }

}