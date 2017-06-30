package com.github.am4dr.rokusho.gui

import com.github.am4dr.rokusho.javafx.collection.TransformedList
import javafx.beans.binding.Bindings
import javafx.beans.binding.BooleanBinding
import javafx.beans.property.ListProperty
import javafx.beans.property.ReadOnlyObjectWrapper
import javafx.beans.property.SimpleDoubleProperty
import javafx.beans.property.SimpleListProperty
import javafx.beans.value.ObservableObjectValue
import javafx.collections.FXCollections.observableArrayList
import javafx.collections.ObservableList
import javafx.geometry.Insets
import javafx.geometry.Pos
import javafx.scene.control.ScrollPane
import javafx.scene.layout.FlowPane

class ThumbnailLayout(
        initialThumbnails: List<Thumbnail> = listOf(),
        filter: ObservableObjectValue<(Thumbnail) -> Boolean> = ReadOnlyObjectWrapper({ _ -> true })) : ScrollPane() {

    private val vValueHeightProperty = SimpleDoubleProperty()
    private val margin = SimpleDoubleProperty(2000.0)
    private val screenTop = margin.multiply(-1).add(vValueHeightProperty)
    private val screenBottom = margin.add(vValueHeightProperty)
    val thumbnails: ListProperty<Thumbnail> = SimpleListProperty(observableArrayList(initialThumbnails))
    private val configuredThumbnails: ObservableList<Thumbnail> = TransformedList(thumbnails) { th ->
        th.apply {
            layoutY = -500.0
            val filterPassedProperty = object : BooleanBinding() {
                init { super.bind(filter) }
                override fun computeValue(): Boolean = filter.value.invoke(this@apply)
            }
            managedProperty().bind(imageLoadedProperty.and(filterPassedProperty))
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
            Bindings.bindContent(children, this@ThumbnailLayout.configuredThumbnails)
            vValueHeightProperty.bind(vvalueProperty().multiply(heightProperty()))
            setOnScroll {
                vvalue -= it.deltaY * 3 / height
                it.consume()
            }
        }
    }
}