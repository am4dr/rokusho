package com.github.am4dr.rokusho.library

/**
 * [Library]によって管理されるアイテムのインスタンスを持つクラス
 *
 * 実質的に不変
 */
class Item<T : Any>(val data: T)