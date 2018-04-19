package com.github.am4dr.rokusho.gui

import com.github.am4dr.rokusho.app.ImageUrl
import com.github.am4dr.rokusho.core.library.Record
import javafx.beans.binding.Bindings
import javafx.beans.property.*
import javafx.collections.FXCollections.observableArrayList
import javafx.scene.control.*
import javafx.scene.layout.BorderPane
import javafx.scene.layout.HBox
import javafx.scene.layout.Priority
import javafx.scene.layout.VBox


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
    val thumbnailViewer: RecordThumbnailViewer = RecordThumbnailViewer()

    init {
        thumbnailViewer.records.bindContent(records)
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