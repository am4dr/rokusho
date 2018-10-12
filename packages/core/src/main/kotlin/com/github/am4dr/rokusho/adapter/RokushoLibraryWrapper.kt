package com.github.am4dr.rokusho.adapter

import kotlin.reflect.KClass
import com.github.am4dr.rokusho.old.core.library.Library as OldLibrary

class RokushoLibraryWrapper<T : Any>(override val type: KClass<T>,
                                     library: OldLibrary<T>,
                                     override val name: String,
                                     override val shortName: String = name) : RokushoLibrary<T>, OldLibrary<T> by library {
}

inline fun <reified T : Any> OldLibrary<T>.toRokushoLibrary(name: String, shortName: String = name): RokushoLibrary<T> =
        RokushoLibraryWrapper(T::class, this, name, shortName)
