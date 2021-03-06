package com.github.am4dr.rokusho.library

/**
 * [Tag]の[Library]に依存しないただのデータとしての部分を持つクラス
 *
 * 実質的に不変
 */
data class TagData(
    val name: String,
    val obj: DataObject
) {

    companion object {
        // TODO パーサークラスに切り出す
        fun parse(string: String): TagData? =
            TagData(string.trim(), DataObject())
    }
}
