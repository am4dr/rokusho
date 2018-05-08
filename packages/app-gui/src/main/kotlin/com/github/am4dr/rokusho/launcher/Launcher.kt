package com.github.am4dr.rokusho.launcher

import com.github.am4dr.rokusho.app.ImageLibraryLoader
import com.github.am4dr.rokusho.app.Rokusho
import com.github.am4dr.rokusho.dev.gui.RokushoViewer
import com.github.am4dr.rokusho.gui.RokushoGui
import javafx.application.Application
import javafx.scene.Scene
import javafx.stage.Stage
import org.apache.commons.cli.CommandLine
import org.apache.commons.cli.DefaultParser
import org.apache.commons.cli.Options
import org.slf4j.LoggerFactory
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths

class Launcher : Application() {

    private val rokusho = Rokusho(listOf(ImageLibraryLoader()))

    companion object {
        @JvmStatic fun main(args: Array<String>) = Application.launch(Launcher::class.java, *args)
        private val log = LoggerFactory.getLogger(Launcher::class.java)
    }

    override fun init() {
        log.info("launched with the params: ${parameters.raw}")
        parseArgs(parameters.raw.toTypedArray()).args
                .map { Paths.get(it) }
                .filter { Files.isDirectory(it) }
                .forEach { rokusho.loadAndAddLibrary<Path, ImageLibraryLoader>(it) }
    }

    private fun parseArgs(args: Array<String>): CommandLine = DefaultParser().parse(Options(), args)

    override fun start(stage: Stage) {
        stage.run {
            title = "Rokusho"
            val rokushoGui = RokushoGui(rokusho, stage, { path -> rokusho.loadAndAddLibrary<Path, ImageLibraryLoader>(path) }, { it.save() })
            scene = Scene(rokushoGui.mainParent, 800.0, 500.0)
            show()
        }
        RokushoViewer(rokusho).also { devViewer ->
            devViewer.stage.apply {
                x = stage.x - RokushoViewer.initialWidth - 2.0
                y = stage.y
            }
        }.show()
    }
}
