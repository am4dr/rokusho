package com.github.am4dr.rokusho.gui.control

import javafx.beans.binding.DoubleBinding
import javafx.beans.property.DoubleProperty
import javafx.beans.property.SimpleDoubleProperty
import javafx.geometry.Insets
import javafx.geometry.Pos
import javafx.scene.control.TextField
import javafx.scene.text.Text

class FittingTextField : TextField("") {
    private val dummyTextForWidthCalculation = Text().apply {
        fontProperty().bind(this@FittingTextField.fontProperty())
        textProperty().bind(this@FittingTextField.textProperty())
    }
    val paddingWidth: DoubleProperty = SimpleDoubleProperty(10.0)
    init {
        alignment = Pos.CENTER
        padding = Insets(0.0, 0.0, 0.0, 0.0)
        prefWidthProperty().bind(object : DoubleBinding() {
            init { super.bind(paddingWidth, dummyTextForWidthCalculation.layoutBoundsProperty()) }
            override fun computeValue(): Double =
                    paddingWidth.get() + dummyTextForWidthCalculation.layoutBounds.width
        })
    }
}