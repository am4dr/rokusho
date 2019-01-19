package com.github.am4dr.rokusho.presenter

import com.github.am4dr.rokusho.library.LibraryItemTag

// TODO presenterにおけるItemということが分かる名前にしたい。Itemにしたいがcoreと衝突するので悩ましい
interface ItemViewModel<T : Any> {

    val item: T
    val tags: List<LibraryItemTag>

    fun parseTagString(string: String): LibraryItemTag?
    fun updateTags(tags: List<LibraryItemTag>)
}