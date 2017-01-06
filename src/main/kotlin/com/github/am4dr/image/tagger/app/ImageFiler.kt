package com.github.am4dr.image.tagger.app

import com.github.am4dr.image.tagger.core.Picture
import com.github.am4dr.image.tagger.node.ThumbnailPane
import com.github.am4dr.image.tagger.util.createEmptyListProperty
import javafx.beans.binding.Bindings
import javafx.beans.binding.ObjectBinding
import javafx.beans.property.ObjectProperty
import javafx.beans.property.ReadOnlyListProperty
import javafx.beans.property.SimpleObjectProperty
import javafx.event.EventHandler
import javafx.scene.Node
import javafx.scene.control.*
import javafx.scene.layout.BorderPane
import javafx.scene.layout.HBox
import javafx.scene.layout.Priority
import javafx.scene.layout.VBox

class ImageFiler(
        val picturesProperty: ReadOnlyListProperty<Picture>,
        val listNode: ListView<Picture>,
        val thumbnailNode: ThumbnailPane) : VBox() {
    val filterProperty: ObjectProperty<(Picture) -> Boolean>
    val filteredPicturesProperty: ReadOnlyListProperty<Picture>
    private val selectedView = SimpleObjectProperty<Node>().apply { set(thumbnailNode) }
    private val currentView: Node = BorderPane().apply {
        VBox.setVgrow(this, Priority.ALWAYS)
        centerProperty().bind(selectedView)
    }
    init {
        val filterInput = TextField()
        filterProperty = SimpleObjectProperty({ it -> true })
        filterProperty.bind(object : ObjectBinding<(Picture) -> Boolean>() {
            init { super.bind(filterInput.textProperty()) }
            override fun computeValue(): (Picture) -> Boolean = { pic ->
                if (filterInput.text.let { it == null || it == "" }) { true }
                else { pic.metaData.tags.find { it.name.contains(filterInput.text) } != null }
            }
        })
        filteredPicturesProperty = createEmptyListProperty()
        filteredPicturesProperty.bind(Bindings.createObjectBinding({
            // TODO dependency
            picturesProperty.filtered(filterProperty.get())
        }, arrayOf(picturesProperty, filterProperty)))
        listNode.itemsProperty().bind(filteredPicturesProperty)
        thumbnailNode.picturesProperty.bind(picturesProperty)
        thumbnailNode.filterProperty.bind(filterProperty)
        children.addAll(
                HBox(
                        Button("リスト").apply { onAction = EventHandler { selectedView.set(listNode) } },
                        Button("サムネイル").apply { onAction = EventHandler { selectedView.set(thumbnailNode) } },
                        Label("フィルター", filterInput).apply { contentDisplay = ContentDisplay.RIGHT}),
                currentView)
    }
}