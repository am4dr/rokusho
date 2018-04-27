package com.github.am4dr.rokusho.app.library

import com.github.am4dr.rokusho.core.library.Library

interface RokushoLibrary<T> : Library<T> {

    val name: String
    val shortName: String get() = name
    var autoSaveEnabled: Boolean
    fun save()
}