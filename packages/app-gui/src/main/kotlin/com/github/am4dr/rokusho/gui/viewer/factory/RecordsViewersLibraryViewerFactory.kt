package com.github.am4dr.rokusho.gui.viewer.factory

import com.github.am4dr.rokusho.app.library.RokushoLibrary
import com.github.am4dr.rokusho.core.library.Record
import com.github.am4dr.rokusho.gui.scene.ViewSelectorPaneWithSearchBox
import com.github.am4dr.rokusho.gui.viewer.LibraryViewerFactory
import com.github.am4dr.rokusho.javafx.function.bindLeft
import javafx.beans.binding.Bindings
import javafx.beans.binding.Bindings.bindContent
import javafx.beans.property.SimpleListProperty
import javafx.collections.transformation.FilteredList
import javafx.scene.Node
import javafx.scene.layout.StackPane
import java.util.function.Predicate
import kotlin.reflect.KClass

class RecordsViewersLibraryViewerFactory(private val recordsViewerFactories: List<RecordsViewerFactory>) : LibraryViewerFactory {

    override fun create(library: RokushoLibrary<*>): Node = createLibraryViewer(library.type, library)

    private fun <T : Any> createLibraryViewer(type: KClass<T>, library: RokushoLibrary<*>): Node {
        assert(type == library.type)

        @Suppress("UNCHECKED_CAST")
        library as RokushoLibrary<T>

        return object : StackPane() {
            val filteredList = FilteredList(library.records)
            val filteredRecords = SimpleListProperty(filteredList)
            init {
                val view = ViewSelectorPaneWithSearchBox().apply {
                    totalCountProperty().bind(Bindings.size(library.records))
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
    }
    private val byTagNameRecordFilterFactory = { input: String? ->
        Predicate { item: Record<*> ->
            if (input == null || input == "") true
            else item.itemTags.any { it.tag.id.contains(input) }
        }
    }
}