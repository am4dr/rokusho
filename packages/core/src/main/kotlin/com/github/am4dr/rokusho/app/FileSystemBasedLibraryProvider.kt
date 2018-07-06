package com.github.am4dr.rokusho.app

import com.github.am4dr.rokusho.core.Library
import com.github.am4dr.rokusho.core.LibraryDescriptor
import com.github.am4dr.rokusho.core.LibraryProvider
import com.github.am4dr.rokusho.core.provider.ProviderDescriptor
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.net.URI
import java.nio.file.Path
import java.nio.file.Paths

class FileSystemBasedLibraryProvider : LibraryProvider {

    companion object {
        const val descriptorString: String = "rokusho.filesystem.files"
        val log: Logger = LoggerFactory.getLogger(FileSystemBasedLibraryProvider::class.java)
        fun createDescriptor(uri: URI): LibraryDescriptor =
                LibraryDescriptor(ProviderDescriptor.of(descriptorString), uri.toString())
    }

    override val name: String = "FileSystem-based Library provider"
    override val description: String = "FileSystemに基づいたLibraryを提供する"

    override fun isAcceptable(descriptor: LibraryDescriptor): Boolean =
            descriptor.providerDescriptor.let { desc ->
                when (desc) {
                    is ProviderDescriptor.FQCNDescriptor -> {
                        desc.value == this::class.qualifiedName
                    }
                    is ProviderDescriptor.StringDescriptor -> {
                        desc.value == descriptorString
                    }
                }
            }

    private val metaDataRepositories = MetaDataRepositories()

    override fun get(descriptor: LibraryDescriptor): Library? {
        if (!isAcceptable(descriptor)) return null

        val uri = URI(descriptor.libraryCoordinates)
        val path = getPath(uri) ?: return null
        val (savefile, metaRepo) = metaDataRepositories.getOrCreate(path)
        val items = PathCollection(savefile.parent, path)
        return Library(createDescriptor(uri), metaRepo, items)
    }

    internal fun getPath(uri: URI): Path? = try {
        Paths.get(uri).normalize()
    } catch (e: Exception) {
        log.warn(e.localizedMessage)
        null
    }

}