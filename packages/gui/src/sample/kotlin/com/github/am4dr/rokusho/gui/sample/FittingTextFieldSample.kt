package com.github.am4dr.rokusho.gui.sample

import com.github.am4dr.rokusho.gui.control.FittingTextField
import javafx.geometry.Insets
import javafx.scene.layout.FlowPane

class FittingTextFieldSample : FlowPane() {

    init {
        padding = Insets(20.0)
        val textField = FittingTextField()
        children.add(textField)
    }
}
