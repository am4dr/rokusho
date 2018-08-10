package com.github.am4dr.rokusho.gui.viewer

import com.github.am4dr.rokusho.app.library.RokushoLibrary
import com.github.am4dr.rokusho.core.library.Record
import com.github.am4dr.rokusho.gui.LibraryViewerRepository
import com.github.am4dr.rokusho.javafx.function.bindLeft
import javafx.beans.binding.Bindings
import javafx.beans.property.SimpleListProperty
import javafx.collections.transformation.FilteredList
import javafx.scene.Node
import java.lang.ref.SoftReference
import java.util.*
import java.util.function.Predicate
import kotlin.reflect.KClass

class LibraryViewerRepositoryImpl(private val recordsViewerFactories: List<RecordsViewerFactory>) : LibraryViewerRepository {

    private val libraryViewCache = WeakHashMap(mutableMapOf<RokushoLibrary<*>, SoftReference<Node>>())

    override fun get(library: RokushoLibrary<*>): Node = libraryViewCache[library]?.get() ?: createLibraryViewAndCache(library)

    private fun createLibraryViewAndCache(library: RokushoLibrary<*>): Node = createLibraryViewer(library).also { libraryViewCache[library] = SoftReference(it) }

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

    private val byTagNameRecordFilterFactory = { input: String? ->
        Predicate { item: Record<*> ->
            if (input == null || input == "") true
            else item.itemTags.any { it.tag.id.contains(input) }
        }
    }
}