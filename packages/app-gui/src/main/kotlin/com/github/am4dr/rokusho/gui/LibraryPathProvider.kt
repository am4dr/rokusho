package com.github.am4dr.rokusho.gui

import java.nio.file.Path

interface LibraryPathProvider {

    fun get(): Path?
}