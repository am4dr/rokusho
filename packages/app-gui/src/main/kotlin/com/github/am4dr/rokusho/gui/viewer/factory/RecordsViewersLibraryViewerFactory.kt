package com.github.am4dr.rokusho.gui.viewer.factory

import com.github.am4dr.rokusho.app.library.RokushoLibrary
import com.github.am4dr.rokusho.core.library.Record
import com.github.am4dr.rokusho.gui.viewer.LibraryViewerFactory
import com.github.am4dr.rokusho.javafx.function.bindLeft
import javafx.beans.binding.Bindings
import javafx.beans.property.SimpleListProperty
import javafx.collections.transformation.FilteredList
import javafx.scene.Node
import java.util.function.Predicate
import kotlin.reflect.KClass

class RecordsViewersLibraryViewerFactory(private val recordsViewerFactories: List<RecordsViewerFactory>) : LibraryViewerFactory {

    override fun create(library: RokushoLibrary<*>): Node = createLibraryViewer(library.type, library)

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
                .filter { it.isAcceptable(library.type) }
                .mapNotNull { it.create(library) }
                .forEach { add(it) }
    }

    private val byTagNameRecordFilterFactory = { input: String? ->
        Predicate { item: Record<*> ->
            if (input == null || input == "") true
            else item.itemTags.any { it.tag.id.contains(input) }
        }
    }
}