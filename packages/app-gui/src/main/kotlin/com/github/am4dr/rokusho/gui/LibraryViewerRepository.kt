package com.github.am4dr.rokusho.gui

import com.github.am4dr.rokusho.app.library.RokushoLibrary
import javafx.scene.Node

interface LibraryViewerRepository {

    fun get(library: RokushoLibrary<*>): Node
}