package com.github.am4dr.rokusho.presenter

import com.github.am4dr.rokusho.core.library.LibraryItem
import javafx.collections.ObservableList
import javafx.scene.Node

interface LibraryItemListViewer<T : Any> {

    val node: Node
    val items: ObservableList<LibraryItem<out T>>
}