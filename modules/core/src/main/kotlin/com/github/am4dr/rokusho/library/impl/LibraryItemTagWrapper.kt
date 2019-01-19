package com.github.am4dr.rokusho.library.impl

import com.github.am4dr.rokusho.core.metadata.PatchedTag
import com.github.am4dr.rokusho.library.LibraryItemTag

internal class LibraryItemTagWrapper(val tag: PatchedTag) : LibraryItemTag {

    override val name: String
        get() = tag.base.name.name
    override val entries: Set<Pair<String, String>>
        get() = tag.data.entries
    override val entryNames: Set<String>
        get() = tag.data.keys

    override fun get(value: String): String? =
        if (value == "name") tag.base.name.name
        else tag.data[value]

    override fun toString(): String = "${this::class.simpleName}($tag)"
}