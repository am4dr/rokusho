package com.github.am4dr.rokusho.javafx.sidemenu.foldable

import com.github.am4dr.rokusho.javafx.binding.invoke
import javafx.beans.binding.When
import javafx.beans.property.*
import javafx.scene.control.Separator
import javafx.scene.layout.Priority
import javafx.scene.layout.Region
import javafx.scene.layout.StackPane
import javafx.scene.layout.VBox

class FoldableSideColumn : StackPane() {

    val folded: BooleanProperty = SimpleBooleanProperty(true)
    val expandedWidth: DoubleProperty = SimpleDoubleProperty(250.0)
    val foldedWidth: DoubleProperty = SimpleDoubleProperty(40.0)

    val toggleClicked: ObjectProperty<(() -> Unit)?> = SimpleObjectProperty {
        if (!folded.isBound) {
            folded.value = !folded.value
        }
    }
    val settingsClicked:  ObjectProperty<(() -> Unit)?> = SimpleObjectProperty {}

    init {
        val currentWidth = When(folded).then(foldedWidth).otherwise(expandedWidth)

        val toggle = ToggleMenu().apply {
            setMinSize(0.0, 0.0)
            setMaxSize(Region.USE_PREF_SIZE, Region.USE_PREF_SIZE)
            prefWidthProperty().bind(currentWidth)
            prefHeightProperty().bind(foldedWidth)
            onClicked.set { toggleClicked() }
        }
        val settings = OptionMenu().apply {
            setMinSize(0.0, 0.0)
            setMaxSize(Region.USE_PREF_SIZE, Region.USE_PREF_SIZE)
            prefWidthProperty().bind(currentWidth)
            prefHeightProperty().bind(foldedWidth)
            onClicked.set { settingsClicked() }
        }
        setMinSize(Region.USE_PREF_SIZE, Region.USE_COMPUTED_SIZE)
        setMaxSize(Region.USE_PREF_SIZE, Region.USE_COMPUTED_SIZE)
        prefWidthProperty().bind(currentWidth)

        val contents = ContentsList().apply {
            folded.bind(this@FoldableSideColumn.folded)
        }
        VBox.setVgrow(contents, Priority.ALWAYS)
        val base = VBox(toggle, Separator(), contents, Separator(), settings)
        children.addAll(base)
    }
}