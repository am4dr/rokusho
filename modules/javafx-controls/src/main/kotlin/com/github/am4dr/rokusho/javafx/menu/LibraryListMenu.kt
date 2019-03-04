package com.github.am4dr.rokusho.javafx.menu

import com.github.am4dr.rokusho.javafx.control.PlusButton
import javafx.beans.binding.Bindings.bindContent
import javafx.beans.binding.When
import javafx.beans.property.BooleanProperty
import javafx.beans.property.ObjectProperty
import javafx.beans.property.SimpleBooleanProperty
import javafx.beans.property.SimpleObjectProperty
import javafx.collections.FXCollections.observableArrayList
import javafx.collections.ObservableList
import javafx.geometry.Insets
import javafx.geometry.Pos
import javafx.geometry.VPos
import javafx.scene.Node
import javafx.scene.control.Label
import javafx.scene.control.Separator
import javafx.scene.layout.HBox
import javafx.scene.layout.Priority
import javafx.scene.layout.VBox
import javafx.scene.text.Font
import javafx.scene.text.TextAlignment

class LibraryListMenu : VBox() {

    val folded: BooleanProperty = SimpleBooleanProperty(false)
    val labels: ObservableList<Node> = observableArrayList()
    val onAddClicked: ObjectProperty<(() -> Unit)?> = SimpleObjectProperty()

    private val menuLabelSize = 20.0
    init {
        paddingProperty().bind(When(folded)
            .then(Insets(0.0, 0.0, 0.0, 0.0))
            .otherwise(Insets(10.0, 0.0, 10.0, 10.0)))
        val label = Label("Libraries").apply {
            textAlignment = TextAlignment.RIGHT
            minWidth = 0.0
            font = Font(menuLabelSize)
            managedProperty().bind(folded.not())
            visibleProperty().bind(folded.not())
        }
        val plusButton = PlusButton().apply {
            size.value = menuLabelSize * 0.9
        }
        val list = VBox()
        VBox.setVgrow(list, Priority.ALWAYS)
        bindContent(list.children, labels)

        val spacing = Separator().apply {
            HBox.setHgrow(this, Priority.ALWAYS)
            valignment = VPos.BASELINE
            visibleProperty().value = false
            managedProperty().bind(folded.not())
        }
        val labelRow = HBox(label, spacing, plusButton).apply {
            alignment = Pos.BASELINE_CENTER
            paddingProperty().bind(When(folded)
                .then(Insets(0.0, 0.0, 0.0, 0.0))
                .otherwise(Insets(0.0, 10.0, 0.0, 0.0)))
        }
        val separator = Separator().apply {
            visibleProperty().bind(folded.not())
        }
        children.addAll(labelRow, separator, list)
    }
}