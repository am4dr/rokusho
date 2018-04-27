package com.github.am4dr.rokusho.app.library

import com.github.am4dr.rokusho.core.library.Library

class RokushoLibraryWrapper<T>(override val name: String,
                               library: Library<T>,
                               private val save: () -> Unit = {}) : RokushoLibrary<T>, Library<T> by library {

    override var autoSaveEnabled: Boolean = false
    override fun save() {
        save.invoke()
    }
}

fun <T> Library<T>.toRokushoLibrary(name: String): RokushoLibrary<T> = RokushoLibraryWrapper(name, this)