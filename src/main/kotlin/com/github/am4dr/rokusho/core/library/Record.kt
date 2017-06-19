package com.github.am4dr.rokusho.core.library

/**
 * [Library]の管理下にあるアイテムを表すデータ。
 *
 * [Library]がそれに付与している[ItemTag]のリストを持つ。
 */
data class Record<out T>(val key: T, val itemTags: List<ItemTag>)