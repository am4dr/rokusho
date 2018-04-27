package com.github.am4dr.rokusho.app

import com.github.am4dr.rokusho.app.library.RokushoLibrary
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

    private val _libraries = ReadOnlyListWrapper(FXCollections.observableArrayList<RokushoLibrary<ImageUrl>>())
    val libraries: ReadOnlyListProperty<RokushoLibrary<ImageUrl>> = _libraries.readOnlyProperty

    fun addLibrary(library: RokushoLibrary<ImageUrl>) {
        if (!_libraries.contains(library)) {
            _libraries.add(library)
        }
    }
}