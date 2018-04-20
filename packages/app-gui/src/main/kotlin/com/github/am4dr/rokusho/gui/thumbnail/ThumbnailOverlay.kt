package com.github.am4dr.rokusho.gui.thumbnail

import com.github.am4dr.rokusho.gui.control.FittingTextField
import com.github.am4dr.rokusho.javafx.collection.ConcatenatedList
import javafx.beans.binding.Bindings
import javafx.beans.property.ObjectProperty
import javafx.beans.property.ReadOnlyBooleanProperty
import javafx.beans.property.SimpleBooleanProperty
import javafx.beans.property.SimpleObjectProperty
import javafx.collections.FXCollections
import javafx.collections.ObservableList
import javafx.geometry.Insets
import javafx.scene.Node
import javafx.scene.control.Button
import javafx.scene.control.TextField
import javafx.scene.input.KeyCode
import javafx.scene.layout.Background
import javafx.scene.layout.BackgroundFill
import javafx.scene.layout.CornerRadii
import javafx.scene.layout.FlowPane
import javafx.scene.paint.Color
import javafx.scene.text.Font

class ThumbnailOverlay : FlowPane() {

    val tagNodes: ObservableList<Node> = FXCollections.observableArrayList()
    val inputFocusedProperty: ReadOnlyBooleanProperty = SimpleBooleanProperty(false)
    val onInputCommittedProperty: ObjectProperty<((String) -> Unit)> = SimpleObjectProperty({ _ -> })
    val onEditEndedProperty: ObjectProperty<(() -> Unit)> = SimpleObjectProperty({})

    private val addTagButton: Button = createAddButton()
    private val inputTextField: TextField = createInputBox()
    private val overlayContents = ConcatenatedList.concat(tagNodes, FXCollections.observableArrayList<Node>(inputTextField, addTagButton))

    init {
        padding = Insets(10.0)
        background = Background(BackgroundFill(Color.rgb(0, 0, 0, 0.5), null, null))
        hgap = 7.5
        vgap = 5.0

        val releaseInputFocus = this::requestFocus
        inputTextField.apply {
            visibleProperty().set(false)
            managedProperty().bind(visibleProperty())
            focusedProperty().addListener { _, _, new ->
                if (new == false) {
                    visibleProperty().set(false)
                    onEditEndedProperty.get().invoke()
                }
            }
            setOnKeyReleased {
                if (it.code == KeyCode.ESCAPE) {
                    it.consume()
                    text = ""
                    releaseInputFocus()
                }
            }
            setOnAction {
                if (text.isNullOrBlank()) {
                    releaseInputFocus()
                    return@setOnAction
                }
                onInputCommittedProperty.get().invoke(text)
                text = ""
            }
            (inputFocusedProperty as SimpleBooleanProperty).bind(focusedProperty())
        }
        addTagButton.apply {
            setOnAction {
                inputTextField.visibleProperty().set(true)
                inputTextField.requestFocus()
            }
        }
        Bindings.bindContent(children, overlayContents)
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
}