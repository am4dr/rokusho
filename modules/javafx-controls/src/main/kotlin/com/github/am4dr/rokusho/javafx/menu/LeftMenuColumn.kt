package com.github.am4dr.rokusho.javafx.menu

import com.github.am4dr.rokusho.javafx.binding.invoke
import javafx.beans.binding.When
import javafx.beans.property.*
import javafx.scene.control.Separator
import javafx.scene.layout.Priority
import javafx.scene.layout.Region
import javafx.scene.layout.StackPane
import javafx.scene.layout.VBox

class LeftMenuColumn : StackPane() {

    val folded: BooleanProperty = SimpleBooleanProperty(false)
    val expandedWidth: DoubleProperty = SimpleDoubleProperty(200.0)
    val foldedWidth: DoubleProperty = SimpleDoubleProperty(40.0)

    val onSettingsClicked:  ObjectProperty<(() -> Unit)?> = SimpleObjectProperty {}
    val toggleClicked: ObjectProperty<(() -> Unit)?> = SimpleObjectProperty {
        if (!folded.isBound) {
            folded.value = !folded.value
        }
    }

    val libraryList = LibraryListMenu().apply {
        folded.bind(this@LeftMenuColumn.folded)
        VBox.setVgrow(this, Priority.ALWAYS)
    }
    val toggle: ToggleMenu
    val settings: OptionMenu

    init {
        val currentWidth = When(folded).then(foldedWidth).otherwise(expandedWidth)

        toggle = ToggleMenu().apply {
            setMinSize(0.0, 0.0)
            setMaxSize(Region.USE_PREF_SIZE, Region.USE_PREF_SIZE)
            prefWidthProperty().bind(currentWidth)
            prefHeightProperty().bind(foldedWidth)
            onClicked.set { toggleClicked() }
        }
        settings = OptionMenu().apply {
            setMinSize(0.0, 0.0)
            setMaxSize(Region.USE_PREF_SIZE, Region.USE_PREF_SIZE)
            prefWidthProperty().bind(currentWidth)
            prefHeightProperty().bind(foldedWidth)
            onClicked.set { onSettingsClicked() }
        }
        setMinSize(Region.USE_PREF_SIZE, Region.USE_COMPUTED_SIZE)
        setMaxSize(Region.USE_PREF_SIZE, Region.USE_COMPUTED_SIZE)
        prefWidthProperty().bind(currentWidth)

        children.addAll(VBox(toggle, Separator(), libraryList, Separator(), settings))
    }
}