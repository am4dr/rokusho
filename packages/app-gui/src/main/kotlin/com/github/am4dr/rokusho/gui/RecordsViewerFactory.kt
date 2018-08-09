package com.github.am4dr.rokusho.gui

import com.github.am4dr.rokusho.app.library.RokushoLibrary
import com.github.am4dr.rokusho.gui.viewer.RecordsViewer
import com.github.am4dr.rokusho.gui.viewer.RecordsViewerContainer
import kotlin.reflect.KClass

interface RecordsViewerFactory {

    fun acceptable(type: KClass<*>): Boolean

    fun create(library: RokushoLibrary<*>, container: RecordsViewerContainer<*>): RecordsViewer
}