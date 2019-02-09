package com.github.am4dr.rokusho.presenter.viewer.multipane.pane.thumbnail

import com.github.am4dr.rokusho.presenter.ItemViewModel
import kotlin.reflect.KClass

class AggregateThumbnailFactory(private val factories: List<ThumbnailFactory>) : ThumbnailFactory {

    override fun maybeAcceptableType(kClass: KClass<*>): Boolean =
        factories.any { it.maybeAcceptableType(kClass) }

    override fun isAcceptable(item: ItemViewModel<*>): Boolean =
        factories.any { it.isAcceptable(item) }

    override fun create(item: ItemViewModel<*>): ThumbnailNode<*>? =
        factories.asSequence()
            .mapNotNull { it.create(item) }
            .firstOrNull()
}