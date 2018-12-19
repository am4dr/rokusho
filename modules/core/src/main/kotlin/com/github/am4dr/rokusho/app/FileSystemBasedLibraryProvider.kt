package com.github.am4dr.rokusho.app

import com.github.am4dr.rokusho.core.metadata.Record
import com.github.am4dr.rokusho.library.Library
import com.github.am4dr.rokusho.library.impl.LibraryImpl
import com.github.am4dr.rokusho.library.provider.LibraryDescriptor
import com.github.am4dr.rokusho.library.provider.LibraryProvider
import com.github.am4dr.rokusho.library.provider.StandardLibraryProviderDescriptors
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.net.URI
import java.nio.file.Path
import java.nio.file.Paths

class FileSystemBasedLibraryProvider(
    private val metaDataRepositories: FileBasedMetaDataRepositories
) : LibraryProvider<Path> {

    companion object {
        const val descriptorString: String = "rokusho.filesystem.files"
        val log: Logger = LoggerFactory.getLogger(FileSystemBasedLibraryProvider::class.java)
        fun createDescriptor(uri: URI): LibraryDescriptor =
            LibraryDescriptor(
                StandardLibraryProviderDescriptors.of(
                    descriptorString
                ), uri.toString()
            )
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

    override fun get(descriptor: LibraryDescriptor): Library<Path>? {
        if (!isAcceptable(descriptor)) return null

        val uri = URI(descriptor.libraryCoordinates)
        val path = getPath(uri) ?: return null
        val (savefile, metaRepo) = metaDataRepositories.getOrCreate(path)
        val items = PathCollection(savefile.parent, path)
        return LibraryImpl(
            "Path Library: $path",
            path.fileName.toString(),
            Path::class,
            items,
            metaRepo
        ) { Record.Key(it.id) }
    }

    internal fun getPath(uri: URI): Path? = try {
        Paths.get(uri).normalize()
    } catch (e: Exception) {
        log.warn(e.localizedMessage)
        null
    }

}