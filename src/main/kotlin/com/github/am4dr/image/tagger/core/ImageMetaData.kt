package com.github.am4dr.image.tagger.core

import java.io.File
import java.nio.file.Path
import java.nio.file.Paths

data class ImageMetaData(val tags: List<Tag> = listOf())

fun parseImageMetaData(tokens: String): Pair<Path, ImageMetaData> =
        parseImageMetaData(tokens.split("\t"))
fun parseImageMetaData(tokens: List<String>): Pair<Path, ImageMetaData> =
        Pair(Paths.get(tokens.first()).normalize(), ImageMetaData(tokens.drop(1).map { TagParser.parse(it) } ))
fun loadImageMataData(file: File): MutableMap<Path, ImageMetaData> =
        file.readLines().associateTo(mutableMapOf<Path, ImageMetaData>(), ::parseImageMetaData)
fun toSaveFormat(data: Map<Path, ImageMetaData>): String =
        data.asIterable().map {
            val (path, meta) = it
            "${path.normalize()}\t${meta.tags.joinToString("\t")}"
        }.joinToString("\n")
fun saveImageMetaData(data: Map<Path, ImageMetaData>, to: File) =
        to.writeText(toSaveFormat(data))
