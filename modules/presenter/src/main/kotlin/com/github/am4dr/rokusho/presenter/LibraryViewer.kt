package com.github.am4dr.rokusho.presenter

import com.github.am4dr.rokusho.core.library.LibraryItem
import javafx.collections.ObservableList
import javafx.scene.Node

interface LibraryViewer<T : Any> {

    val node: Node
    val items: ObservableList<LibraryItem<T>>
}