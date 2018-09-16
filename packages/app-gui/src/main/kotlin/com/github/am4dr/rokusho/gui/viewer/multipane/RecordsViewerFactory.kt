package com.github.am4dr.rokusho.gui.viewer.multipane

import com.github.am4dr.rokusho.app.library.RokushoLibrary
import kotlin.reflect.KClass

interface RecordsViewerFactory {

    fun isAcceptable(type: KClass<*>): Boolean

    fun create(library: RokushoLibrary<*>): RecordsViewer<*>?
}