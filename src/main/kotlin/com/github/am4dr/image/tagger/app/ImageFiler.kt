package com.github.am4dr.image.tagger.app

import javafx.beans.binding.Bindings
import javafx.beans.binding.When
import javafx.beans.property.*
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
    private val thumbnailNode = ScrollPane().apply {
        fitToWidthProperty().set(true)
        val flowPane = javafx.scene.layout.FlowPane(10.0, 10.0).apply {
            alignment = Pos.CENTER
            rowValignment = VPos.BASELINE
        }
        content = flowPane
        val selectedTileProperty = SimpleObjectProperty<ImageTile>()
        tiles.addListener { observable, old, new ->
            log.debug("tiles changed: $old -> $new")
            selectedTileProperty.set(null)
            val lastIndexOfSelectedRow = SimpleIntegerProperty()
            var lastIndexBindingCreated = false
            flowPane.children.setAll(new)

            val tileClickHandler = EventHandler<MouseEvent> { e ->
                val tile = e.source
                if (tile !is ImageTile) { return@EventHandler }
                if (selectedTileProperty.get() === tile) {
                    selectedTileProperty.set(null)
                    return@EventHandler
                }
                selectedTileProperty.set(tile)
                if (!lastIndexBindingCreated) {
                    createLastIndexOfSelectedRowBinding(flowPane, selectedTileProperty, lastIndexOfSelectedRow)
                    lastIndexBindingCreated = true
                }
            }
            new.map { tile -> tile.onMouseClicked = tileClickHandler }
        }
    }
    // TODO 生成したバインディングを返す程度にとどめ、この中ではバインドしない
    private fun createLastIndexOfSelectedRowBinding(flowPane: Pane, selectedTileProperty: ObservableObjectValue<ImageTile>, lastIndexOfSelectedRow: IntegerProperty) {
        val callable = Callable<Int> {
            val selectedTile = selectedTileProperty.get()
            selectedTile ?: return@Callable flowPane.children.size - 1
            val selectedRowBaseline = selectedTile.boundsInParent.maxY
            val index = flowPane.children.indexOf(selectedTile)
            for (i in index+1..flowPane.children.size-1) {
                if (flowPane.children[i].boundsInParent.maxY > selectedRowBaseline + 10) {
                    return@Callable i - 1
                }
            }
            return@Callable flowPane.children.size - 1
        }
        lastIndexOfSelectedRow.addListener { observableValue, old, new -> log.debug("last index of selected row: ${lastIndexOfSelectedRow.get()}") }
        lastIndexOfSelectedRow.bind(Bindings.createObjectBinding(callable,// flowPane.widthProperty(), selectedTileProperty))
                flowPane.scene.window.widthProperty(), selectedTileProperty)) // TODO windowの幅ではなく行の最後の要素の位置に依存させる
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
            Label("画像ファイラー 仮置きテキスト"),
            HBox(
                Button("リスト").apply { onAction = EventHandler { selectedView.set(listNode) } },
                Button("サムネイル").apply { onAction = EventHandler { selectedView.set(thumbnailNode) } }),
            currentView)
}