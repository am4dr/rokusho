package com.github.am4dr.rokusho.launcher

import com.github.am4dr.rokusho.app.PathLibraryLoader
import com.github.am4dr.rokusho.javafx.control.DirectoryPathChooser
import com.github.am4dr.rokusho.library2.LibraryContainer
import com.github.am4dr.rokusho.library2.LoadedLibrary
import com.github.am4dr.rokusho.library2.addOrReplaceEntity
import com.github.am4dr.rokusho.presenter.Presenter
import com.github.am4dr.rokusho.presenter.dev.RokushoViewer
import com.github.am4dr.rokusho.presenter.scene.module.MainPaneModule
import com.github.am4dr.rokusho.presenter.scene.module.SideMenuModule
import com.github.am4dr.rokusho.presenter.viewer.multipane.MultiPaneViewerFactory
import com.github.am4dr.rokusho.presenter.viewer.multipane.pane.ListPaneFactory
import com.github.am4dr.rokusho.presenter.viewer.multipane.pane.thumbnail.ImageThumbnailFactory
import com.github.am4dr.rokusho.presenter.viewer.multipane.pane.thumbnail.ThumbnailPaneFactory
import com.github.am4dr.rokusho.presenter.viewer.multipane.pane.thumbnail.UrlImageLoader
import javafx.application.Application
import javafx.application.Platform.runLater
import javafx.collections.FXCollections
import javafx.collections.ObservableList
import javafx.scene.Scene
import javafx.stage.Stage
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import org.apache.commons.cli.CommandLine
import org.apache.commons.cli.DefaultParser
import org.apache.commons.cli.Options
import org.slf4j.LoggerFactory
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths

class RokushoGUIApp : Application() {

    companion object {
        private val log = LoggerFactory.getLogger(GUILauncher::class.java)
    }


    private lateinit var libraryContainer: LibraryContainer
    private val libraries2: ObservableList<LoadedLibrary> = FXCollections.observableArrayList()
    private lateinit var presenter: Presenter

    private val eventDispatcherContext = Dispatchers.Default

    @ExperimentalCoroutinesApi
    override fun init() {
        log.info("launched with the params: ${parameters.raw}")

        // TODO 基本のローダーを初期化してセットする
        val pathLibraryLoader: (Path) -> LoadedLibrary? = PathLibraryLoader(
            eventDispatcherContext
        )::getOrCreate
        libraryContainer = LibraryContainer(pathLibraryLoader, eventDispatcherContext)
        parseArgs(parameters.raw.toTypedArray()).args
            .map { Paths.get(it) }
            .filter { Files.isDirectory(it) }
            .forEach { libraryContainer.loadPathLibrary(it) }
    }

    private fun parseArgs(args: Array<String>): CommandLine = DefaultParser().parse(Options(), args)

    @ExperimentalCoroutinesApi
    override fun start(stage: Stage) {
        libraryContainer.getDataAndSubscribe { loadedLibraries ->
            libraries2.addAll(loadedLibraries)
            subscribeFor(libraries2) { event, libs ->
                runLater {
                    when (event) {
                        is LibraryContainer.Event.Added -> libs.add(event.library)
                        is LibraryContainer.Event.Removed -> libs.remove(event.library)
                        is LibraryContainer.Event.Updated -> libs.addOrReplaceEntity(event.library)
                    }.let { /* 網羅性チェック */ }
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
        presenter = Presenter(libraries2, viewerFactory, libraryContainer::loadPathLibrary, pathChooser::get)

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