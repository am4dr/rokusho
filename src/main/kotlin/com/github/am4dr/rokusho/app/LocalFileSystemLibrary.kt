package com.github.am4dr.rokusho.app

import com.github.am4dr.rokusho.core.library.DefaultMetaDataRegistry
import com.github.am4dr.rokusho.core.library.Library
import com.github.am4dr.rokusho.core.library.MetaDataRegistry
import com.github.am4dr.rokusho.core.library.ObservableRecordList
import javafx.beans.property.ReadOnlyListProperty
import javafx.beans.property.ReadOnlyListWrapper
import javafx.collections.FXCollections.observableArrayList
import java.nio.file.Path

class LocalFileSystemLibrary(savefilePath: Path,
                             override val metaDataRegistry: MetaDataRegistry<ImageUrl> = DefaultMetaDataRegistry()) : Library<ImageUrl> {
    val savefilePath: Path = savefilePath.toAbsolutePath()
    private val _recordLists = ReadOnlyListWrapper(observableArrayList<ObservableRecordList<ImageUrl>>())
    override val recordLists: ReadOnlyListProperty<ObservableRecordList<ImageUrl>> = _recordLists.readOnlyProperty
    override fun createRecordList(list: Iterable<ImageUrl>): ObservableRecordList<ImageUrl> =
            metaDataRegistry.getRecordList(list).also { _recordLists.add(it) }
}
