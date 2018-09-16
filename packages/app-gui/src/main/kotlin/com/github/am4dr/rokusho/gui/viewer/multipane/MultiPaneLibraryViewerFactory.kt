package com.github.am4dr.rokusho.gui.viewer.multipane

import com.github.am4dr.rokusho.app.library.RokushoLibrary
import com.github.am4dr.rokusho.core.library.Record
import com.github.am4dr.rokusho.gui.LibraryViewer
import com.github.am4dr.rokusho.gui.LibraryViewerFactory
import com.github.am4dr.rokusho.gui.scene.ViewSelectorPaneWithSearchBox
import com.github.am4dr.rokusho.javafx.function.bindLeft
import javafx.beans.binding.Bindings
import javafx.beans.binding.Bindings.bindContent
import javafx.beans.property.SimpleListProperty
import javafx.collections.FXCollections
import javafx.collections.ObservableList
import javafx.collections.transformation.FilteredList
import javafx.scene.Node
import javafx.scene.layout.StackPane
import java.util.function.Predicate

class MultiPaneLibraryViewerFactory(private val recordsViewerFactories: List<RecordsViewerFactory>) : LibraryViewerFactory {

    override fun <T : Any> create(library: RokushoLibrary<T>): LibraryViewer<T> = Viewer(library)

    private inner class Viewer<T : Any>(library: RokushoLibrary<T>) : StackPane(), LibraryViewer<T> {

        override val node: Node get() = this
        override val records: ObservableList<Record<T>> = FXCollections.observableArrayList()

        val filteredList = FilteredList(records)
        val filteredRecords = SimpleListProperty(filteredList)
        init {
            val view = ViewSelectorPaneWithSearchBox().apply {
                totalCountProperty().bind(Bindings.size(records))
                filterPassedCountProperty().bind(Bindings.size(filteredRecords))
            }
            val recordFilter = byTagNameRecordFilterFactory.bindLeft(view.filterTextProperty())
            filteredList.predicateProperty().bind(recordFilter)
            recordsViewerFactories
                    .filter { it.isAcceptable(library.type) }
                    .mapNotNull { it.create(library) }
                    .forEach {
                        bindContent(it.records, filteredRecords)
                        view.selections.add(ViewSelectorPaneWithSearchBox.Selection(it.label, it.viewer))
                    }
            children.add(view)
        }
    }
    private val byTagNameRecordFilterFactory = { input: String? ->
        Predicate { item: Record<*> ->
            if (input == null || input == "") true
            else item.itemTags.any { it.tag.id.contains(input) }
        }
    }
}