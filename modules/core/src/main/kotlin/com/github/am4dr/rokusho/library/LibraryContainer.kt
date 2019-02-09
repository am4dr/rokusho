package com.github.am4dr.rokusho.library

import com.github.am4dr.rokusho.library.internal.DataLocker
import com.github.am4dr.rokusho.util.event.EventPublisher
import com.github.am4dr.rokusho.util.event.EventPublisherSupport
import com.github.am4dr.rokusho.util.event.EventSubscription
import kotlinx.coroutines.ExperimentalCoroutinesApi
import java.nio.file.Path
import kotlin.coroutines.CoroutineContext

/**
 * アプリケーションによって現在読み込まれているライブラリを状態として持つ
 *
 * ライブラリを新しく読み込む際に既存のものとの関係を考慮する必要がある場合、それに責任を持つ
 */
class LibraryContainer private constructor(
    private val pathLibraryLoader: (Path) -> LoadedLibrary?,
    eventPublisherSupport: EventPublisherSupport<Event>
) : EventPublisher<LibraryContainer.Event> by eventPublisherSupport {

    constructor(
        pathLibraryLoader: (Path) -> LoadedLibrary?,
        eventPublisherContext: CoroutineContext
    ) : this(pathLibraryLoader, EventPublisherSupport(eventPublisherContext))

    private val libraries = DataLocker(LibrarySet(eventPublisherSupport))


    fun getAllLibraries(): Set<LoadedLibrary> = libraries.read {
        it.asSet()
    }

    @ExperimentalCoroutinesApi
    fun loadPathLibrary(path: Path): LoadedLibrary? =
        pathLibraryLoader(path)?.also(::addLibrary)

    @ExperimentalCoroutinesApi
    private fun addLibrary(library: LoadedLibrary) = libraries.write {
        it.add(library)
        activateAutoSave(library)
    }

    // 変更イベントのたびに保存するようにしているが、保存頻度が高いかもしれない
    // ライブラリーへの初期ロード時にこれが誘発しないようにしなければならない
    @ExperimentalCoroutinesApi
    private fun activateAutoSave(library: LoadedLibrary) {
        val getCurrentLibraryInstance = { libraries.read { it.get(library) } }
        library.library.subscribe(getCurrentLibraryInstance) { event, currentLibraryInstance ->
            libraries.read {
                when (event) {
                    is Library.Event.TagEvent.Added,
                    is Library.Event.TagEvent.Removed,
                    is Library.Event.TagEvent.Updated,
                    is Library.Event.ItemEvent.Added,
                    is Library.Event.ItemEvent.Removed,
                    is Library.Event.ItemEvent.Updated -> {
                        currentLibraryInstance.saveToDefault()
                    }
                    is Library.Event.TagEvent.Loaded,
                    is Library.Event.ItemEvent.Loaded -> {
                        /* もともとストアに存在するはずのものを読み込んだに過ぎないので保存しない */
                    }
                }.let { /* eventの網羅性をコンパイラにチェックさせるために必要 */ }
            }
        }
    }


    fun getDataAndSubscribe(
        block: EventPublisher<Event>.(Set<LoadedLibrary>) -> EventSubscription
    ): EventSubscription = libraries.read {
        block(this, getAllLibraries())
    }


    sealed class Event(val library: LoadedLibrary) {
        class Added(library: LoadedLibrary) : Event(library)
        class Removed(library: LoadedLibrary) : Event(library)
        class Updated(library: LoadedLibrary) : Event(library)
    }


    internal class LibrarySet(
        private val eventPublisherSupport: EventPublisherSupport<in Event>
    ) {
        private val libraries = mutableSetOf<LoadedLibrary>()

        fun asSet(): Set<LoadedLibrary> = libraries.toSet()
        fun get(library: LoadedLibrary): LoadedLibrary? =
            libraries.find { it.isSameEntity(library) }

        @ExperimentalCoroutinesApi
        fun add(library: LoadedLibrary) {
            libraries.putOrReplaceEntity(library)
            eventPublisherSupport.dispatch(Event.Added(library))
        }

        @ExperimentalCoroutinesApi
        fun update(library: LoadedLibrary) {
            libraries.putOrReplaceEntity(library)
            eventPublisherSupport.dispatch(Event.Updated(library))
        }
    }
}