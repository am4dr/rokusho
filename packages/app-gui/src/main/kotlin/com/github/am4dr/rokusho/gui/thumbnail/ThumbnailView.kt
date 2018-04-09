package com.github.am4dr.rokusho.gui.thumbnail

import com.github.am4dr.rokusho.javafx.collection.ConcatenatedList
import com.github.am4dr.rokusho.gui.control.FittingTextField
import javafx.beans.binding.Bindings
import javafx.collections.FXCollections
import javafx.collections.ObservableList
import javafx.geometry.Insets
import javafx.scene.Node
import javafx.scene.control.Button
import javafx.scene.control.TextField
import javafx.scene.input.KeyCode
import javafx.scene.layout.*
import javafx.scene.paint.Color
import javafx.scene.text.Font

class ThumbnailView(private val content: Region) : StackPane() {

    var onInputCommitted: ((String) -> Unit)? = null
    var onEditEnded: (() -> Unit)? = null

    val tagNodes: ObservableList<Node> = FXCollections.observableArrayList()

    private val addTagButton: Button = createAddButton()
    private val inputTextField: TextField = createInputBox()

    private val overlayContents = ConcatenatedList.concat(tagNodes, FXCollections.observableArrayList<Node>(inputTextField, addTagButton))

    init {
        inputTextField.apply {
            visibleProperty().set(false)
            managedProperty().bind(visibleProperty())
            focusedProperty().addListener { _, _, new ->
                if (new == false) {
                    visibleProperty().set(false)
                    onEditEnded?.invoke()
                }
            }
            val releaseFocus = this@ThumbnailView::requestFocus
            setOnKeyReleased {
                if (it.code == KeyCode.ESCAPE) {
                    it.consume()
                    text = ""
                    releaseFocus()
                }
            }
            setOnAction {
                if (text.isNullOrBlank()) {
                    releaseFocus()
                    return@setOnAction
                }
                onInputCommitted?.invoke(text)
                text = ""
            }
        }
        addTagButton.apply {
            setOnAction {
                inputTextField.visibleProperty().set(true)
                inputTextField.requestFocus()
            }
        }

        val overlay = createOverlayPane().apply {
            Bindings.bindContent(children, overlayContents)
            visibleProperty().bind(this@ThumbnailView.hoverProperty().or(inputTextField.focusedProperty()))

            prefWidthProperty().bind(content.widthProperty())
            prefHeightProperty().bind(content.heightProperty())
            maxWidthProperty().bind(content.widthProperty())
            maxHeightProperty().bind(content.heightProperty())
        }
        children.addAll(content, overlay)
    }

    private fun createInputBox() = FittingTextField().apply {
        font = Font(14.0)
        background = Background(BackgroundFill(Color.WHITE, CornerRadii(2.0), null))
        padding = Insets(-1.0, 2.0, 0.0, 2.0)
    }
    private fun createAddButton() = Button(" + ").apply {
        textFill = Color.rgb(200, 200, 200)
        padding = Insets(-1.0, 2.0, 0.0, 2.0)
        font = Font(14.0)
        background = Background(BackgroundFill(Color.BLACK, CornerRadii(2.0), null))
    }
    private fun createOverlayPane() = FlowPane(7.5, 5.0).apply {
        padding = Insets(10.0)
        background = Background(BackgroundFill(Color.rgb(0, 0, 0, 0.5), null, null))
    }
}