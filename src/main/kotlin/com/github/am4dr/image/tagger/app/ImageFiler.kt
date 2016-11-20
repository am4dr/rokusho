package com.github.am4dr.image.tagger.app

import javafx.beans.binding.Bindings
import javafx.beans.binding.When
import javafx.beans.property.IntegerProperty
import javafx.beans.property.ListProperty
import javafx.beans.property.SimpleListProperty
import javafx.beans.property.SimpleObjectProperty
import javafx.beans.value.ObservableObjectValue
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

// TODO Mainをプロパティに持っているが、必要なimagesプロパティのみに制限する
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
        selectedTileProperty.addListener { obs, old, new -> log.debug("set selectedTileProperty: $new") }
        tiles.addListener { observable, old, new ->
            log.debug("tiles changed - new.size: ${new.size}")
            selectedTileProperty.set(null)
            flowPane.children.setAll(new)
            val nextRowStartIndexProperty = SimpleObjectProperty<Int>().apply {
                addListener { observableValue, old, new -> log.debug("next row index of selected row: $new") }
                labelTmp.textProperty().bind(this.asString())
                bind(Bindings.createObjectBinding(
                        Callable<Int> {
                            val tile = selectedTileProperty.get()
                            if (tile != null) calcNextRowStartIndex(flowPane.children, flowPane.children.indexOf(tile)) else 0
                        },
                        selectedTileProperty,
                        flowPane.children.last().layoutXProperty()))
            }

            var lastIndexBindingCreated = false

            val tileClickHandler = EventHandler<MouseEvent> { e ->
                val tile = e.source
                if (tile !is ImageTile) { return@EventHandler }
                log.debug("tile clicked")
                if (selectedTileProperty.get() === tile) {
                    selectedTileProperty.set(null)
                    return@EventHandler
                }
                selectedTileProperty.set(tile)
                /*
                if (!lastIndexBindingCreated) {
                    //createLastIndexOfSelectedRowBinding(flowPane, selectedTileProperty, nextRowStartIndexProperty)
                    lastIndexBindingCreated = true
                }
                */
            }
            new.map { tile -> tile.onMouseClicked = tileClickHandler }
        }
    }
    private fun calcNextRowStartIndex(nodes: List<Node>, index: Int): Int {
        val selectedBaseline = nodes[index].boundsInParent.maxY
        return (index+1..nodes.size-1)
                .firstOrNull { nodes[it].boundsInParent.maxY > selectedBaseline + 10 } ?: 0
    }
    // TODO 生成したバインディングを返す程度にとどめ、この中ではバインドしない
    private fun createLastIndexOfSelectedRowBinding(flowPane: Pane, selectedTileProperty: ObservableObjectValue<ImageTile>, nextRowStartIndexProperty: IntegerProperty) {
        val callable = Callable<Int> {
            val selectedTile = selectedTileProperty.get()
            selectedTile ?: return@Callable 0
            val selectedRowBaseline = selectedTile.boundsInParent.maxY
            val index = flowPane.children.indexOf(selectedTile)
            for (i in index+1..flowPane.children.size-1) {
                if (flowPane.children[i].boundsInParent.maxY > selectedRowBaseline + 10) {
                    return@Callable i
                }
            }
            return@Callable 0
        }
        nextRowStartIndexProperty.addListener { observableValue, old, new -> log.info("next row index of selected row: $new") }
        nextRowStartIndexProperty.bind(
                Bindings.createObjectBinding(callable, selectedTileProperty, flowPane.children.last().layoutXProperty()))
        log.info("binding created")
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