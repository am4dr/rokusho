package com.github.am4dr.rokusho.app

import com.github.am4dr.rokusho.core.library.Library
import com.github.am4dr.rokusho.core.library.MetaDataRegistry
import com.github.am4dr.rokusho.core.library.ObservableRecordList
import javafx.beans.property.ReadOnlyListProperty
import javafx.beans.property.ReadOnlyListWrapper
import javafx.collections.FXCollections.observableArrayList
import java.nio.file.Path

class LocalFileSystemLibrary(val savefilePath: Path,
                             override val metaDataRegistry: MetaDataRegistry<ImageUrl>) : Library<ImageUrl> {
    private val _recordLists = ReadOnlyListWrapper(observableArrayList<ObservableRecordList<ImageUrl>>())
    override val recordLists: ReadOnlyListProperty<ObservableRecordList<ImageUrl>> = _recordLists.readOnlyProperty

}
