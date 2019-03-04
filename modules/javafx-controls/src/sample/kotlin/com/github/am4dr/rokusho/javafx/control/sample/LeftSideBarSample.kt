package com.github.am4dr.rokusho.javafx.control.sample

import com.github.am4dr.rokusho.javafx.menu.LeftMenuColumn
import com.github.am4dr.rokusho.javafx.menu.LibraryLabel
import javafx.geometry.Insets
import javafx.scene.layout.HBox

class LeftSideBarSample : HBox() {

    init {
        padding = Insets(20.0)
        val menu = LeftMenuColumn()
        menu.libraryList.labels.addAll(
            LibraryLabel().apply { title.value = "Library 1"},
            LibraryLabel().apply { title.value = "Library 2"},
            LibraryLabel().apply { title.value = "Library 3"}
        )
        children.add(menu)
    }
}