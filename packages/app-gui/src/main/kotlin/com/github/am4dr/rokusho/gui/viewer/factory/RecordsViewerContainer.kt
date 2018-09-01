package com.github.am4dr.rokusho.gui.viewer.factory

import com.github.am4dr.rokusho.core.library.Record
import com.github.am4dr.rokusho.javafx.collection.TransformedList
import javafx.beans.InvalidationListener
import javafx.beans.binding.Bindings
import javafx.beans.binding.Bindings.bindContent
import javafx.beans.property.*
import javafx.collections.FXCollections.observableArrayList
import javafx.scene.Node
import javafx.scene.control.Button
import javafx.scene.control.ContentDisplay
import javafx.scene.control.Label
import javafx.scene.control.TextField
import javafx.scene.layout.*


class RecordsViewerContainer<T> : VBox() {

    val filteredCount: LongProperty = SimpleLongProperty(0)
    val totalCount: LongProperty = SimpleLongProperty(0)
    val records: ListProperty<Record<T>> = SimpleListProperty(observableArrayList())

    private val filterInputNode = TextField()
    val filterProperty: ReadOnlyStringProperty = filterInputNode.textProperty()

    private val content = BorderPane().apply { VBox.setVgrow(this, Priority.ALWAYS) }
    private val viewers = observableArrayList<Pair<Node, Button>>()
    private val buttons = TransformedList(viewers) { it.second }
    init {
        val viewerButtons = FlowPane()
        Bindings.bindContent(viewerButtons.children, buttons)
        children.addAll(
                HBox(
                        viewerButtons,
                        Label("フィルター", filterInputNode).apply { contentDisplay = ContentDisplay.RIGHT },
                        Label().apply { textProperty().bind(Bindings.concat("[", filteredCount,  " / ", totalCount, "]")) }
                ),
                content)

        fun selectFirst() {
            if (viewerButtons.children.size == 1) {
                content.center = viewers[0].first
            }
        }
        selectFirst()
        viewerButtons.children.addListener(InvalidationListener {
            selectFirst()
        })
    }

    fun add(viewer: RecordsViewer<*>) {
        bindContent(viewer.records, records)
        viewers.add(Pair(viewer.viewer, Button(viewer.label).apply { setOnAction { content.center = viewer.viewer } }))
    }
}
