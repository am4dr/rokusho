package com.github.am4dr.rokusho.library2.app

import com.github.am4dr.rokusho.core.datastore.DataStore
import com.github.am4dr.rokusho.core.datastore.savedata.SaveData
import com.github.am4dr.rokusho.core.datastore.savedata.yaml.YamlSaveDataStore
import com.github.am4dr.rokusho.core.util.DataObject
import com.github.am4dr.rokusho.library2.*
import kotlinx.coroutines.ExperimentalCoroutinesApi
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.PathMatcher
import java.util.*
import kotlin.coroutines.CoroutineContext
import kotlin.streams.asSequence
import com.github.am4dr.rokusho.core.datastore.savedata.Item as DataItem
import com.github.am4dr.rokusho.core.datastore.savedata.ItemTag as DataItemTag
import com.github.am4dr.rokusho.core.datastore.savedata.Tag as DataTag

@ExperimentalCoroutinesApi
class PathLibraryLoader(
    private val context: CoroutineContext
) {

    companion object {
        private const val defaultSavefileName: String = "rokusho.yaml"
        private val saveFilePathMatchers: PathMatcher = PathMatcher { it.endsWith(defaultSavefileName) }
    }

    private val libraries = Collections.synchronizedMap<Path, LoadedLibrary>(mutableMapOf())


    fun getOrCreate(path: Path): LoadedLibrary? {
        val root = path
        val loadedLibrary = libraries.getOrPut(root) {
            createLoadedLibrary(root)
        }
        return loadedLibrary
    }

    // TODO 深さが1よりも大きいライブラリの存在を認めるかどうか考える。
    // 認めるならば次の関数が必要
//    private fun findRoot(path: Path): Path {
//        fun isLibraryRoot(path: Path): Boolean =
//            libraries.keys.any { Files.isSameFile(it, path) } ||
//                    Files.isDirectory(path) && Files.list(path).anyMatch(saveFilePathMatchers::matches)
//        tailrec fun findToUpper(path: Path): Path? = when {
//            isLibraryRoot(path) -> path
//            path.parent != null -> findToUpper(path.parent)
//            else -> null
//        }
//        return findToUpper(path) ?: path
//    }

    private fun createLoadedLibrary(root: Path): LoadedLibrary {
        val store = createDataStore(root)
        val data = store.load()
        val tags  = data?.tags ?: setOf()
        val itemData = data?.items ?: setOf()
        val library = Library(context, tags, createLibraryItemSequence(root, itemData))
        return LoadedLibrary(library, root.fileName.toString(), store)
    }

    private fun createLibraryItemSequence(root: Path, itemData: Set<LibraryItem<*>>): Sequence<LibraryItem<*>> {
        return getPathCollection(root).map { path ->
            itemData.find { it.item.data is Path && Files.isSameFile(it.item.data, path) }
                ?: LibraryItem(Item(path), setOf())
        }.asSequence()
    }

    private fun getPathCollection(root: Path): Sequence<Path> {
        return Files.list(root).filter { Files.isRegularFile(it) }.asSequence()
    }

    private fun createDataStore(root: Path): DataStore<Library.Data> {
        val saveFile = root.resolve(defaultSavefileName)
        return LibraryDataStore(saveFile)
    }

    @ExperimentalCoroutinesApi
    internal class LibraryDataStore(
        private val saveFile: Path,
        private val libraryRoot: Path = saveFile.parent
    ) : DataStore<Library.Data> {

        private val yamlSaveDataStore = YamlSaveDataStore(saveFile)

        override fun save(data: Library.Data) {
            yamlSaveDataStore.save(convert(data))
        }

        override fun load(): Library.Data? {
            val loaded = yamlSaveDataStore.load()
            return convert(loaded)
        }

        private fun convert(data: Library.Data): SaveData {
            val (tags, items) = data
            val dataTags = tags.map { tag -> tag to tagToDataTag(tag) }.toMap()
            val dataItems = items.mapNotNull libraryItemLoop@{ libraryItem ->
                val path = libraryItem.item.data as? Path ?: return@libraryItemLoop null
                val libraryItemTags = libraryItem.tags
                val id = libraryRoot.relativize(path).joinToString("/")
                val dataItemTags = libraryItemTags.mapNotNull itemTagLoop@{ itemTag ->
                    val tagData = dataTags.entries.find { (key) -> key.isSameEntity(itemTag.tag) }?.value ?: return@itemTagLoop null
                    DataItemTag(tagData, itemTag["value"])
                }
                DataItem(id, dataItemTags.sortedBy { it.tag.id })
            }
            return SaveData(
                dataTags.values.sortedBy { it.id }.toList(),
                dataItems.sortedBy { it.id }
            )
        }

        private fun tagToDataTag(tag: Tag): DataTag {
            val id = tag.name
            val type = tag["type"]?.let(DataTag.Type.Companion::from) ?: DataTag.Type.TEXT
            val data = tag.data.obj.asMap()
            return DataTag(id, type, data)
        }

        private fun convert(data: SaveData?): Library.Data? {
            val (dataTags, dataItems) = data ?: return null
            val tags = dataTags.map { dataTag ->
                val checkedData = dataTag.data.entries.mapNotNull { (key, value) ->
                    val checkedValue = value as? String ?: return@mapNotNull null
                    key to checkedValue
                }.toMap()
                dataTag.id to Tag(TagData(dataTag.id, DataObject(checkedData.toMap())))
            }.toMap()
            val items = dataItems.map { dataItem ->
                val path = libraryRoot.resolve(dataItem.id)
                val itemTags = dataItem.tags.mapNotNull dataItemTagLoop@{ dataItemTag ->
                    val tag = tags[dataItemTag.tag.id] ?: return@dataItemTagLoop null
                    val itemTagData = dataItemTag.value?.let { DataObject(mapOf("value" to it)) } ?: DataObject()
                    ItemTag(tag, itemTagData)
                }.toSet()
                LibraryItem(Item(path), itemTags)
            }.toSet()
            return Library.Data(tags.values.toSet(), items)
        }
    }
}