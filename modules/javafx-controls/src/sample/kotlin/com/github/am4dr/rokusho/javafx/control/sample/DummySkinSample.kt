package com.github.am4dr.rokusho.javafx.control.sample

import com.github.am4dr.rokusho.javafx.util.Dummy
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