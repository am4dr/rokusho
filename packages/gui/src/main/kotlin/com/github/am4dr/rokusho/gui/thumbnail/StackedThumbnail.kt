package com.github.am4dr.rokusho.gui.thumbnail

import javafx.beans.binding.Bindings
import javafx.beans.property.BooleanProperty
import javafx.beans.property.ReadOnlyBooleanProperty
import javafx.beans.property.SimpleBooleanProperty
import javafx.event.EventHandler
import javafx.scene.Node
import javafx.scene.input.MouseEvent
import javafx.scene.layout.Region
import javafx.scene.layout.StackPane

class StackedThumbnail(private val base: ThumbnailFlowPane.Thumbnail,
                       private val overlaySupplier: () -> Node,
                       stackLazily: Boolean = true) : StackPane(), ThumbnailFlowPane.Thumbnail {

    override val view: StackedThumbnail = this
    override val loadedProperty: ReadOnlyBooleanProperty get() = base.loadedProperty

    private val overlayVisibility = SimpleBooleanProperty().apply { base.view.hoverProperty() }
    fun overlayVisibilityProperty(): BooleanProperty = overlayVisibility

    private val overlay by lazy {
        overlaySupplier().apply {
            val contentBounds = base.view.boundsInParentProperty()
            prefWidthProperty().bind(Bindings.select(contentBounds, "width"))
            prefHeightProperty().bind(Bindings.select(contentBounds, "height"))
            setMaxSize(Region.USE_PREF_SIZE, Region.USE_PREF_SIZE)
            visibleProperty().bind(overlayVisibility)
        }
    }
    private var applied = false
    private fun applyOverlay() {
        if (applied) return
        children.add(overlay)
        applied = true
    }

    init {
        children.addAll(base.view)
        if (stackLazily) {
            onMouseEntered = object : EventHandler<MouseEvent> {
                override fun handle(event: MouseEvent?) {
                    if (onMouseEntered == this) {
                        onMouseEntered = null
                    }
                    applyOverlay()
                }
            }
        }
        else {
            applyOverlay()
        }
    }
}
