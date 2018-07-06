package com.github.am4dr.rokusho.app.library

import com.github.am4dr.rokusho.old.core.library.Library
import kotlin.reflect.KClass

class RokushoLibraryWrapper<T : Any>(override val type: KClass<T>,
                                     library: Library<T>,
                                     override val name: String,
                                     override val shortName: String = name,
                                     private val save: () -> Unit = {}) : RokushoLibrary<T>, Library<T> by library {

    override var autoSaveEnabled: Boolean = false
    override fun save() {
        save.invoke()
    }
}

inline fun <reified T : Any> Library<T>.toRokushoLibrary(name: String,
                                                         shortName: String = name,
                                                         noinline save: () -> Unit = {}): RokushoLibrary<T> =
        RokushoLibraryWrapper(T::class, this, name, shortName, save)