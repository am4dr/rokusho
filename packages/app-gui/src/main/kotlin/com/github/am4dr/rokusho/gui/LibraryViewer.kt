package com.github.am4dr.rokusho.gui

import com.github.am4dr.rokusho.core.library.Record
import javafx.collections.ObservableList
import javafx.scene.Node

interface LibraryViewer<T : Any> {

    val node: Node
    val records: ObservableList<Record<T>>
}