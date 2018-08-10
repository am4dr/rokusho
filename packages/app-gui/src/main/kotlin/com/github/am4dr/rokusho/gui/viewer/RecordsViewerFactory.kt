package com.github.am4dr.rokusho.gui.viewer

import com.github.am4dr.rokusho.app.library.RokushoLibrary
import kotlin.reflect.KClass

interface RecordsViewerFactory {

    fun acceptable(type: KClass<*>): Boolean

    fun create(library: RokushoLibrary<*>, container: RecordsViewerContainer<*>): RecordsViewer
}