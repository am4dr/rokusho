package com.github.am4dr.rokusho.gui.thumbnail

import com.github.am4dr.rokusho.javafx.collection.TransformedList
import javafx.beans.binding.Bindings
import javafx.beans.property.ListProperty
import javafx.beans.property.ReadOnlyBooleanProperty
import javafx.beans.property.SimpleDoubleProperty
import javafx.beans.property.SimpleListProperty
import javafx.collections.FXCollections
import javafx.collections.ObservableList
import javafx.geometry.Insets
import javafx.geometry.Pos
import javafx.scene.Node
import javafx.scene.control.ScrollPane
import javafx.scene.layout.FlowPane

class ThumbnailFlowPane : ScrollPane() {

    val thumbnails: ListProperty<Thumbnail> = SimpleListProperty(FXCollections.observableArrayList())
    private val vValueHeightProperty = SimpleDoubleProperty()
    private val margin = SimpleDoubleProperty(2000.0)
    private val screenTop = margin.multiply(-1).add(vValueHeightProperty)
    private val screenBottom = margin.add(vValueHeightProperty)
    private val configuredThumbnailNodes: ObservableList<Node> = TransformedList(thumbnails) { th ->
        th.node.apply {
            visibleProperty().bind(
                    managedProperty()
                            .and(layoutYProperty().greaterThanOrEqualTo(screenTop))
                            .and(layoutYProperty().lessThanOrEqualTo(screenBottom)))
        }
    }
    init {
        fitToWidthProperty().set(true)
        hbarPolicy = ScrollBarPolicy.NEVER
        content = FlowPane(10.0, 10.0).apply {
            padding = Insets(25.0, 0.0, 25.0, 0.0)
            alignment = Pos.CENTER
            Bindings.bindContent(children, configuredThumbnailNodes)
            vValueHeightProperty.bind(vvalueProperty().multiply(heightProperty()))
            setOnScroll {
                vvalue -= it.deltaY * 3 / height
                it.consume()
            }
        }
    }

    interface Thumbnail {
        val node: Node

        val loadedProperty: ReadOnlyBooleanProperty
    }
}