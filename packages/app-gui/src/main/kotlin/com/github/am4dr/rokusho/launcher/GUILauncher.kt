package com.github.am4dr.rokusho.launcher

import com.github.am4dr.rokusho.app.ImageLibraryLoader
import com.github.am4dr.rokusho.app.Rokusho
import com.github.am4dr.rokusho.app.SaveDataStoreProvider
import com.github.am4dr.rokusho.app.gui.GUIPopupPathChooser
import com.github.am4dr.rokusho.app.gui.LibraryCollection
import com.github.am4dr.rokusho.app.gui.LibraryViewerCollection
import com.github.am4dr.rokusho.app.gui.RokushoLibraryCollection
import com.github.am4dr.rokusho.app.gui.dev.RokushoViewer
import com.github.am4dr.rokusho.app.gui.viewer.multipane.MultiPaneLibraryViewerFactory
import com.github.am4dr.rokusho.app.gui.viewer.multipane.pane.ListPaneFactory
import com.github.am4dr.rokusho.app.gui.viewer.multipane.pane.ThumbnailPaneFactory
import com.github.am4dr.rokusho.app.library.RokushoLibrary
import com.github.am4dr.rokusho.app.library.fs.FileSystemLibraryLoader
import com.github.am4dr.rokusho.app.library.fs.LibraryRootDetector
import com.github.am4dr.rokusho.gui.scene.MainPane
import com.github.am4dr.rokusho.gui.sidemenu.CharacterIcon
import com.github.am4dr.rokusho.gui.sidemenu.SideMenuIcon
import com.github.am4dr.rokusho.gui.sidemenu.SimpleSideMenu
import com.github.am4dr.rokusho.javafx.collection.TransformedList
import com.github.am4dr.rokusho.old.datastore.file.yaml.YamlSaveDataStore
import javafx.application.Application
import javafx.beans.binding.Bindings
import javafx.beans.binding.When
import javafx.collections.ObservableList
import javafx.geometry.Insets
import javafx.scene.Scene
import javafx.scene.control.Tooltip
import javafx.scene.layout.Background
import javafx.scene.layout.BackgroundFill
import javafx.scene.layout.CornerRadii
import javafx.scene.paint.Color
import javafx.stage.Stage
import org.apache.commons.cli.CommandLine
import org.apache.commons.cli.DefaultParser
import org.apache.commons.cli.Options
import org.slf4j.LoggerFactory
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths

class GUILauncher : Application() {

    companion object {
        @JvmStatic
        fun main(args: Array<String>) = Application.launch(GUILauncher::class.java, *args)

        private val log = LoggerFactory.getLogger(GUILauncher::class.java)
    }

    private lateinit var rokusho: Rokusho

    private fun loadImageLibrary(path: Path) {
        rokusho.loadAndAddLibrary(ImageLibraryLoader::class, path)
    }

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
        val libraryCollection = RokushoLibraryCollection(rokusho, GUIPopupPathChooser(stage))
        val libraryIcons = createIcons(libraryCollection, ::createSideMenuIcon)
        val viewerFactory = MultiPaneLibraryViewerFactory(listOf(ListPaneFactory(), ThumbnailPaneFactory()))
        val viewerCollection = LibraryViewerCollection(libraryCollection, viewerFactory)

        val sideMenu = SimpleSideMenu().apply {
            width.value = 40.0
            onAddClicked.set(libraryCollection::addLibraryViaGUI)
            Bindings.bindContent(icons, libraryIcons)
        }
        val pane = MainPane().apply {
            this.sideMenu.set(sideMenu)
            addLibraryEventHandler.set(libraryCollection::addLibraryViaGUI)
            libraryViewer.bind(viewerCollection.currentLibraryViewer)
            showAddLibrarySuggestion.bind(libraryCollection.selectedProperty().isNull)
        }

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

private fun createSideMenuIcon(library: RokushoLibrary<*>): SideMenuIcon =
        CharacterIcon().apply {
            Tooltip.install(this, Tooltip(library.name))
            backgroundProperty().bind(When(selectedProperty)
                    .then(Background(BackgroundFill(Color.INDIANRED, CornerRadii(4.0), Insets.EMPTY)))
                    .otherwise(Background(BackgroundFill(Color.ANTIQUEWHITE, CornerRadii(4.0), Insets.EMPTY))))
            character.set(library.shortName.first().toString())
        }

private fun createIcons(libraryCollection: LibraryCollection,
                        iconFactory: (RokushoLibrary<*>) -> SideMenuIcon): ObservableList<SideMenuIcon> =
        TransformedList(libraryCollection.libraries) { library ->
            val libraryIsSelected = libraryCollection.selectedProperty().isEqualTo(library)
            iconFactory(library).apply {
                setOnMouseClicked {
                    libraryCollection.select(library)
                }
                selectedProperty.bind(libraryIsSelected)
            }
        }

private fun createFileSystemLibraryLoader(): FileSystemLibraryLoader {
    val saveFileName = "rokusho.yaml"
    val libraryRootDetector: LibraryRootDetector = { path -> Files.isRegularFile(path.resolve(saveFileName)) }
    val saveDataStoreProvider = SaveDataStoreProvider { YamlSaveDataStore(it.resolve(saveFileName)) }
    return FileSystemLibraryLoader(libraryRootDetector, saveDataStoreProvider::getOrCreate)
}
