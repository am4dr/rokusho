package com.github.am4dr.rokusho.launcher

import com.github.am4dr.rokusho.adapter.DataStoreConverter
import com.github.am4dr.rokusho.app.FileBasedMetaDataRepositories
import com.github.am4dr.rokusho.app.FileSystemBasedLibraryProvider
import com.github.am4dr.rokusho.app.LibraryCollection
import com.github.am4dr.rokusho.app.gui.GUIPopupPathChooser
import com.github.am4dr.rokusho.app.gui.LibrarySelector
import com.github.am4dr.rokusho.app.gui.LibrarySelectorImpl
import com.github.am4dr.rokusho.app.gui.LibraryViewerCollection
import com.github.am4dr.rokusho.app.gui.dev.RokushoViewer
import com.github.am4dr.rokusho.app.gui.viewer.multipane.MultiPaneLibraryViewerFactory
import com.github.am4dr.rokusho.app.gui.viewer.multipane.pane.ListPaneFactory
import com.github.am4dr.rokusho.app.gui.viewer.multipane.pane.ThumbnailPaneFactory
import com.github.am4dr.rokusho.core.datastore.savedata.yaml.YamlSaveDataStore
import com.github.am4dr.rokusho.core.library.Library
import com.github.am4dr.rokusho.gui.scene.MainPane
import com.github.am4dr.rokusho.gui.sidemenu.CharacterIcon
import com.github.am4dr.rokusho.gui.sidemenu.SideMenuIcon
import com.github.am4dr.rokusho.gui.sidemenu.SimpleSideMenu
import com.github.am4dr.rokusho.javafx.collection.TransformedList
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
import java.nio.file.Paths

class GUILauncher : Application() {

    companion object {
        @JvmStatic
        fun main(args: Array<String>) = Application.launch(GUILauncher::class.java, *args)

        private val log = LoggerFactory.getLogger(GUILauncher::class.java)
    }

    private lateinit var libraryCollection: LibraryCollection
    private lateinit var libraries: ObservableList<Library<*>>

    override fun init() {
        log.info("launched with the params: ${parameters.raw}")

        val fileBasedMetaDataRepositories = FileBasedMetaDataRepositories { savefile ->
            DataStoreConverter(YamlSaveDataStore(savefile))
        }
        libraryCollection = LibraryCollection(listOf(FileSystemBasedLibraryProvider(fileBasedMetaDataRepositories)))
        parseArgs(parameters.raw.toTypedArray()).args
                .map { Paths.get(it) }
                .filter { Files.isDirectory(it) }
                .forEach { libraryCollection.loadPathLibrary(it) }
        libraries = libraryCollection.getLibraries()
    }

    private fun parseArgs(args: Array<String>): CommandLine = DefaultParser().parse(Options(), args)

    override fun start(stage: Stage) {

        val librarySelector = LibrarySelectorImpl()
        Bindings.bindContent(librarySelector.libraries, libraries)
        val libraryIcons = createIcons(librarySelector, ::createSideMenuIcon)
        val viewerFactory = MultiPaneLibraryViewerFactory(listOf(ListPaneFactory(), ThumbnailPaneFactory()))
        val viewerCollection = LibraryViewerCollection(librarySelector, viewerFactory)

        val pathChooser = GUIPopupPathChooser(stage)
        fun addPathLibraryViaGUI() = pathChooser.get()?.let(libraryCollection::loadPathLibrary)

        val sideMenu = SimpleSideMenu().apply {
            width.value = 40.0
            onAddClicked.set { addPathLibraryViaGUI() }
            Bindings.bindContent(icons, libraryIcons)
        }
        val pane = MainPane().apply {
            this.sideMenu.set(sideMenu)
            addLibraryEventHandler.set { addPathLibraryViaGUI() }
            libraryViewer.bind(viewerCollection.currentLibraryViewer)
            showAddLibrarySuggestion.bind(librarySelector.selectedProperty().isNull)
        }

        stage.run {
            title = "Rokusho"
            scene = Scene(pane, 800.0, 500.0)
            show()
        }
        RokushoViewer(librarySelector.libraries).also {
            it.stage.apply {
                x = stage.x - RokushoViewer.initialWidth - 2.0
                y = stage.y
            }
        }.show()
    }
}

private fun createSideMenuIcon(library: Library<*>): SideMenuIcon =
        CharacterIcon().apply {
            Tooltip.install(this, Tooltip(library.name))
            backgroundProperty().bind(When(selectedProperty)
                    .then(Background(BackgroundFill(Color.INDIANRED, CornerRadii(4.0), Insets.EMPTY)))
                    .otherwise(Background(BackgroundFill(Color.ANTIQUEWHITE, CornerRadii(4.0), Insets.EMPTY))))
            character.set(library.shortName.first().toString())
        }

private fun createIcons(librarySelector: LibrarySelector,
                        iconFactory: (Library<*>) -> SideMenuIcon): ObservableList<SideMenuIcon> =
        TransformedList(librarySelector.libraries) { library ->
            val libraryIsSelected = librarySelector.selectedProperty().isEqualTo(library)
            iconFactory(library).apply {
                setOnMouseClicked {
                    librarySelector.select(library)
                }
                selectedProperty.bind(libraryIsSelected)
            }
        }
