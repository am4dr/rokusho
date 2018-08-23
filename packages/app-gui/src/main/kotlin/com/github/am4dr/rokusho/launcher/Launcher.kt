package com.github.am4dr.rokusho.launcher

import com.github.am4dr.rokusho.app.ImageLibraryLoader
import com.github.am4dr.rokusho.app.Rokusho
import com.github.am4dr.rokusho.app.SaveDataStoreProvider
import com.github.am4dr.rokusho.app.datastore.yaml.YamlSaveDataStore
import com.github.am4dr.rokusho.app.library.fs.FileSystemLibraryLoader
import com.github.am4dr.rokusho.app.library.fs.LibraryRootDetector
import com.github.am4dr.rokusho.dev.gui.RokushoViewer
import com.github.am4dr.rokusho.gui.GUIModel
import com.github.am4dr.rokusho.gui.PathChooser
import com.github.am4dr.rokusho.gui.RokushoLibraryCollection
import com.github.am4dr.rokusho.gui.old.sidemenu.SimpleSideMenu
import com.github.am4dr.rokusho.gui.scene.MainPane
import com.github.am4dr.rokusho.gui.viewer.LibraryViewerRepositoryImpl
import com.github.am4dr.rokusho.gui.viewer.ListRecordsViewerFactory
import com.github.am4dr.rokusho.gui.viewer.ThumbnailRecordsViewerFactory
import javafx.application.Application
import javafx.beans.InvalidationListener
import javafx.scene.Parent
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

    companion object {
        @JvmStatic fun main(args: Array<String>) = Application.launch(Launcher::class.java, *args)
        private val log = LoggerFactory.getLogger(Launcher::class.java)
    }

    private lateinit var rokusho: Rokusho

    private fun loadImageLibrary(path: Path) { rokusho.loadAndAddLibrary(ImageLibraryLoader::class, path) }

    override fun init() {
        log.info("launched with the params: ${parameters.raw}")
        val libraryLoaders = listOf(ImageLibraryLoader(createFileSystemLibraryLoader()))
        rokusho = Rokusho(libraryLoaders)
        parseArgs(parameters.raw.toTypedArray()).args
                .map { Paths.get(it) }
                .filter { Files.isDirectory(it) }
                .forEach { loadImageLibrary(it) }
    }

    private fun parseArgs(args: Array<String>): CommandLine = DefaultParser().parse(Options(), args)

    override fun start(stage: Stage) {
        val model = createGUIModel(rokusho, stage)
        val pane = createMainPane(model)
        stage.run {
            title = "Rokusho"
            scene = Scene(pane, 800.0, 500.0)
            show()
        }
        RokushoViewer(rokusho).also {
            it.stage.apply {
                x = stage.x - RokushoViewer.initialWidth - 2.0
                y = stage.y
            }
        }.show()
    }
}

private fun createGUIModel(rokusho: Rokusho, stage: Stage): GUIModel {
    val recordsViewerFactories = listOf(ListRecordsViewerFactory(), ThumbnailRecordsViewerFactory())
    val viewerFactory = LibraryViewerRepositoryImpl.RecordsViewersLibraryViewerFactory(recordsViewerFactories)
    val libraryViewerRepository = LibraryViewerRepositoryImpl(viewerFactory)
    val libraryCollection = RokushoLibraryCollection(rokusho, PathChooser(stage))
    return GUIModel(libraryCollection, libraryViewerRepository)
}

private fun createMainPane(model: GUIModel): Parent {
    val simpleSideMenu = SimpleSideMenu(model.libraryCollection::addLibraryViaGUI).apply {
        width.value = 40.0
        setIcons(model.libraryIcons)
        model.libraryIcons.addListener(InvalidationListener {
            setIcons(model.libraryIcons)
        })
    }
    return MainPane().apply {
        sideMenu.set(simpleSideMenu)
        addLibraryEventHandler.set(model.libraryCollection::addLibraryViaGUI)
        libraryViewer.bind(model.currentLibraryViewer)
        showAddLibrarySuggestion.bind(model.libraryCollection.selectedProperty().isNull)
    }
}

private fun createFileSystemLibraryLoader(): FileSystemLibraryLoader {
    val saveFileName = "rokusho.yaml"
    val libraryRootDetector: LibraryRootDetector = { path -> Files.isRegularFile(path.resolve(saveFileName)) }
    val saveDataStoreProvider = SaveDataStoreProvider { YamlSaveDataStore(it.resolve(saveFileName)) }
    return FileSystemLibraryLoader(libraryRootDetector, saveDataStoreProvider::getOrCreate)
}
