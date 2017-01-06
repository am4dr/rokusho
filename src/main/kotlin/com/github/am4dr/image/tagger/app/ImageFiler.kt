package com.github.am4dr.image.tagger.app

import com.github.am4dr.image.tagger.core.Picture
import com.github.am4dr.image.tagger.node.ThumbnailPane
import com.github.am4dr.image.tagger.util.createEmptyListProperty
import javafx.beans.binding.Bindings
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
    val filteredPicturesProperty: ReadOnlyListProperty<Picture>
    private val selectedView = SimpleObjectProperty<Node>().apply { set(thumbnailNode) }
    private val currentView: Node = BorderPane().apply {
        VBox.setVgrow(this, Priority.ALWAYS)
        centerProperty().bind(selectedView)
    }
    init {
        val filterInput = TextField()
        filteredPicturesProperty = createEmptyListProperty()
        filteredPicturesProperty.bind(Bindings.createObjectBinding({
            if (filterInput.text?.let { it == "" } ?: true) { return@createObjectBinding picturesProperty }
            // TODO dependency
            picturesProperty.filtered { it.metaData.tags.find { it.name.contains(filterInput.text) } != null }
        }, arrayOf(picturesProperty, filterInput.textProperty())))
        listNode.itemsProperty().bind(filteredPicturesProperty)
        thumbnailNode.picturesProperty.bind(filteredPicturesProperty)
        children.addAll(
                HBox(
                        Button("リスト").apply { onAction = EventHandler { selectedView.set(listNode) } },
                        Button("サムネイル").apply { onAction = EventHandler { selectedView.set(thumbnailNode) } },
                        Label("フィルター", filterInput).apply { contentDisplay = ContentDisplay.RIGHT}),
                currentView)
    }
}