package com.github.am4dr.rokusho.launcher

import com.github.am4dr.rokusho.adapter.DataStoreConverter
import com.github.am4dr.rokusho.app.FileBasedMetaDataRepositories
import com.github.am4dr.rokusho.app.FileSystemBasedLibraryProvider
import com.github.am4dr.rokusho.app.LibraryCollection
import com.github.am4dr.rokusho.core.datastore.savedata.yaml.YamlSaveDataStore
import com.github.am4dr.rokusho.javafx.control.DirectoryPathChooser
import com.github.am4dr.rokusho.library.Library
import com.github.am4dr.rokusho.presenter.Presenter
import com.github.am4dr.rokusho.presenter.dev.RokushoViewer
import com.github.am4dr.rokusho.presenter.scene.module.MainPaneModule
import com.github.am4dr.rokusho.presenter.scene.module.SideMenuModule
import com.github.am4dr.rokusho.presenter.viewer.multipane.MultiPaneViewerFactory
import com.github.am4dr.rokusho.presenter.viewer.multipane.pane.ListPaneFactory
import com.github.am4dr.rokusho.presenter.viewer.multipane.pane.thumbnail.ImageThumbnailFactory
import com.github.am4dr.rokusho.presenter.viewer.multipane.pane.thumbnail.ThumbnailPaneFactory
import com.github.am4dr.rokusho.presenter.viewer.multipane.pane.thumbnail.UrlImageLoader
import com.github.am4dr.rokusho.util.event.EventPublisherSupport
import javafx.application.Application
import javafx.application.Platform.runLater
import javafx.collections.FXCollections
import javafx.collections.ObservableList
import javafx.scene.Scene
import javafx.stage.Stage
import kotlinx.coroutines.Dispatchers
import org.apache.commons.cli.CommandLine
import org.apache.commons.cli.DefaultParser
import org.apache.commons.cli.Options
import org.slf4j.LoggerFactory
import java.nio.file.Files
import java.nio.file.Paths

class RokushoGUIApp : Application() {

    companion object {
        private val log = LoggerFactory.getLogger(GUILauncher::class.java)
    }


    private lateinit var libraryCollection: LibraryCollection
    private val libraries: ObservableList<Library<*>> = FXCollections.observableArrayList()
    private lateinit var presenter: Presenter

    private val eventDispatcherContext = Dispatchers.Default

    override fun init() {
        log.info("launched with the params: ${parameters.raw}")

        val fileBasedMetaDataRepositories = FileBasedMetaDataRepositories { savefile ->
            DataStoreConverter(YamlSaveDataStore(savefile))
        }
        libraryCollection = LibraryCollection(
            listOf(FileSystemBasedLibraryProvider(fileBasedMetaDataRepositories, eventDispatcherContext)),
            EventPublisherSupport(eventDispatcherContext)
        )
        parseArgs(parameters.raw.toTypedArray()).args
            .map { Paths.get(it) }
            .filter { Files.isDirectory(it) }
            .forEach { libraryCollection.loadPathLibrary(it) }
    }

    private fun parseArgs(args: Array<String>): CommandLine = DefaultParser().parse(Options(), args)

    override fun start(stage: Stage) {
        libraryCollection.subscribeFor(libraries) { event, libs ->
            runLater {
                when (event) {
                    is LibraryCollection.Event.AddLibrary -> libs.add(event.library)
                    is LibraryCollection.Event.RemoveLibrary -> libs.remove(event.library)
                    else -> {}
                }
            }
        }

        val thumbnailMaxWidth = 500.0
        val thumbnailMaxHeight = 200.0

        val imageLoader = UrlImageLoader()
        val viewerFactory = MultiPaneViewerFactory(
            listOf(
                ListPaneFactory(),
                ThumbnailPaneFactory(listOf(ImageThumbnailFactory(imageLoader, thumbnailMaxWidth, thumbnailMaxHeight)))
            )
        )
        val pathChooser = DirectoryPathChooser(stage)
        presenter = Presenter(libraries, viewerFactory, libraryCollection::loadPathLibrary, pathChooser::get)

        val sideMenu = SideMenuModule(presenter)
        val libraryViewerContainer = MainPaneModule(presenter, sideMenu)

        stage.run {
            title = "Rokusho"
            scene = Scene(libraryViewerContainer.node, 800.0, 500.0)
            show()
        }
        RokushoViewer(presenter.libraries).also {
            it.stage.apply {
                x = stage.x - RokushoViewer.initialWidth - 2.0
                y = stage.y
            }
        }.show()
    }
}