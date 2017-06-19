package com.github.am4dr.rokusho.core.library

import javafx.beans.property.ReadOnlyListProperty

/**
 * [Library]が保持している[Record]の部分集合を表す。
 *
 * おおもとである[Library]で[Record]に変更があった場合には、これが[records]に保持している[Record]は新しい[Record]で置換される。
 */
interface ObservableRecordList<T> {
    val library: Library<T>
    val records: ReadOnlyListProperty<Record<T>>
}