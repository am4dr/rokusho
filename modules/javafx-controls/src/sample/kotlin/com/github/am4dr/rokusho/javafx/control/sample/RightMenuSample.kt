package com.github.am4dr.rokusho.javafx.control.sample

import com.github.am4dr.rokusho.javafx.sidemenu.RightTabColumn
import javafx.geometry.Insets
import javafx.scene.layout.HBox
import javafx.scene.layout.Pane
import javafx.scene.layout.Priority

class RightMenuSample : HBox() {

    init {
        padding = Insets(20.0)
        children.addAll(Pane().apply {
            HBox.setHgrow(this, Priority.ALWAYS)
        }, RightTabColumn())
    }
}