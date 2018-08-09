package com.github.am4dr.rokusho.gui.old.thumbnail

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
    private val flowPane = FlowPane(10.0, 10.0)
    private val hiddenHeight = flowPane.heightProperty().subtract(heightProperty())
    private val screenTop = hiddenHeight.multiply(vvalueProperty())
    private val screenBottom = heightProperty().add(screenTop)
    private val rowHeight = SimpleDoubleProperty(200.0)
    private val viewTop = rowHeight.multiply(-1).add(screenTop)
    private val viewBottom = rowHeight.add(screenBottom)
    private val configuredThumbnailNodes: ObservableList<Node> = TransformedList(thumbnails) { th ->
        th.view.apply {
            layoutY = -500.0
            managedProperty().bind(th.loadedProperty)
            visibleProperty().bind(
                    managedProperty()
                            .and(layoutYProperty().greaterThanOrEqualTo(viewTop))
                            .and(layoutYProperty().lessThanOrEqualTo(viewBottom)))
        }
    }
    init {
        fitToWidthProperty().set(true)
        hbarPolicy = ScrollBarPolicy.NEVER
        content = flowPane.apply {
            padding = Insets(25.0, 0.0, 25.0, 0.0)
            alignment = Pos.CENTER
            Bindings.bindContent(children, configuredThumbnailNodes)
            setOnScroll {
                vvalue -= it.deltaY * 3 / height
                it.consume()
            }
        }
    }

    interface Thumbnail {
        val view: Node

        val loadedProperty: ReadOnlyBooleanProperty
    }
}