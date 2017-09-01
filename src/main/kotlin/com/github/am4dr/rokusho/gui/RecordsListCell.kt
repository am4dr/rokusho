package com.github.am4dr.rokusho.gui

import javafx.scene.layout.Pane
import javafx.scene.text.Text
import javafx.scene.text.TextFlow

class RecordsListCell(name: String) : Pane() {
    init {
        val tf = TextFlow(Text(name))
        children.add(tf)
    }
}