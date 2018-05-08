package com.github.am4dr.rokusho.app

import com.github.am4dr.rokusho.app.library.RokushoLibrary
import javafx.beans.property.ReadOnlyListProperty
import javafx.beans.property.ReadOnlyListWrapper
import javafx.collections.FXCollections
import java.nio.file.Files
import java.nio.file.Path

class Rokusho(val loaders: List<LibraryLoader<*, *>>) {

    companion object {
        private val imageFileNameMatcher = Regex(".*\\.(bmp|gif|jpe?g|png)$", RegexOption.IGNORE_CASE)
        fun isSupportedImageFile(path: Path) = Files.isRegularFile(path) && imageFileNameMatcher.matches(path.fileName.toString())
    }

    private val _libraries = ReadOnlyListWrapper(FXCollections.observableArrayList<RokushoLibrary<*>>())
    val libraries: ReadOnlyListProperty<RokushoLibrary<*>> = _libraries.readOnlyProperty

    fun addLibrary(library: RokushoLibrary<*>) {
        if (!_libraries.contains(library)) {
            _libraries.add(library)
        }
    }

    fun getLibraryLoader(name: String): LibraryLoader<*, *>? = loaders.find { it.name == name || it::class.java.name == name }

    fun loadAndAddLibrary(name: String, specifier: String): RokushoLibrary<*>? =
            try {
                getLibraryLoader(name)?.load(specifier)?.also(::addLibrary)
            } catch (t: Throwable) {
                t.printStackTrace()
                null
            }

    inline fun <reified T : LibraryLoader<*, *>> getLibraryLoader(): T? = loaders.find { it is T }?.let { it as T }

    inline fun <S, T : Any, reified L : LibraryLoader<S, T>> loadAndAddLibrary(specifier: S): RokushoLibrary<T>? =
            try {
                getLibraryLoader<L>()?.load(specifier)?.also(::addLibrary)
            } catch (t: Throwable) {
                t.printStackTrace()
                null
            }
}