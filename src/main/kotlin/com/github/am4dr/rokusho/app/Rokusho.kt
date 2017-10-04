package com.github.am4dr.rokusho.app

import com.github.am4dr.rokusho.app.library.lfs.FileCollector
import com.github.am4dr.rokusho.app.library.lfs.LocalFileSystemLibrary
import com.github.am4dr.rokusho.app.library.lfs.LocalFileSystemLibraryLoader
import com.github.am4dr.rokusho.app.library.lfs.SaveDataStoreProvider
import com.github.am4dr.rokusho.app.savedata.SaveData
import com.github.am4dr.rokusho.app.savedata.store.FileBasedSaveDataStore
import com.github.am4dr.rokusho.app.savedata.store.SaveDataDeserializer
import com.github.am4dr.rokusho.app.savedata.store.SaveDataSerializer
import com.github.am4dr.rokusho.app.savefile.yaml.YamlSaveDataSerializer
import com.github.am4dr.rokusho.app.savefile.yaml.YamlSaveFileLoader
import com.github.am4dr.rokusho.app.savefile.yaml.YamlSaveFileParser
import com.github.am4dr.rokusho.core.library.ItemTag
import com.github.am4dr.rokusho.core.library.Record
import com.github.am4dr.rokusho.javafx.collection.ConcatenatedList
import com.github.am4dr.rokusho.javafx.collection.TransformedList
import javafx.beans.property.ReadOnlyListProperty
import javafx.beans.property.ReadOnlyListWrapper
import javafx.collections.FXCollections
import javafx.collections.ObservableList
import java.nio.charset.StandardCharsets
import java.nio.file.Files
import java.nio.file.Path

class Rokusho {
    companion object {
        private val imageFileNameMatcher = Regex(".*\\.(bmp|gif|jpe?g|png)$", RegexOption.IGNORE_CASE)
        fun isSupportedImageFile(path: Path) =
                Files.isRegularFile(path) && imageFileNameMatcher.matches(path.fileName.toString())
    }
    private val yamlSaveFileLoader = YamlSaveFileLoader()
    private val saveDataStoreProvider = SaveDataStoreProvider({
        val savefile = yamlSaveFileLoader.locateSaveFilePath(it)?:it.resolve(YamlSaveFileLoader.DEFAULT_SAVEFILE_NAME)
        val store = FileBasedSaveDataStore(savefile,
                object : SaveDataSerializer<SaveData> {
                    val serializer = YamlSaveDataSerializer()
                    override fun invoke(data: SaveData): ByteArray = serializer(data)
                },
                object : SaveDataDeserializer<SaveData> {
                    override fun invoke(bytes: ByteArray): SaveData = YamlSaveFileParser.parse(bytes.toString(StandardCharsets.UTF_8))
                })
        savefile.parent to store
    })
    private val lfsLibraryLoader = LocalFileSystemLibraryLoader(saveDataStoreProvider::get, FileCollector(saveDataStoreProvider::get, Rokusho.Companion::isSupportedImageFile)::collect)

    private val _libraries = ReadOnlyListWrapper(FXCollections.observableArrayList<RokushoLibrary<ImageUrl>>())
    val libraries: ReadOnlyListProperty<out RokushoLibrary<ImageUrl>> = _libraries.readOnlyProperty
    val recordLists: ReadOnlyListProperty<ObservableList<Record<ImageUrl>>>
    init {
        val listOfRecordLists: ObservableList<ObservableList<ObservableList<Record<ImageUrl>>>> = TransformedList(libraries, RokushoLibrary<ImageUrl>::recordLists)
        recordLists = ReadOnlyListWrapper(ConcatenatedList(listOfRecordLists)).readOnlyProperty
    }

    fun addDirectory(directory: Path) {
        val library = lfsLibraryLoader.getLibrary(directory)
        if (!_libraries.contains(library)) {
            library.createRecordList(library.records.map(Record<ImageUrl>::key))
            _libraries.add(library)
        }
    }

    fun updateItemTags(record: Record<ImageUrl>, itemTags: List<ItemTag>) = getLibrary(record)?.updateItemTags(record.key, itemTags)

    fun save() {
        libraries.filterIsInstance(LocalFileSystemLibrary::class.java).forEach(LocalFileSystemLibrary::save)
    }

    fun getLibrary(record: Record<ImageUrl>): RokushoLibrary<ImageUrl>? =
            recordLists.find { it.contains(record) }?.let { list ->
                libraries.find { it.recordLists.contains(list) }
            }
}