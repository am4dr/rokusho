package com.github.am4dr.rokusho.app.gui

import com.github.am4dr.rokusho.adapter.RokushoLibrary

interface LibraryViewerFactory {

    fun <T : Any> create(library: RokushoLibrary<T>): LibraryViewer<T>
}