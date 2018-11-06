package com.github.am4dr.rokusho.javafx.control.sample

import com.github.am4dr.javafx.sample_viewer.RestorableNode
import com.github.am4dr.rokusho.javafx.control.FittingTextField
import javafx.beans.property.SimpleStringProperty
import javafx.beans.property.StringProperty
import javafx.geometry.Insets
import javafx.scene.layout.FlowPane

class FittingTextFieldSample : FlowPane(), RestorableNode {

    companion object {
        fun createStates(): Map<String, Any> = mapOf("text" to SimpleStringProperty())
    }

    val textField = FittingTextField()

    init {
        padding = Insets(20.0)
        children.add(textField)
    }

    override fun restore(states: MutableMap<String, Any>) {
        states["text"]?.let {
            if (it is StringProperty) {
                textField.text = it.value
                it.bind(textField.textProperty())
            }
        }
    }
}
