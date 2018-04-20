package com.github.am4dr.rokusho.gui

import com.github.am4dr.rokusho.app.ImageUrl
import com.github.am4dr.rokusho.core.library.Record
import com.github.am4dr.rokusho.gui.thumbnail.ImageThumbnail
import javafx.beans.binding.Bindings
import javafx.beans.property.*
import javafx.collections.FXCollections.observableArrayList
import javafx.event.EventHandler
import javafx.scene.control.*
import javafx.scene.layout.*
import javafx.scene.paint.Color


// TODO separate into RecordListViewerContainer and Viewer
class RecordListViewer : VBox() {

    val records: ReadOnlyListProperty<Record<ImageUrl>> = SimpleListProperty(observableArrayList())

    private val content = BorderPane().apply {
        VBox.setVgrow(this, Priority.ALWAYS)
    }
    private val filterInputNode = TextField()
    val filterProperty: ReadOnlyStringProperty = filterInputNode.textProperty()

    val filteredCount: LongProperty = SimpleLongProperty(0)
    val totalCount: LongProperty = SimpleLongProperty(0)


    // TODO remove these hardcoded dependencies
    val listViewer: ListView<Record<ImageUrl>> = ListView(records)
    private val imageLoader = UrlImageLoader()
    val thumbnailViewer: RecordThumbnailViewer<ImageUrl> = RecordThumbnailViewer({ record, width, height ->
        ImageThumbnail(imageLoader.getImage(record.key.url, width, height, true))
    })

    init {
        thumbnailViewer.apply {
            val overlay = ImageOverlay().apply {
                isVisible = false
                onMouseClicked = EventHandler { isVisible = false }
                background = Background(BackgroundFill(Color.rgb(30, 30, 30, 0.75), null, null))
            }
            children.add(overlay)
            records.bindContent(this@RecordListViewer.records)
            onActionProperty.set {
                overlay.imageProperty.value = imageLoader.getImage(it.first().key.url)
                overlay.isVisible = true
            }
        }
        content.center = listViewer
        children.addAll(
                HBox(
                        Button("リスト").apply { setOnAction { content.center = listViewer } },
                        Button("サムネイル").apply { setOnAction { content.center = thumbnailViewer } },
                        Label("フィルター", filterInputNode).apply { contentDisplay = ContentDisplay.RIGHT },
                        Label().apply { textProperty().bind(Bindings.concat("[", filteredCount,  " / ", totalCount, "]")) }
                ),
                content)

    }
}