package com.github.am4dr.rokusho.presenter.viewer.multipane.pane.thumbnail

import com.github.am4dr.rokusho.presenter.ItemViewModel
import kotlin.reflect.KClass

interface ThumbnailFactory {

    fun maybeAcceptableType(kClass: KClass<*>): Boolean
    fun isAcceptable(item: ItemViewModel<*>): Boolean
    fun create(item: ItemViewModel<*>): ThumbnailNode<*>?
}
