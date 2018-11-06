package com.github.am4dr.rokusho.gui.control

import javafx.stage.DirectoryChooser
import javafx.stage.Stage
import java.io.File
import java.nio.file.Path

class DirectoryPathChooser(
    private val stage: Stage,
    val configuration: DirectoryChooser.() -> Unit = {}
) {

    private var lastSelectedDirectory: File? = null

    fun get(): Path? {
        return selectLibraryDirectory()
    }

    private fun selectLibraryDirectory(): Path? {
        val chooser = DirectoryChooser().apply {
            initialDirectory = lastSelectedDirectory
            configuration()
        }
        synchronized(this::lastSelectedDirectory) {
            val file = chooser.showDialog(stage)?.also {
                lastSelectedDirectory = it
            }
            return  file?.toPath()
        }
    }
}