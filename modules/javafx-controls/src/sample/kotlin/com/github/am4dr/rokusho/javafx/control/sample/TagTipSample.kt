package com.github.am4dr.rokusho.javafx.control.sample

import com.github.am4dr.javafx.sample_viewer.RestorableNode
import com.github.am4dr.rokusho.javafx.control.TagTip
import javafx.beans.property.SimpleStringProperty
import javafx.beans.property.StringProperty
import javafx.geometry.Insets
import javafx.geometry.Orientation
import javafx.scene.control.ContentDisplay
import javafx.scene.control.Label
import javafx.scene.control.Separator
import javafx.scene.control.TextField
import javafx.scene.layout.FlowPane
import javafx.scene.text.Text

class TagTipSample : FlowPane(Orientation.VERTICAL, 3.0, 3.0), RestorableNode {

    companion object {
        fun createStates(): Map<String, Any> = mapOf("text" to SimpleStringProperty("TagTip"))
    }

    val text = TextField("TagTip")
    val tagTip = TagTip()

    init {
        tagTip.textProperty().bind(text.textProperty())

        padding = Insets(20.0)
        children.addAll(
                Text("自由なテキストでタグを作るサンプル"),
                Label("入力: ", text).apply { contentDisplay = ContentDisplay.RIGHT },
                Label("タグ: ", tagTip).apply { contentDisplay = ContentDisplay.RIGHT },
                Separator(Orientation.HORIZONTAL))
    }

    override fun restore(states: MutableMap<String, Any>) {
        states["text"]?.let {
            if (it is StringProperty) {
                text.text = it.value
                it.bind(text.textProperty())
            }
        }
    }
}