package com.github.am4dr.rokusho.launcher

import com.github.am4dr.rokusho.app.ImageUrl
import com.github.am4dr.rokusho.app.Rokusho
import com.github.am4dr.rokusho.app.Rokusho.Companion.isSupportedImageFile
import com.github.am4dr.rokusho.app.SaveDataStoreProvider
import com.github.am4dr.rokusho.app.datastore.yaml.YamlSaveDataStore
import com.github.am4dr.rokusho.app.library.RokushoLibrary
import com.github.am4dr.rokusho.app.library.fs.FileSystemLibraryLoader
import com.github.am4dr.rokusho.app.library.fs.LibraryRootDetector
import com.github.am4dr.rokusho.app.library.toRokushoLibrary
import com.github.am4dr.rokusho.core.library.filter
import com.github.am4dr.rokusho.core.library.transform
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

    private val rokusho = Rokusho()
    private val fsLoader = createFileSystemLibraryLoader()
    private fun getLibrary(path: Path): RokushoLibrary<ImageUrl> =
            fsLoader.load(path).let { base ->
                base.filter { isSupportedImageFile(it) }
                        .transform { ImageUrl(it.toUri().toURL()) }
                        .toRokushoLibrary(path.toString(), base::save)
            }

    companion object {
        @JvmStatic fun main(args: Array<String>) = Application.launch(Launcher::class.java, *args)
        private val log = LoggerFactory.getLogger(Launcher::class.java)
    }

    override fun init() {
        log.info("launched with the params: ${parameters.raw}")
        parseArgs(parameters.raw.toTypedArray()).args
                .map { Paths.get(it) }
                .filter { Files.isDirectory(it) }
                .forEach { rokusho.addLibrary(getLibrary(it)) }
    }

    private fun parseArgs(args: Array<String>): CommandLine = DefaultParser().parse(Options(), args)

    override fun start(stage: Stage) {
        stage.run {
            title = "Rokusho"
            val rokushoGui = RokushoGui(rokusho, stage, { path -> rokusho.addLibrary(getLibrary(path)) }, { it.save() })
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

private fun createFileSystemLibraryLoader(): FileSystemLibraryLoader {
    val saveFileName = "rokusho.yaml"
    val libraryRootDetector: LibraryRootDetector = { path -> Files.isRegularFile(path.resolve(saveFileName)) }
    val saveDataStoreProvider = SaveDataStoreProvider { YamlSaveDataStore(it.resolve(saveFileName)) }
    return FileSystemLibraryLoader(libraryRootDetector, saveDataStoreProvider::getOrCreate)
}
