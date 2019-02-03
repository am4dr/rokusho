package com.github.am4dr.rokusho.library2

import com.github.am4dr.rokusho.core.util.DataObject

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
        fun parse(string: String): TagData? =
            TagData(string.trim(), DataObject())
    }
}
