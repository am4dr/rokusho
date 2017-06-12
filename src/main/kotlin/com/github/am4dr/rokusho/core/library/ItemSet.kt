package com.github.am4dr.rokusho.core.library

import javafx.beans.property.ReadOnlyListProperty

/**
 * [Library]が保持している[Item]の部分集合を表す。
 *
 * おおもとである[Library]で[Item]変更があった場合には、これが[items]に保持している[Item]は新しい[Item]で置換される。
 */
interface ItemSet<T> {
    val library: Library<T>
    val items: ReadOnlyListProperty<Item<T>>
}