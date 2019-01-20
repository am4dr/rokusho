package com.github.am4dr.rokusho.library

/**
 * ある[Library]に含まれているアイテムについてアイテムのインスタンスを取得したり、
 * タグの取得や更新をしたりする操作を行うためのインターフェース
 */
interface LibraryItem<T : Any> {

    fun get(): T
    fun getTags(): Set<LibraryItemTag>
    fun updateTags(tags: Set<LibraryItemTag>): LibraryItem<out T>?
    fun isSame(other: LibraryItem<out T>): Boolean
}