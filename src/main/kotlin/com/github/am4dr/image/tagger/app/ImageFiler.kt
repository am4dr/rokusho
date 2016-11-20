package com.github.am4dr.image.tagger.app

import javafx.beans.binding.Bindings
import javafx.beans.binding.When
import javafx.beans.property.ListProperty
import javafx.beans.property.SimpleIntegerProperty
import javafx.beans.property.SimpleListProperty
import javafx.beans.property.SimpleObjectProperty
import javafx.collections.FXCollections
import javafx.collections.ObservableList
import javafx.event.EventHandler
import javafx.geometry.Pos
import javafx.geometry.VPos
import javafx.scene.Node
import javafx.scene.control.Button
import javafx.scene.control.Label
import javafx.scene.control.ListView
import javafx.scene.control.ScrollPane
import javafx.scene.input.MouseEvent
import javafx.scene.layout.*
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.util.concurrent.Callable

// TODO サムネイルビューに拡大表示を実装する
//          拡大表示PaneをflowPane.childrenにおける対象の次に挿入する
// TODO ダブルクリックに限って拡大表示をする
// TODO thumbnailNodeを別クラスに分割する
class ImageFiler(val mainFrame: MainFrame) {
    private val log: Logger = LoggerFactory.getLogger(this.javaClass)
    private val imagesProperty: ListProperty<ImageData> = SimpleListProperty<ImageData>().apply { bind(mainFrame.imagesProperty) }
    private val listNode = ListView<ImageData>().apply {
        itemsProperty().bind(imagesProperty)
    }
    private val tiles = SimpleListProperty<ImageTile>().apply {
        val callable = Callable<ObservableList<ImageTile>> {
            FXCollections.observableList(imagesProperty.get().filterNotNull().map(::ImageTile))
        }
        bind(Bindings.createObjectBinding(callable, imagesProperty))
    }
    private val labelTmp = Label()
    private val thumbnailNode = ScrollPane().apply {
        fitToWidthProperty().set(true)
        val flowPane = FlowPane(10.0, 10.0).apply {
            alignment = Pos.CENTER
            rowValignment = VPos.BASELINE
        }
        content = flowPane
        val selectedTileProperty = SimpleObjectProperty<ImageTile>()
        selectedTileProperty.addListener { obs, old, new -> log.debug("change selectedTileProperty: $old -> $new") }
        tiles.addListener { observable, old, new ->
            log.debug("tiles changed - new.size: ${new.size}")
            selectedTileProperty.set(null)
            val tileClickHandler = EventHandler<MouseEvent> { e ->
                val tile = e.source
                if (tile !is ImageTile) { return@EventHandler }
                log.debug("tile clicked")
                if (selectedTileProperty.get() === tile) {
                    selectedTileProperty.set(null)
                    return@EventHandler
                }
                selectedTileProperty.set(tile)
            }
            new.map { tile -> tile.onMouseClicked = tileClickHandler }
            val children = flowPane.children
            children.setAll(new)
            val nextRowStartIndexProperty = SimpleIntegerProperty().apply {
                addListener { observableValue, old, new -> log.debug("next row index of selected row: $new") }
                labelTmp.textProperty().bind(this.asString())
                val calc = Callable {
                    val tile = selectedTileProperty.get();
                    if (tile != null) calcNextRowStartIndex(children, children.indexOf(tile)) else 0
                }
                bind(Bindings.createIntegerBinding(calc, selectedTileProperty, children.last().layoutXProperty()))
            }
        }
    }
    private fun calcNextRowStartIndex(nodes: List<Node>, index: Int): Int {
        val selectedBaseline = nodes[index].boundsInParent.maxY
        return (index+1..nodes.size-1)
                .firstOrNull { nodes[it].boundsInParent.maxY > selectedBaseline + 10 } ?: 0
    }
    private val selectedView = SimpleObjectProperty<Node>().apply { set(thumbnailNode) }
    private val currentView: Node = BorderPane().apply {
        VBox.setVgrow(this, Priority.ALWAYS)
        centerProperty().bind(
                When(imagesProperty.sizeProperty().isEqualTo(0))
                        .then<Node>(mainFrame.makeDirectorySelectorPane())
                        .otherwise(selectedView))
    }
    val node: Node = VBox(
            HBox(Label("画像ファイラー 仮置きテキスト"), labelTmp),
            HBox(
                Button("リスト").apply { onAction = EventHandler { selectedView.set(listNode) } },
                Button("サムネイル").apply { onAction = EventHandler { selectedView.set(thumbnailNode) } }),
            currentView)
}