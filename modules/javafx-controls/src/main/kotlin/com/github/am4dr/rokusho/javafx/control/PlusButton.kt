package com.github.am4dr.rokusho.javafx.control

import com.github.am4dr.rokusho.javafx.binding.createBinding
import com.github.am4dr.rokusho.javafx.binding.invoke
import javafx.beans.binding.When
import javafx.beans.property.*
import javafx.scene.control.Label
import javafx.scene.layout.*
import javafx.scene.paint.Color
import javafx.scene.text.Font

class PlusButton : StackPane() {

    val size: DoubleProperty = SimpleDoubleProperty(30.0)
    val onClicked: ObjectProperty<(() -> Unit)?> = SimpleObjectProperty {}
    val tabHighlighted: BooleanProperty = SimpleBooleanProperty(false)


    init {
        setMinSize(Region.USE_PREF_SIZE, Region.USE_PREF_SIZE)
        setMaxSize(Region.USE_PREF_SIZE, Region.USE_PREF_SIZE)
        prefWidthProperty().bind(size)
        prefHeightProperty().bind(size)

        val addLabel = Label("＋").apply {
            fontProperty().bind(createBinding({
                Font(size.multiply(0.8).value)
            }, size))
        }
        setOnMouseClicked {
            it.consume()
            onClicked()
        }
        val highlightedBorder = Border(BorderStroke(Color.BLACK, BorderStrokeStyle.SOLID, null, BorderWidths.DEFAULT))
        val notHighlightedBorder = Border(BorderStroke(Color.GRAY, BorderStrokeStyle.SOLID, null, BorderWidths.DEFAULT))
        borderProperty().bind(When(tabHighlighted).then(highlightedBorder).otherwise(notHighlightedBorder))

        bindDefaults()
        children.addAll(addLabel)
    }

    private fun bindDefaults() {
        tabHighlighted.bind(hoverProperty())
    }
}