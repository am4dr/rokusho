package com.github.am4dr.rokusho.javafx.menu

import com.github.am4dr.rokusho.javafx.binding.createBinding
import com.github.am4dr.rokusho.javafx.binding.invoke
import javafx.beans.property.ObjectProperty
import javafx.beans.property.SimpleObjectProperty
import javafx.geometry.Pos
import javafx.scene.layout.HBox
import javafx.scene.text.Font
import javafx.scene.text.Text
import javafx.scene.text.TextFlow

class OptionMenu : HBox() {

    val onClicked: ObjectProperty<(() -> Unit)?> = SimpleObjectProperty {}

    init {
        val icon = Text("⚙").apply {
            fontProperty().bind(createBinding({
                Font(height)
            }, widthProperty()))
        }
        val text = TextFlow(Text(" "), icon).apply {
            prefWidthProperty().bind(this@OptionMenu.widthProperty())
        }
        alignment = Pos.CENTER
        setOnMouseClicked {
            onClicked()
        }
        children.addAll(text)
    }
}