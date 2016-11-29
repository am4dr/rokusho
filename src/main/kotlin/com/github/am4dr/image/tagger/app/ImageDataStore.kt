package com.github.am4dr.image.tagger.app

import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.nio.charset.Charset
import java.nio.file.Files
import java.nio.file.Path

class ImageDataStore {
    private val log: Logger = LoggerFactory.getLogger(this.javaClass)
    private val data = mutableMapOf<Path, ImageData>()
    fun getData(path: Path): ImageData {
        val p = realPath(path)
        return data[p] ?: ImageData(p).apply { data[p] = this }
    }
    fun load(baseDir: Path, saveFile: Path) {
        val base = realPath(baseDir)
        val save = realPath(saveFile)
        Files.lines(save, Charset.forName("utf-8"))
                .forEach {
                    val d = recordToImageData(base, it)
                    data[d.path] = d
                }
    }
    private fun recordToImageData(base: Path, record: String): ImageData {
        val r = record.split("\t")
        return ImageData(base.resolve(r[0]), r.drop(1))
    }
    private fun realPath(path: Path): Path {
        try { return path.toRealPath() }
        catch (e: Throwable) {
            val msg = "failed on toRealPath(): $path"
            log.error(msg, e)
            throw IllegalArgumentException(msg, e)
        }
    }
}