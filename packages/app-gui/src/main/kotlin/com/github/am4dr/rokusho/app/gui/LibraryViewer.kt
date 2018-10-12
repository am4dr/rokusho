package com.github.am4dr.rokusho.app.gui

import com.github.am4dr.rokusho.old.core.library.Record
import javafx.collections.ObservableList
import javafx.scene.Node

interface LibraryViewer<T : Any> {

    val node: Node
    val records: ObservableList<Record<T>>
}