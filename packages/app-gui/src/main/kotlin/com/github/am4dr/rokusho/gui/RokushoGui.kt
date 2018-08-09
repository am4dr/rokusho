package com.github.am4dr.rokusho.gui

import com.github.am4dr.rokusho.app.Rokusho
import com.github.am4dr.rokusho.app.library.RokushoLibrary
import com.github.am4dr.rokusho.core.library.Record
import com.github.am4dr.rokusho.gui.old.sidemenu.SideMenuIcon
import com.github.am4dr.rokusho.gui.viewer.RecordsViewerContainer
import com.github.am4dr.rokusho.javafx.function.bindLeft
import javafx.beans.binding.Bindings
import javafx.beans.binding.Bindings.createObjectBinding
import javafx.beans.binding.When
import javafx.beans.property.SimpleListProperty
import javafx.collections.transformation.FilteredList
import javafx.geometry.Insets
import javafx.scene.Node
import javafx.scene.Parent
import javafx.scene.control.Label
import javafx.scene.control.Tooltip
import javafx.scene.layout.AnchorPane
import javafx.scene.layout.Background
import javafx.scene.layout.BackgroundFill
import javafx.scene.layout.CornerRadii
import javafx.scene.paint.Color
import javafx.scene.text.Font
import javafx.stage.DirectoryChooser
import javafx.stage.Stage
import javafx.stage.Window
import java.io.File
import java.nio.file.Path
import java.util.concurrent.Callable
import java.util.function.Predicate
import kotlin.reflect.KClass

// TODO remove save button
class RokushoGui(val rokusho: Rokusho,
                 val stage: Stage,
                 val addLibraryFromPath: (Path) -> Unit,
                 val saveLibrary: (RokushoLibrary<*>) -> Unit,
                 val recordsViewerFactories: List<RecordsViewerFactory>) {

    val mainParent: Parent  = createMainScene()

    private fun createMainScene(): Parent {
        return MainView({ it.toSideMenuIcon() }, ::createLibraryViewer).apply {
            libraries.bindContent(rokusho.libraries)
            onSaveClickedProperty.set {
                currentLibrary.get()?.let(saveLibrary)
            }
            openLibrarySelectorProperty.set { selectLibraryDirectory() }
        }
    }

    private var lastSelectedDirectory: File? = null
    private fun selectLibraryDirectory(window: Window = stage) {
        DirectoryChooser().run {
            title = "画像があるディレクトリを選択してください"
            initialDirectory = lastSelectedDirectory
            showDialog(window)?.let {
                lastSelectedDirectory = it
                addLibraryFromPath(it.toPath())
            }
        }
    }

    private fun createLibraryViewer(library: RokushoLibrary<*>): Node = createLibraryViewer(library.type, library)

    private fun <T : Any> createLibraryViewer(type: KClass<T>, library: RokushoLibrary<*>): Node = RecordsViewerContainer<T>().apply {
        assert(type == library.type)
        @Suppress("UNCHECKED_CAST")
        library as RokushoLibrary<T>

        records.bind(SimpleListProperty(FilteredList(library.records).apply {
            val recordFilter = byTagNameRecordFilterFactory.bindLeft(filterProperty)
            predicateProperty().bind(recordFilter)
        }))
        totalCount.bind(Bindings.size(library.records))
        filteredCount.bind(Bindings.size(records))

        recordsViewerFactories
                .filter { it.acceptable(library.type) }
                .map { it.create(library, this) }
                .forEach { add(it) }
    }
}

private val byTagNameRecordFilterFactory = { input: String? ->
    Predicate { item: Record<*> ->
        if (input == null || input == "") true
        else item.itemTags.any { it.tag.id.contains(input) }
    }
}

// TODO move into SideMenuIcon and redesign
private fun RokushoLibrary<*>.toSideMenuIcon(): SideMenuIcon =
        SideMenuIcon().apply {
            Tooltip.install(this, Tooltip(name))
            backgroundProperty().bind(When(selectedProperty)
                    .then(Background(BackgroundFill(Color.INDIANRED, CornerRadii(4.0), Insets.EMPTY)))
                    .otherwise(Background(BackgroundFill(Color.ANTIQUEWHITE, CornerRadii(4.0), Insets.EMPTY))))
            val firstLetter = Label(this@toSideMenuIcon.shortName.first().toString()).apply {
                fontProperty().bind(createObjectBinding(Callable { Font(size.get() * 0.7) }, size))
            }
            AnchorPane.setTopAnchor(firstLetter, 0.0)
            AnchorPane.setLeftAnchor(firstLetter, 6.0)
            val libLabel = Label("Lib:")
            AnchorPane.setTopAnchor(libLabel, 0.0)
            AnchorPane.setLeftAnchor(libLabel, 0.0)
            val labels = AnchorPane(firstLetter, libLabel)
            children.add(labels)
        }
