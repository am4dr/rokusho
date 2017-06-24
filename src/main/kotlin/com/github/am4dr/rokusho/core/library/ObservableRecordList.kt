package com.github.am4dr.rokusho.core.library

import javafx.beans.property.ReadOnlyListProperty

/**
 * [MetaDataRegistry]が保持している[Record]の部分集合を表す。
 *
 * おおもとである[MetaDataRegistry]で[Record]に変更があった場合には、これが[records]に保持している[Record]は新しい[Record]で置換される。
 */
interface ObservableRecordList<T> {
    val metaDataRegistry: MetaDataRegistry<T>
    val records: ReadOnlyListProperty<Record<T>>
}