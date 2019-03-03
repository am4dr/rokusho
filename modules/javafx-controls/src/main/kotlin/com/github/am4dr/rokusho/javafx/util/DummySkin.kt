package com.github.am4dr.rokusho.javafx.util

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
    private val text = TextFlow()

    private val dummyPane = createDummyPane()

    private fun createDummyPane(): Pane = FlowPane(TextFlow(text)).apply {
        border = Border(BorderStroke(Color.BLACK, BorderStrokeStyle.SOLID, null, BorderWidths(1.0), Insets.EMPTY))
        padding = Insets(5.0)
        setMinSize(0.0, 0.0)
    }

    init {
        width.bind(dummyPane.widthProperty())
        height.bind(dummyPane.heightProperty())
        val widthText = Text().apply {
            textProperty().bind(width.asString())
        }
        val heightText = Text().apply {
            textProperty().bind(height.asString())
        }
        text.children.addAll(widthText, Text(" x "), heightText)
        text.prefWidthProperty().bind(dummyPane.widthProperty())
        children.add(dummyPane)
    }
}