package com.github.am4dr.rokusho.gui.sample

import com.github.am4dr.rokusho.gui.util.Dummy
import javafx.geometry.Insets
import javafx.scene.layout.BorderPane
import javafx.scene.text.Text

class DummySkinSample : BorderPane() {

    init {
        padding = Insets(20.0)
        top = Text("DummySkinのサンプル")
        center = Dummy()
    }
}