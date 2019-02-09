package com.github.am4dr.rokusho.presenter.viewer.multipane

import com.github.am4dr.rokusho.javafx.collection.TransformedList
import com.github.am4dr.rokusho.presenter.ItemListViewer
import com.github.am4dr.rokusho.presenter.ItemViewModel
import com.github.am4dr.rokusho.presenter.scene.ViewSelectorPaneWithSearchBox
import javafx.beans.binding.Binding
import javafx.beans.binding.Bindings
import javafx.beans.binding.Bindings.bindContent
import javafx.beans.binding.Bindings.createObjectBinding
import javafx.beans.value.ObservableValue
import javafx.collections.FXCollections
import javafx.collections.FXCollections.observableList
import javafx.collections.ObservableList
import javafx.collections.transformation.FilteredList
import javafx.scene.Node
import java.util.concurrent.Callable
import java.util.function.Predicate

class MultiPaneViewer(
    panes: List<Pane>,
    view: ViewSelectorPaneWithSearchBox = ViewSelectorPaneWithSearchBox()
) : ItemListViewer {

    override val node: Node = view
    override val items: ObservableList<ItemViewModel<*>> = FXCollections.observableArrayList()

    private val filteredList = FilteredList(items)
    private val filteredListForPane: MutableMap<Pane, ObservableList<ItemViewModel<*>>> = mutableMapOf()
    private fun getFilteredRecords(pane: Pane): ObservableList<ItemViewModel<*>> {
        return filteredListForPane.getOrPut(pane) {
            if (pane.filter != null) {
                FilteredList(filteredList, pane.filter)
            }
            else { filteredList }
        }
    }
    private val selections = TransformedList(observableList(panes)) {
        bindContent(it.records, getFilteredRecords(it))
        ViewSelectorPaneWithSearchBox.Selection(it.label, it.viewer)
    }

    init {
        bindWith(view)
    }

    private fun bindWith(view: ViewSelectorPaneWithSearchBox) {
        filteredList.predicateProperty().bind(createFilterPredicateBinding(view.filterTextProperty()))
        view.totalCountProperty().bind(Bindings.size(items))
        view.filterPassedCountProperty().bind(Bindings.size(filteredList))
        // FIXME ひとつづつ追加するほかに最初のPaneを選択した状態にするすべがない
        selections.forEach { view.selections.add(it) }
    }

    private fun createFilterPredicateBinding(string: ObservableValue<String>): Binding<Predicate<ItemViewModel<*>>> {
        val filterPredicate = Predicate { item: ItemViewModel<*> ->
            val input = string.value
            if (input == null || input == "") true
            else item.tags.any { it.name.contains(input) }
        }
        return createObjectBinding(Callable { filterPredicate }, string)
    }

    class Pane(
        val label: String,
        val viewer: Node,
        val records: ObservableList<ItemViewModel<*>> = FXCollections.observableArrayList(),
        val filter: ((ItemViewModel<*>)->Boolean)? = null
    )
}