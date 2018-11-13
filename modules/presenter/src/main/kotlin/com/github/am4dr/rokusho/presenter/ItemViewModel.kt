package com.github.am4dr.rokusho.presenter

import com.github.am4dr.rokusho.core.metadata.PatchedTag

// TODO presenterにおけるItemということが分かる名前にしたい。Itemにしたいがcoreと衝突するので悩ましい
interface ItemViewModel<T : Any> {

    val item: T
    val tags: List<PatchedTag>

    fun parseTagString(string: String): PatchedTag?
    fun updateTags(tags: List<PatchedTag>)
}