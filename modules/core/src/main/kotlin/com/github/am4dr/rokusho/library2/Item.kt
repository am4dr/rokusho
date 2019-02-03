package com.github.am4dr.rokusho.library2

/**
 * [Library]によって管理されるアイテムのインスタンスを持つクラス
 *
 * 実質的に不変
 */
class Item<T : Any>(val data: T)