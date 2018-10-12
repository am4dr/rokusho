package com.github.am4dr.rokusho.app

import com.github.am4dr.rokusho.core.library.Library
import com.github.am4dr.rokusho.core.library.LibraryImpl
import com.github.am4dr.rokusho.core.library.provider.LibraryDescriptor
import com.github.am4dr.rokusho.core.library.provider.LibraryProvider
import com.github.am4dr.rokusho.core.library.provider.StandardLibraryProviderDescriptors
import com.github.am4dr.rokusho.core.metadata.Record
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.net.URI
import java.nio.file.Path
import java.nio.file.Paths

class FileSystemBasedLibraryProvider : LibraryProvider<Path> {

    companion object {
        const val descriptorString: String = "rokusho.filesystem.files"
        val log: Logger = LoggerFactory.getLogger(FileSystemBasedLibraryProvider::class.java)
        fun createDescriptor(uri: URI): LibraryDescriptor =
                LibraryDescriptor(StandardLibraryProviderDescriptors.of(descriptorString), uri.toString())
    }

    override val name: String = "FileSystem-based Library provider"
    override val description: String = "FileSystemに基づいたLibraryを提供する"

    override fun isAcceptable(descriptor: LibraryDescriptor): Boolean =
            descriptor.libraryProviderDescriptor.let { desc ->
                if (desc !is StandardLibraryProviderDescriptors) return false
                when (desc) {
                    is StandardLibraryProviderDescriptors.FQCNDescriptor -> {
                        desc.value == this::class.qualifiedName
                    }
                    is StandardLibraryProviderDescriptors.StringDescriptor -> {
                        desc.value == descriptorString
                    }
                }
            }

    private val metaDataRepositories = FileBasedMetaDataRepositories()

    override fun get(descriptor: LibraryDescriptor): Library<Path>? {
        if (!isAcceptable(descriptor)) return null

        val uri = URI(descriptor.libraryCoordinates)
        val path = getPath(uri) ?: return null
        val (savefile, metaRepo) = metaDataRepositories.getOrCreate(path)
        val items = PathCollection(savefile.parent, path)
        return LibraryImpl(metaRepo, items, { Record.Key(it.id) }, Path::class)
    }

    internal fun getPath(uri: URI): Path? = try {
        Paths.get(uri).normalize()
    } catch (e: Exception) {
        log.warn(e.localizedMessage)
        null
    }

}