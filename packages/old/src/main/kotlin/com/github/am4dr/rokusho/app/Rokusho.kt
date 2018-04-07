package com.github.am4dr.rokusho.app

import com.github.am4dr.rokusho.app.library.RokushoLibrary
import com.github.am4dr.rokusho.core.library.Record
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
    val libraries: ReadOnlyListProperty<out RokushoLibrary<ImageUrl>> = _libraries.readOnlyProperty

    fun addLibrary(library: RokushoLibrary<ImageUrl>) {
        if (!_libraries.contains(library)) {
            library.createRecordList(library.records.map(Record<ImageUrl>::key))
            _libraries.add(library)
        }
    }
}