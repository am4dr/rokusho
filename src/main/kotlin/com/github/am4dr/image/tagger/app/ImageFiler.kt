package com.github.am4dr.image.tagger.app

import javafx.beans.binding.Bindings
import javafx.beans.binding.When
import javafx.beans.property.SimpleListProperty
import javafx.beans.property.SimpleObjectProperty
import javafx.collections.FXCollections
import javafx.collections.ObservableList
import javafx.event.EventHandler
import javafx.scene.Node
import javafx.scene.control.Button
import javafx.scene.control.Label
import javafx.scene.control.ListView
import javafx.scene.control.ScrollPane
import javafx.scene.layout.BorderPane
import javafx.scene.layout.HBox
import javafx.scene.layout.Priority
import javafx.scene.layout.VBox
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.util.concurrent.Callable

// TODO Mainをプロパティに持っているが、必要なimagesプロパティのみに制限する
// TODO ビューの種類を選択可能にする
// TODO サムネイルビューに拡大表示を実装する
class ImageFiler(val mainFrame: MainFrame) {
    private val log: Logger = LoggerFactory.getLogger(this.javaClass)
    private val listNode = ListView<ImageData>().apply {
        itemsProperty().bind(mainFrame.imagesProperty)
    }
    private val tiles = SimpleListProperty<ImageTile>().apply {
        val images = mainFrame.imagesProperty
        val callable = Callable<ObservableList<ImageTile>> {
            FXCollections.observableList(images.get().filterNotNull().map(::ImageTile))
        }
        bind(Bindings.createObjectBinding(callable, images))
    }
    private val thumbnailNode = ScrollPane().apply {
        fitToWidthProperty().set(true)
        val flowPane = javafx.scene.layout.FlowPane(10.0, 10.0)
        content = flowPane
        tiles.addListener { observable, old, new ->
            log.debug("tiles changed: $old -> $new")
            flowPane.children.setAll(new)
        }
    }
    private val selectedView = SimpleObjectProperty<Node>().apply { set(thumbnailNode) }
    private val currentView: Node = BorderPane().apply {
        VBox.setVgrow(this, Priority.ALWAYS)
        centerProperty().bind(
                When(mainFrame.imagesProperty.sizeProperty().isEqualTo(0))
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