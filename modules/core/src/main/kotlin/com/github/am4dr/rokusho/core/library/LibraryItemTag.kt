package com.github.am4dr.rokusho.core.library

interface LibraryItemTag {

    val name: String
    val entries: Set<Pair<String, String>>
    val entryNames: Set<String>

    operator fun get(value: String): String?
}
