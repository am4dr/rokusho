package com.github.am4dr.rokusho.app

import com.github.am4dr.rokusho.core.library.Library
import javafx.beans.property.ReadOnlyListProperty
import javafx.beans.property.ReadOnlyListWrapper
import javafx.collections.FXCollections
import java.nio.file.Files
import java.nio.file.Path

class Rokusho {

    companion object {
        private val imageFileNameMatcher = Regex(".*\\.(bmp|gif|jpe?g|png)$", RegexOption.IGNORE_CASE)
        fun isSupportedImageFile(path: Path) = Files.isRegularFile(path) && imageFileNameMatcher.matches(path.fileName.toString())
    }

    private val _libraries = ReadOnlyListWrapper(FXCollections.observableArrayList<Library<ImageUrl>>())
    val libraries: ReadOnlyListProperty<Library<ImageUrl>> = _libraries.readOnlyProperty

    fun addLibrary(library: Library<ImageUrl>) {
        if (!_libraries.contains(library)) {
            _libraries.add(library)
        }
    }
}