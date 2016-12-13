package com.github.am4dr.image.tagger.core

import java.io.File
import java.nio.file.Path
import java.nio.file.Paths

data class ImageMetaData(val path: Path, val tags: MutableList<out String> = mutableListOf())

fun createImageMetaData(tokens: List<String>): ImageMetaData =
        ImageMetaData(Paths.get(tokens[0]), tokens.drop(1).toMutableList())
fun loadImageMataData(file: File): MutableList<ImageMetaData> =
    file.useLines { lines -> lines.map { createImageMetaData(it.split("\t")) }.toMutableList() }