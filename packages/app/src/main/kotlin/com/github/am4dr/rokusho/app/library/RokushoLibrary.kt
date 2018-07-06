package com.github.am4dr.rokusho.app.library

import com.github.am4dr.rokusho.old.core.library.Library
import kotlin.reflect.KClass

interface RokushoLibrary<T : Any> : Library<T> {

    val type: KClass<T>
    val name: String
    val shortName: String get() = name
    var autoSaveEnabled: Boolean
    fun save()
}