package com.github.am4dr.image.tagger.app

import com.github.am4dr.image.tagger.core.Picture
import com.github.am4dr.image.tagger.node.ThumbnailPane
import javafx.beans.binding.Bindings
import javafx.beans.property.ReadOnlyListProperty
import javafx.beans.property.SimpleListProperty
import javafx.beans.property.SimpleObjectProperty
import javafx.collections.transformation.FilteredList
import javafx.event.EventHandler
import javafx.scene.Node
import javafx.scene.control.*
import javafx.scene.layout.BorderPane
import javafx.scene.layout.HBox
import javafx.scene.layout.Priority
import javafx.scene.layout.VBox

class ImageFiler(
        val picturesProperty: ReadOnlyListProperty<Picture>,
        val pictureFilter: PictureFilter<String?>,
        val listNode: ListView<Picture>,
        val thumbnailNode: ThumbnailPane) : VBox() {
    private val filteredPicturesProperty: ReadOnlyListProperty<Picture>
    private val selectedView = SimpleObjectProperty<Node>().apply { set(thumbnailNode) }
    private val currentView: Node = BorderPane().apply {
        VBox.setVgrow(this, Priority.ALWAYS)
        centerProperty().bind(selectedView)
    }
    init {
        val filterInput = TextField()
        pictureFilter.inputProperty.bind(filterInput.textProperty())
        filteredPicturesProperty = SimpleListProperty()
        filteredPicturesProperty.bind(Bindings.createObjectBinding({
            FilteredList(picturesProperty, pictureFilter.filterProperty.get())
        }, arrayOf(pictureFilter.filterProperty)))
        listNode.itemsProperty().bind(filteredPicturesProperty)
        thumbnailNode.picturesProperty.bind(picturesProperty)
        thumbnailNode.filterProperty.bind(pictureFilter.filterProperty)
        children.addAll(
                HBox(
                        Button("リスト").apply { onAction = EventHandler { selectedView.set(listNode) } },
                        Button("サムネイル").apply { onAction = EventHandler { selectedView.set(thumbnailNode) } },
                        Label("フィルター", filterInput).apply { contentDisplay = ContentDisplay.RIGHT}),
                currentView)
    }
}