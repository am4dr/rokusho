package com.github.am4dr.rokusho.javafx.menu

import javafx.beans.binding.When
import javafx.beans.property.*
import javafx.scene.control.Label
import javafx.scene.layout.*
import javafx.scene.paint.Color

class LibraryLabel : HBox() {

    val title: StringProperty = SimpleStringProperty()
    val labelHeight: DoubleProperty = SimpleDoubleProperty(20.0)
    val selected: BooleanProperty = SimpleBooleanProperty(false)

    init {
        setMinSize(Region.USE_COMPUTED_SIZE, Region.USE_PREF_SIZE)
        setMaxSize(Region.USE_COMPUTED_SIZE, Region.USE_PREF_SIZE)
        prefHeightProperty().bind(labelHeight)

        val selectedBorder = Border(BorderStroke(Color.BLACK, BorderStrokeStyle.SOLID, null, BorderWidths.DEFAULT))
        borderProperty().bind(When(selected).then(selectedBorder).otherwise(Border.EMPTY))

        val titleLabel = Label().apply {
            textProperty().bind(title)
        }
        children.addAll(titleLabel)
    }
}