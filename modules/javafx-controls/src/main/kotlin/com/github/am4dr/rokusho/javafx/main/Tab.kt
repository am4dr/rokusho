package com.github.am4dr.rokusho.javafx.main

import com.github.am4dr.rokusho.javafx.binding.createBinding
import com.github.am4dr.rokusho.javafx.binding.invoke
import javafx.beans.binding.When
import javafx.beans.property.*
import javafx.geometry.Insets
import javafx.geometry.Pos
import javafx.scene.control.Label
import javafx.scene.control.Tooltip
import javafx.scene.layout.*
import javafx.scene.paint.Color
import javafx.scene.text.Font

class Tab : HBox(5.0) {

    val title: StringProperty = SimpleStringProperty("title")
    val tabHeight: DoubleProperty = SimpleDoubleProperty(30.0)

    val selected: BooleanProperty = SimpleBooleanProperty(false)
    val closeButtonHighlighted: BooleanProperty = SimpleBooleanProperty(false)
    val onCloseClicked: ObjectProperty<(() -> Unit)?> = SimpleObjectProperty {}
    val onClicked: ObjectProperty<(() -> Unit)?> = SimpleObjectProperty {}

    private val closeButton = Label("✖").apply {
        val buttonSize = tabHeight.multiply(0.4)
        setMinSize(Region.USE_PREF_SIZE, Region.USE_PREF_SIZE)
        setMaxSize(Region.USE_PREF_SIZE, Region.USE_PREF_SIZE)
        prefWidthProperty().bind(buttonSize)
        prefHeightProperty().bind(buttonSize)
        fontProperty().bind(createBinding({ Font(buttonSize.value) }, buttonSize))
        alignment = Pos.CENTER
        setOnMouseClicked {
            it.consume()
            onCloseClicked()
        }
        backgroundProperty().bind(When(closeButtonHighlighted)
            .then(Background(BackgroundFill(Color.DARKGRAY, CornerRadii(100.0, true), Insets.EMPTY)))
            .otherwise(Background.EMPTY))
    }

    private val titleLabel = Label().apply {
        setMinSize(0.0, Region.USE_PREF_SIZE)
        setMaxSize(Region.USE_COMPUTED_SIZE, Region.USE_PREF_SIZE)
        prefHeightProperty().bind(tabHeight)
        fontProperty().bind(createBinding({ Font(tabHeight.value * 0.5) }, tabHeight))
        textProperty().bind(title)
        tooltip = Tooltip().apply {
            textProperty().bind(title)
        }
    }

    init {
        padding = Insets(0.0, 5.0, 0.0, 5.0)
        alignment = Pos.CENTER

        setOnMouseClicked {
            it.consume()
            onClicked()
        }
        minWidthProperty().bind(tabHeight)
        minHeightProperty().bind(tabHeight)

        val selectedBorder = Border(BorderStroke(Color.BLACK, BorderStrokeStyle.SOLID, null, BorderWidths.DEFAULT))
        val notSelectedBorder = Border(BorderStroke(Color.GRAY, BorderStrokeStyle.SOLID, null, BorderWidths.DEFAULT))
        borderProperty().bind(When(selected).then(selectedBorder).otherwise(notSelectedBorder))

        bindDefaults()
        children.addAll(titleLabel, closeButton)
    }

    private fun bindDefaults() {
        closeButtonHighlighted.bind(closeButton.hoverProperty())
    }
}