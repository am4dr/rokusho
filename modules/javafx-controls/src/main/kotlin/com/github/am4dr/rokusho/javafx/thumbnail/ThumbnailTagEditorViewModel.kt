package com.github.am4dr.rokusho.javafx.thumbnail

import com.github.am4dr.rokusho.javafx.binding.createBinding
import com.github.am4dr.rokusho.javafx.binding.invoke
import com.github.am4dr.rokusho.javafx.collection.ConcatenatedList
import com.github.am4dr.rokusho.javafx.collection.TransformedList
import com.github.am4dr.rokusho.javafx.control.RemovableTag
import javafx.beans.binding.Bindings
import javafx.beans.property.ObjectProperty
import javafx.beans.property.SimpleListProperty
import javafx.beans.property.SimpleObjectProperty
import javafx.collections.FXCollections
import javafx.collections.ObservableList
import javafx.scene.Node

class ThumbnailTagEditorViewModel<T> {

    val tags: ObservableList<T> = FXCollections.observableArrayList()
    val inputParserProperty: ObjectProperty<((String) -> T?)?> = SimpleObjectProperty()
    val tagNodeFactoryProperty: ObjectProperty<((T) -> Node)?> = SimpleObjectProperty { tag ->
        RemovableTag().apply {
            textProperty.set(tag.toString())
            onRemoved.set { remove(tag) }
        }
    }
    val onEditEndedProperty: ObjectProperty<((List<T>) -> Unit)?> = SimpleObjectProperty()


    private val pendingTags = FXCollections.observableArrayList<T>()
    private val allTags = ConcatenatedList.concat(tags, pendingTags)
    private val tagNodes = SimpleListProperty<Node>().apply {
        bind(createBinding({
            TransformedList(allTags, tagNodeFactoryProperty::invoke)
        }, tagNodeFactoryProperty))
    }

    fun remove(item: T) {
        pendingTags.remove(item) || tags.remove(item)
        endEdit()
    }

    private fun commitInput(input: String) {
        inputParserProperty(input)?.let {
            pendingTags.add(it)
        }
    }

    private fun endEdit() {
        onEditEndedProperty(allTags.toList())
        pendingTags.clear()
    }

    fun bindToView(view: ThumbnailTagEditor) {
        view.onInputCommittedProperty.set(::commitInput)
        view.onEditEndedProperty.set(::endEdit)
        Bindings.bindContent(view.tagNodes, tagNodes)
    }
}
