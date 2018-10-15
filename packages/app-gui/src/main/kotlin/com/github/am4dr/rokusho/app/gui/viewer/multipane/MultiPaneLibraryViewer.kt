package com.github.am4dr.rokusho.app.gui.viewer.multipane

import com.github.am4dr.rokusho.app.gui.LibraryViewer
import com.github.am4dr.rokusho.gui.scene.ViewSelectorPaneWithSearchBox
import com.github.am4dr.rokusho.javafx.function.bindLeft
import com.github.am4dr.rokusho.old.core.library.Record
import javafx.beans.binding.Bindings
import javafx.beans.property.SimpleListProperty
import javafx.collections.FXCollections
import javafx.collections.ObservableList
import javafx.collections.transformation.FilteredList
import javafx.scene.Node
import java.util.function.Predicate

class MultiPaneLibraryViewer<T : Any>(panes: List<Pane<T>>) : LibraryViewer<T> {

    private val view = ViewSelectorPaneWithSearchBox()
    override val node: Node get() = view
    override val records: ObservableList<Record<T>> = FXCollections.observableArrayList()

    private val filteredList = FilteredList(records)
    private val filteredRecords = SimpleListProperty(filteredList)

    init {
        view.apply {
            totalCountProperty().bind(Bindings.size(records))
            filterPassedCountProperty().bind(Bindings.size(filteredRecords))
        }
        val byTagNameRecordFilterFactory = { input: String? ->
            Predicate { item: Record<*> ->
                if (input == null || input == "") true
                else item.itemTags.any { it.tag.id.contains(input) }
            }
        }
        val recordFilter = byTagNameRecordFilterFactory.bindLeft(view.filterTextProperty())
        filteredList.predicateProperty().bind(recordFilter)
        panes.forEach {
            Bindings.bindContent(it.records, filteredRecords)
            view.selections.add(ViewSelectorPaneWithSearchBox.Selection(it.label, it.viewer))
        }
    }

    class Pane<T>(val label: String, val viewer: Node, val records: ObservableList<Record<T>> = FXCollections.observableArrayList())
}