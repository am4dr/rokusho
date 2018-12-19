package com.github.am4dr.rokusho.core.library

import com.github.am4dr.rokusho.core.metadata.BaseTag

internal class LibraryItemTagTemplateWrapper(val tag: BaseTag) : LibraryItemTagTemplate {

    override val name: String
        get() = tag.name.name
    override val entries: Set<Pair<String, String>>
        get() = tag.data.entries
    override val entryNames: Set<String>
        get() = tag.data.keys

    override fun get(value: String): String? =
        if (value == "name") name
        else tag.data[value]

    override fun toString(): String = "${this::class.simpleName}($tag)"
}