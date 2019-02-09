package com.github.am4dr.rokusho.javafx.sidemenu

import javafx.beans.InvalidationListener
import javafx.beans.binding.Bindings
import javafx.beans.property.SimpleStringProperty
import javafx.beans.property.StringProperty
import javafx.scene.control.Label
import javafx.scene.layout.AnchorPane
import javafx.scene.text.Font
import java.util.concurrent.Callable

class CharacterIcon : SideMenuIcon() {

    val character: StringProperty = SimpleStringProperty("*")
    private val fontSize = size.multiply(0.7)
    private val characterLeftMargin = size.subtract(fontSize).divide(2)

    private val characterLabel = Label().apply {
        textProperty().bind(character)
        fontProperty().bind(Bindings.createObjectBinding(Callable { Font(fontSize.get()) }, fontSize, character))
    }

    init {
        updateLetterAnchor()
        characterLeftMargin.addListener(InvalidationListener { updateLetterAnchor() })
        children.add(AnchorPane(characterLabel))
    }

    private fun updateLetterAnchor() {
        AnchorPane.setTopAnchor(characterLabel, 0.0)
        AnchorPane.setLeftAnchor(characterLabel, characterLeftMargin.get())
    }
}