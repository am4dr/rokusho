package com.github.am4dr.rokusho.core.library

/**
 * [MetaDataRegistry]の管理下にあるアイテムを表すデータ。
 *
 * [MetaDataRegistry]がそれに付与している[ItemTag]のリストを持つ。
 */
data class Record<out T>(val key: T, val itemTags: List<ItemTag>)