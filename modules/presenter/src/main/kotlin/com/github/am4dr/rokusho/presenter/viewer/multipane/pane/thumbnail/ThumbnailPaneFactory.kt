package com.github.am4dr.rokusho.presenter.viewer.multipane.pane.thumbnail

import com.github.am4dr.rokusho.javafx.control.ImageOverlay
import com.github.am4dr.rokusho.javafx.thumbnail.CachedThumbnailFlowPane
import com.github.am4dr.rokusho.presenter.ItemViewModel
import com.github.am4dr.rokusho.presenter.viewer.multipane.MultiPaneViewer
import com.github.am4dr.rokusho.presenter.viewer.multipane.PaneFactory
import javafx.event.EventHandler
import javafx.scene.layout.Background
import javafx.scene.layout.BackgroundFill
import javafx.scene.paint.Color
import kotlin.reflect.KClass


class ThumbnailPaneFactory(
    thumbnailFactories: List<ThumbnailFactory>
) : PaneFactory {

    private val thumbnailFactories = AggregateThumbnailFactory(thumbnailFactories)

    override fun isAcceptable(type: KClass<*>): Boolean {
        return thumbnailFactories.maybeAcceptableType(type)
    }

    override fun create(): MultiPaneViewer.Pane? {
        val viewer = createImageRecordsViewer { thumbnailFactories.create(it)!! }
        return MultiPaneViewer.Pane("サムネイル", viewer, viewer.records, thumbnailFactories::isAcceptable)
    }

    private fun createImageRecordsViewer(thumbnailFactory: (ItemViewModel<*>) -> ThumbnailNode<*>): CachedThumbnailFlowPane<ItemViewModel<*>> {
        val imageViewer = createImageViewer()
        return CachedThumbnailFlowPane<ItemViewModel<*>> {
            thumbnailFactory(it).apply {
                view.setOnMouseClicked {
                    getFullImage.get()?.invoke()?.let(imageViewer::show)
                }
            }
        }.apply { children.add(imageViewer) }
    }

    private fun createImageViewer(): ImageOverlay =
        ImageOverlay().apply {
            isVisible = false
            background = Background(BackgroundFill(Color.rgb(30, 30, 30, 0.75), null, null))
            onMouseClicked = EventHandler { hide() }
        }
}
