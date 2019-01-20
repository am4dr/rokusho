package com.github.am4dr.rokusho.app

import com.github.am4dr.rokusho.library.Library
import com.github.am4dr.rokusho.library.provider.LibraryProvider
import com.github.am4dr.rokusho.library.provider.LibraryProviderCollection
import com.github.am4dr.rokusho.util.event.EventPublisher
import com.github.am4dr.rokusho.util.event.EventPublisherSupport
import java.nio.file.Path
import java.util.*

/**
 * Libraryの読み込みと、読み込んだLibraryの把握を行う
 */
class LibraryCollection(
    libraryProviders: Collection<LibraryProvider<*>>,
    private val events: EventPublisherSupport<Event> = EventPublisherSupport()
) : EventPublisher<LibraryCollection.Event> by events {

    private val libraryProvider = LibraryProviderCollection(libraryProviders.toSet())
    private val libraries = Collections.synchronizedSet(mutableSetOf<Library<*>>())

    fun getLibraries(): Set<Library<*>> = libraries.toSet()

    private fun addLibrary(library: Library<*>) {
        libraries.add(library)
        events.dispatch(Event.AddLibrary(library))
    }

    /**
     * path以下のPathを再帰的に集めたLibrary<Path>を読み込む
     */
    fun loadPathLibrary(path: Path): Library<Path>? {
        val descriptor = FileSystemBasedLibraryProvider.createDescriptor(path.toUri())
        val library = libraryProvider.get(descriptor)?.takeIf { it.type == Path::class } ?: return null

        @Suppress("UNCHECKED_CAST")
        library as Library<Path>
        addLibrary(library)
        return library
    }

    sealed class Event {
        class AddLibrary(val library: Library<*>) : Event()
        class RemoveLibrary(val library: Library<*>) : Event()
    }
}