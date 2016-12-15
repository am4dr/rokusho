package com.github.am4dr.image.tagger.core

import java.io.File
import java.nio.file.Path
import java.nio.file.Paths

data class ImageMetaData(val tags: MutableList<out String> = mutableListOf())

fun parseImageMetaData(tokens: String): Pair<Path, ImageMetaData> =
        parseImageMetaData(tokens.split("\t"))
fun parseImageMetaData(tokens: List<String>): Pair<Path, ImageMetaData> =
        Pair(Paths.get(tokens.first()).normalize(), ImageMetaData(tokens.drop(1).toMutableList()))
fun loadImageMataData(file: File): MutableMap<Path, ImageMetaData> =
        file.readLines().associateTo(mutableMapOf<Path, ImageMetaData>(), ::parseImageMetaData)
