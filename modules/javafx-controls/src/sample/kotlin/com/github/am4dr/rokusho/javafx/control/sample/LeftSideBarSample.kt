package com.github.am4dr.rokusho.javafx.control.sample

import com.github.am4dr.rokusho.javafx.sidemenu.foldable.FoldableSideColumn
import javafx.geometry.Insets
import javafx.scene.layout.HBox

class LeftSideBarSample : HBox() {

    init {
        padding = Insets(20.0)
        val menu = FoldableSideColumn()
        children.add(menu)
    }
}