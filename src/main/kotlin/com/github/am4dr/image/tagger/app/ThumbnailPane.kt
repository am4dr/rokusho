package com.github.am4dr.image.tagger.app

import javafx.beans.binding.Bindings
import javafx.beans.binding.When
import javafx.beans.property.*
import javafx.beans.value.ObservableObjectValue
import javafx.collections.FXCollections
import javafx.event.EventHandler
import javafx.geometry.Pos
import javafx.geometry.VPos
import javafx.scene.Node
import javafx.scene.control.ScrollPane
import javafx.scene.image.Image
import javafx.scene.image.ImageView
import javafx.scene.input.MouseEvent
import javafx.scene.layout.*
import javafx.scene.paint.Color
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.util.concurrent.Callable

class ThumbnailPane(children: ListProperty<ImageData>) {
    private val log: Logger = LoggerFactory.getLogger(this.javaClass)
    private val imagesProperty = SimpleListProperty<ImageData>()
    var children: ListProperty<ImageData>
        get() = imagesProperty
        set(value) = imagesProperty.bind(value)
    val pane = ScrollPane().apply {
        fitToWidthProperty().set(true)
        hbarPolicy = ScrollPane.ScrollBarPolicy.NEVER
    }
    private val tiles = SimpleListProperty<ImageTile>()
    private val selectedTileProperty = SimpleObjectProperty<ImageTile>().apply {
        addListener { obs, old, new -> log.debug("change selectedTileProperty: $old -> $new") }
    }
    private val nextRowStartIndexProperty = SimpleIntegerProperty().apply {
        addListener { observableValue, old, new -> log.debug("next row index of selected row: $new") }
    }
    private val expandedTile: ExpandedTile
    private val flowPane = FlowPane(10.0, 10.0).apply {
        alignment = Pos.CENTER
        rowValignment = VPos.BASELINE
    }
    init {
        this.children = children
        pane.content = flowPane
        expandedTile = ExpandedTile(pane, selectedTileProperty)
        tiles.bind(Bindings.createObjectBinding(
                Callable { FXCollections.observableList(imagesProperty.filterNotNull().map(::ImageTile)) },
                imagesProperty))
        tiles.addListener { observable, old, new ->
            log.debug("tiles changed - new.size: ${new.size}")
            selectedTileProperty.set(null)
            val tileClickHandler = EventHandler<MouseEvent> { e ->
                val tile = e.source
                if (tile !is ImageTile) { return@EventHandler }
                log.debug("tile clicked")
                removeExpandedTile()
                if (selectedTileProperty.get() === tile) {
                    selectedTileProperty.set(null)
                    return@EventHandler
                }
                selectedTileProperty.set(tile)
                insertExpandedTile()
            }
            new.map { tile -> tile.onMouseClicked = tileClickHandler }
            val contents = flowPane.children
            contents.setAll(new)
            nextRowStartIndexProperty.apply {
                val calc = Callable {
                    val tile = selectedTileProperty.get()
                    if (tile != null) calcNextRowStartIndex(contents, contents.indexOf(tile)) else 0
                }
                unbind()
                bind(Bindings.createIntegerBinding(calc, selectedTileProperty, contents.last().layoutXProperty()))
                addListener { observableValue, old, new ->
                    insertExpandedTile()
                }
            }
        }
    }
    private fun removeExpandedTile() {
        log.debug("remove ExpandTile")
        selectedTileProperty.get() ?: return
        flowPane.children.removeAll(expandedTile)
    }
    private fun insertExpandedTile(index: Int = -1) {
        log.debug("insert ExpandTile at $index")
        selectedTileProperty.get() ?: return
        flowPane.children.removeAll(expandedTile)
        if (index < 0) { flowPane.children.add(nextRowStartIndexProperty.get(), expandedTile) }
        else if (index != 0) { flowPane.children.add(index, expandedTile) }
        else { flowPane.children.add(expandedTile) }
    }
    private fun calcNextRowStartIndex(nodes: List<Node>, index: Int): Int {
        val selectedBaseline = nodes[index].boundsInParent.maxY
        return (index+1..nodes.size-1)
                .firstOrNull { nodes[it].boundsInParent.maxY > selectedBaseline + 10 } ?: 0
    }
}
private class ExpandedTile(parent: Region, targetTile: ObservableObjectValue<ImageTile>) : VBox() {
    private val imageView = ImageView().apply {
        isPreserveRatio = true
        fitWidthProperty().bind(
                When(parent.widthProperty().multiply(2.0).lessThan(400.0))
                        .then(0.9).otherwise(0.75)
                        .multiply(parent.widthProperty()))
        fitHeightProperty().bind(
                When(parent.heightProperty().multiply(2.0).lessThan(400.0))
                        .then(0.9).otherwise(0.75)
                        .multiply(parent.heightProperty()))
    }
    val image: Property<Image>
        get() = imageView.imageProperty()
    init {
        fillWidthProperty().set(true)
        prefWidthProperty().bind(parent.widthProperty().subtract(40))
        maxWidthProperty().bind(parent.widthProperty().subtract(40))
        alignment = Pos.CENTER
        background = Background(BackgroundFill(Color.rgb(30, 30, 30), null, null))
        image.bind(Bindings.createObjectBinding(Callable { targetTile.get()?.data?.image }, targetTile))
        children.add(imageView)
    }
}