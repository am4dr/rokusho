package com.github.am4dr.rokusho.app

import com.github.am4dr.rokusho.core.library.Library
import com.github.am4dr.rokusho.core.library.provider.LibraryProvider
import com.github.am4dr.rokusho.core.library.provider.LibraryProviderCollection
import javafx.beans.property.ReadOnlyListProperty
import javafx.beans.property.SimpleListProperty
import javafx.collections.FXCollections
import javafx.collections.ObservableList
import java.nio.file.Path

/**
 * Libraryの読み込みと、読み込んだLibraryの把握を行う
 */
class LibraryCollection(libraryProviders: Collection<LibraryProvider<*>>) {

    private val libraryProvider = LibraryProviderCollection(libraryProviders.toSet())

    private val libraries: ObservableList<Library<*>> = FXCollections.observableArrayList()
    fun getLibraries(): ReadOnlyListProperty<Library<*>> = SimpleListProperty<Library<*>>(FXCollections.observableArrayList()).apply { bindContent(libraries) }

    /**
     * path以下のPathを再帰的に集めたLibrary<Path>を読み込む
     */
    fun loadPathLibrary(path: Path): Library<Path>? {
        val descriptor = FileSystemBasedLibraryProvider.createDescriptor(path.toUri())
        val library = libraryProvider.get(descriptor)?.takeIf { it.type == Path::class } ?: return null

        @Suppress("UNCHECKED_CAST")
        library as Library<Path>
        libraries.add(library)
        return library
    }
}