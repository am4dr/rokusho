package com.github.am4dr.rokusho.presenter

// TODO presenterにおけるItemということが分かる名前にしたい。Itemにしたいがcoreと衝突するので悩ましい
interface ItemViewModel<T : Any> {

    val item: T
    val tags: List<ItemTagData>

    fun parseTagString(string: String): ItemTagData?
    fun updateTags(tags: List<ItemTagData>)
}