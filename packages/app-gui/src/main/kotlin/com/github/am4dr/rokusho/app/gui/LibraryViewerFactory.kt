package com.github.am4dr.rokusho.app.gui

import com.github.am4dr.rokusho.old.core.library.Library

interface LibraryViewerFactory {

    fun <T : Any> create(library: Library<T>): LibraryViewer<T>
}