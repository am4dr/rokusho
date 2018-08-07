package com.github.am4dr.rokusho.gui.util

import javafx.beans.property.SimpleDoubleProperty
import javafx.geometry.Insets
import javafx.scene.control.Control
import javafx.scene.control.SkinBase
import javafx.scene.layout.*
import javafx.scene.paint.Color
import javafx.scene.text.Text
import javafx.scene.text.TextFlow


class DummySkin<C : Control>(val control: C) : SkinBase<C>(control) {

    private val width = SimpleDoubleProperty(0.0)
    private val height = SimpleDoubleProperty(0.0)
    private val text = Text()

    private val dummyPane = createDummyPane()

    private fun createDummyPane(): Pane = Pane().apply {
        children.add(TextFlow(text))
        border = Border(BorderStroke(Color.BLACK, BorderStrokeStyle.SOLID, null, BorderWidths(1.0), Insets.EMPTY))
    }

    init {
        width.bind(dummyPane.widthProperty())
        height.bind(dummyPane.heightProperty())
        text.textProperty().bind(width.asString().concat(" x ").concat(height.asString()))
        children.add(dummyPane)
    }
}