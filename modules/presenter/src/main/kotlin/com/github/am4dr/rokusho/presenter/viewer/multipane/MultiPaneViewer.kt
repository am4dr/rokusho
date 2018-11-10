package com.github.am4dr.rokusho.presenter.viewer.multipane

import com.github.am4dr.rokusho.core.library.LibraryItem
import com.github.am4dr.rokusho.presenter.LibraryItemListViewer
import com.github.am4dr.rokusho.presenter.scene.ViewSelectorPaneWithSearchBox
import javafx.beans.binding.Bindings
import javafx.beans.property.SimpleListProperty
import javafx.collections.FXCollections
import javafx.collections.ObservableList
import javafx.collections.transformation.FilteredList
import javafx.scene.Node
import java.util.concurrent.Callable
import java.util.function.Predicate

class MultiPaneViewer<T : Any>(panes: List<Pane<T>>) : LibraryItemListViewer<T> {

    private val view = ViewSelectorPaneWithSearchBox()
    override val node: Node get() = view
    override val items: ObservableList<LibraryItem<out T>> = FXCollections.observableArrayList()

    private val filteredList = FilteredList(items)
    private val filteredRecords = SimpleListProperty(filteredList)
    private val filteredPaneRecords: MutableMap<Pane<T>, ObservableList<LibraryItem<out T>>> = mutableMapOf()

    init {
        val filterPredicate = Predicate { item: LibraryItem<*> ->
            val input = view.filterTextProperty().get()
            if (input == null || input == "") true
            else item.tags.any { it.base.name.name.contains(input) }
        }
        filteredList.predicateProperty()
                .bind(Bindings.createObjectBinding(Callable { filterPredicate }, view.filterTextProperty()))

        view.apply {
            totalCountProperty().bind(Bindings.size(items))
            filterPassedCountProperty().bind(Bindings.size(filteredRecords))
        }

        panes.forEach {
            filteredPaneRecords[it] =
                    if (it.filter != null) { FilteredList(filteredList, it.filter) }
                    else { filteredList }
            Bindings.bindContent(it.records, filteredPaneRecords[it])
            view.selections.add(ViewSelectorPaneWithSearchBox.Selection(it.label, it.viewer))
        }
    }

    class Pane<T : Any>(
        val label: String,
        val viewer: Node,
        val records: ObservableList<LibraryItem<out T>> = FXCollections.observableArrayList(),
        val filter: ((LibraryItem<out T>)->Boolean)? = null
    )
}