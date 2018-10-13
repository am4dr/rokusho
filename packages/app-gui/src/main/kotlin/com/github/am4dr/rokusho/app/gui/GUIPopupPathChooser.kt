package com.github.am4dr.rokusho.app.gui

import javafx.stage.DirectoryChooser
import javafx.stage.Stage
import java.io.File
import java.nio.file.Path

class GUIPopupPathChooser(private val stage: Stage, val defaultTitle: String = "") {

    fun get(title: String = defaultTitle): Path? {
        return selectLibraryDirectory(title)
    }

    private var lastSelectedDirectory: File? = null
    private fun selectLibraryDirectory(title: String): Path? {
        DirectoryChooser().run {
            this.title = title
            initialDirectory = lastSelectedDirectory
            return showDialog(stage)?.also {
                lastSelectedDirectory = it
            }?.toPath()
        }
    }
}