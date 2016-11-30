package com.github.am4dr.image.tagger.app

import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.nio.charset.Charset
import java.nio.file.Files
import java.nio.file.Path
import java.util.stream.Collectors

private val imageFileNameMatcher = Regex(".*\\.(bmp|gif|jpe?g|png)$", RegexOption.IGNORE_CASE)

class ImageDataStore {
    private val log: Logger = LoggerFactory.getLogger(this.javaClass)
    private val data = mutableMapOf<Path, ImageData>()
    fun getData(path: Path): ImageData {
        val p = path.toRealPath()
        return data[p] ?: ImageData(p).apply { data[p] = this }
    }
    fun loadImageInfo(baseDir: Path, infoPath: Path) {
        val base = baseDir.toRealPath()
        if(Files.notExists(infoPath)) {
            log.info("info file not found: $infoPath")
            return
        }
        log.info("load image info from file: $infoPath")
        val save = infoPath.toRealPath()
        Files.lines(save, Charset.forName("utf-8"))
                .forEach {
                    val d = recordToImageData(base, it)
                    data[d.path] = d
                }
    }
    fun loadImageData(targetDir: Path): List<ImageData> =
        Files.list(targetDir)
                .filter { imageFileNameMatcher.matches(it.fileName.toString()) }
                .map { getData(it) }
                .collect(Collectors.toList<ImageData>())
    fun loadImageData(baseDir: Path, info: Path): List<ImageData> {
        loadImageInfo(baseDir, info)
        return loadImageData(baseDir)
    }
    private fun recordToImageData(base: Path, record: String): ImageData {
        val r = record.split("\t")
        return ImageData(base.resolve(r[0]), r.drop(1))
    }
}