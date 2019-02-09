package com.github.am4dr.rokusho.presenter

import javafx.collections.ObservableList
import javafx.scene.Node

interface ItemListViewer {

    val node: Node
    val items: ObservableList<ItemViewModel<*>>
}