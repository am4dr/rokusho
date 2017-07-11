package com.github.am4dr.rokusho.app

import com.github.am4dr.rokusho.app.savefile.SaveFile
import com.github.am4dr.rokusho.app.savefile.yaml.YamlSaveFileLoader
import com.github.am4dr.rokusho.app.savefile.yaml.YamlSaveFileParser
import com.github.am4dr.rokusho.core.library.DefaultMetaDataRegistry
import com.github.am4dr.rokusho.core.library.ItemTag
import com.github.am4dr.rokusho.core.library.MetaDataRegistry
import com.github.am4dr.rokusho.core.library.SimpleItemTagDB
import javafx.beans.property.ReadOnlyListProperty
import javafx.beans.property.ReadOnlyListWrapper
import javafx.collections.FXCollections.observableArrayList
import java.nio.file.Files
import java.nio.file.Path

class LocalFileSystemLibraryLoader {
    private val savefileLoader = YamlSaveFileLoader()
    private val _loadedLibraries = ReadOnlyListWrapper(observableArrayList<LocalFileSystemLibrary>())
    val loadedLibraries: ReadOnlyListProperty<LocalFileSystemLibrary> = _loadedLibraries.readOnlyProperty

    private fun addLibrary(library: LocalFileSystemLibrary) = _loadedLibraries.add(library)

    fun getOrLoadLibrary(directory: Path): LocalFileSystemLibrary {
        val savefilePath = getSavefilePathFor(directory)
        findLibraryBySavefilePath(savefilePath)?.let { return it }

        val registry = if (Files.exists(savefilePath)) createMetaDataRegistry(savefileLoader.load(savefilePath)) else DefaultMetaDataRegistry()
        return LocalFileSystemLibrary(savefilePath, registry).also { addLibrary(it) }
    }

    private fun getSavefilePathFor(directory: Path): Path {
        val loaded: LocalFileSystemLibrary? = findLibrariesContains(directory).maxBy { it.savefilePath.nameCount }
        val savefilePath = savefileLoader.locateSaveFilePath(directory)?.normalize()

        return when (compareValues(loaded?.savefilePath?.nameCount, savefilePath?.nameCount)) {
            0  -> loaded?.savefilePath ?: savefileLoader.getDefaultSavefilePath(directory)
            1  -> loaded!!.savefilePath
            else -> savefilePath!!
        }
    }

    private fun createMetaDataRegistry(savefile: SaveFile): MetaDataRegistry<ImageUrl> {
        val tags = savefile.data.tags.values.toMutableList()
        val items = savefile.data.metaData.map { (path, imageMetaData) ->
            val url = ImageUrl(savefile.savefilePath.parent.resolve(path).toUri().toURL())
            val itemTags = imageMetaData.tags.map { ItemTag(it.id, it.data["value"]?.toString() ?: it.id) }
            url to itemTags
        }
        return DefaultMetaDataRegistry(tags, SimpleItemTagDB(items.toMap()))
    }

    private fun findLibrariesContains(directory: Path): List<LocalFileSystemLibrary> =
            _loadedLibraries.filter { directory.normalize().startsWith(it.savefilePath.parent) }

    private fun findLibraryBySavefilePath(savefilePath: Path): LocalFileSystemLibrary? =
            _loadedLibraries.find { savefilePath == it.savefilePath }
}