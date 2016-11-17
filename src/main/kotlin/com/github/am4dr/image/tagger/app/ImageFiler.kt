package com.github.am4dr.image.tagger.app

import javafx.beans.binding.When
import javafx.beans.property.SimpleObjectProperty
import javafx.collections.FXCollections
import javafx.scene.Node
import javafx.scene.control.Label
import javafx.scene.control.ListView
import javafx.scene.image.ImageView
import javafx.scene.layout.BorderPane
import javafx.scene.layout.Priority
import javafx.scene.layout.TilePane
import javafx.scene.layout.VBox
import org.slf4j.Logger
import org.slf4j.LoggerFactory

// TODO Mainをプロパティに持っているが、必要なimagesプロパティのみに制限する
// TODO tileビューも実装する
class ImageFiler(val mainFrame: MainFrame) {
    private val log: Logger = LoggerFactory.getLogger(this.javaClass)
    private val listNode = ListView<ImageData>().apply {
        itemsProperty().bind(mainFrame.imagesProperty)
    }
    private val tiles = FXCollections.observableArrayList<ImageView>()
    private val tilePane = TilePane().apply {
    }
    private val selectedView = SimpleObjectProperty<Node>().apply { set(listNode) }
    private val currentView: Node = BorderPane().apply {
        VBox.setVgrow(this, Priority.ALWAYS)
        centerProperty().bind(
                When(mainFrame.imagesProperty.sizeProperty().isEqualTo(0))
                        .then<Node>(mainFrame.makeEmptyTargetPane())
                        .otherwise(selectedView))
    }
    val node: Node = VBox(Label("画像ファイラー 仮置きテキスト"), currentView)
}