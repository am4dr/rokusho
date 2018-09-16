package com.github.am4dr.rokusho.gui

import com.github.am4dr.rokusho.app.library.RokushoLibrary

interface LibraryViewerFactory {

    fun <T : Any> create(library: RokushoLibrary<T>): LibraryViewer<T>
}