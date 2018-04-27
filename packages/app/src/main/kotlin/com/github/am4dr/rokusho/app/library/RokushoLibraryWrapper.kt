package com.github.am4dr.rokusho.app.library

import com.github.am4dr.rokusho.core.library.Library

class RokushoLibraryWrapper<T>(library: Library<T>,
                               override val name: String,
                               override val shortName: String = name,
                               private val save: () -> Unit = {}) : RokushoLibrary<T>, Library<T> by library {

    override var autoSaveEnabled: Boolean = false
    override fun save() {
        save.invoke()
    }
}

fun <T> Library<T>.toRokushoLibrary(name: String,
                                    shortName: String = name,
                                    save: () -> Unit = {}): RokushoLibrary<T> = RokushoLibraryWrapper(this, name, shortName, save)