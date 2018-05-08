package com.github.am4dr.rokusho.app

import com.github.am4dr.rokusho.app.library.RokushoLibrary

interface LibraryLoader<in S, T : Any> {

    val name: String

    fun load(specifier: String): RokushoLibrary<T>?
    fun load(specifier: S): RokushoLibrary<T>
}