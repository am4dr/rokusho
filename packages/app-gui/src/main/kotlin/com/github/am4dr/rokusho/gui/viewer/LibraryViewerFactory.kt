package com.github.am4dr.rokusho.gui.viewer

import com.github.am4dr.rokusho.app.library.RokushoLibrary
import javafx.scene.Node

interface LibraryViewerFactory {

    fun create(library: RokushoLibrary<*>): Node
}