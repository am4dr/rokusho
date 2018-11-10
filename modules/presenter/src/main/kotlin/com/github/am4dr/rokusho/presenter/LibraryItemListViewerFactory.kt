package com.github.am4dr.rokusho.presenter

import com.github.am4dr.rokusho.core.library.Library

interface LibraryItemListViewerFactory {

    fun <T : Any> create(library: Library<T>): LibraryItemListViewer<T>
}