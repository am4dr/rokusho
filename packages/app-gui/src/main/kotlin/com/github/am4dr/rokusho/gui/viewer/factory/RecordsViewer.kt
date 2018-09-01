package com.github.am4dr.rokusho.gui.viewer.factory

import com.github.am4dr.rokusho.core.library.Record
import javafx.collections.FXCollections
import javafx.collections.ObservableList
import javafx.scene.Node

class RecordsViewer<T>(val label: String, val viewer: Node, val records: ObservableList<Record<T>> = FXCollections.observableArrayList())