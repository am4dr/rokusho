package com.github.am4dr.rokusho.app.gui

import java.nio.file.Path

interface LibraryPathProvider {

    fun get(): Path?
}