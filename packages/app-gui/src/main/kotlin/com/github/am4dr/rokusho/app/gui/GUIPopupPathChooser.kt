package com.github.am4dr.rokusho.app.gui

import javafx.stage.DirectoryChooser
import javafx.stage.Stage
import java.io.File
import java.nio.file.Path

class GUIPopupPathChooser(private val stage: Stage) : LibraryPathProvider {

    override fun get(): Path? {
        return selectLibraryDirectory()
    }

    private var lastSelectedDirectory: File? = null
    private fun selectLibraryDirectory(): Path? {
        DirectoryChooser().run {
            title = "画像があるディレクトリを選択してください"
            initialDirectory = lastSelectedDirectory
            return showDialog(stage)?.also {
                lastSelectedDirectory = it
            }?.toPath()
        }
    }
}