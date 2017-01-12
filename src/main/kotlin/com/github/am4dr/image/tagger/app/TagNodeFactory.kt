package com.github.am4dr.image.tagger.app

import com.github.am4dr.image.tagger.core.Tag
import com.github.am4dr.image.tagger.core.TagInfo
import com.github.am4dr.image.tagger.core.TagType
import javafx.beans.binding.StringBinding
import javafx.beans.binding.When
import javafx.beans.property.MapProperty
import javafx.beans.property.ObjectProperty
import javafx.beans.property.SimpleObjectProperty
import javafx.beans.property.StringProperty
import javafx.event.EventHandler
import javafx.geometry.Insets
import javafx.scene.control.Button
import javafx.scene.control.Label
import javafx.scene.layout.*
import javafx.scene.paint.Color
import javafx.scene.text.Font

class TagNodeFactory(val prototypes: MapProperty<String, TagInfo>) {
    fun createTagNode(tag: Tag): TagNode =
        TagNode().apply {
            textProperty.bind(object : StringBinding() {
                init {
                    super.bind(
                            if (prototypes.containsKey(tag.name)) prototypes.valueAt(tag.name)
                            else prototypes)
                }
                override fun computeValue(): String = toTextFormat(tag)
            })
        }
    fun toTextFormat(tag: Tag): String =
        when (prototypes[tag.name]?.let(TagInfo::type)) {
            TagType.TEXT, null -> tag.name
            TagType.VALUE -> "${tag.name} | ${tag.data["value"]?.let { it } ?: "-"}"
            TagType.SELECTION -> "${tag.name} | ${tag.data["value"]?.let {it} ?: "-"}"
            TagType.OTHERS -> tag.name
        }
}
class TagNode : StackPane() {
    companion object {
        private val buttonBackground = Background(BackgroundFill(Color.rgb(60, 50, 50), CornerRadii(2.0), null))
        private val buttonBackgroundHovered = Background(BackgroundFill(Color.DARKRED, CornerRadii(2.0), null))
    }
    private val label = Label().apply {
        textFill = Color.rgb(200, 200, 200)
        padding = Insets(-1.0, 2.0, 0.0, 2.0)
        font = Font(14.0)
        background = Background(BackgroundFill(Color.BLACK, CornerRadii(2.0), null))
    }
    val textProperty: StringProperty = label.textProperty()
    val onCloseClickedProperty: ObjectProperty<(TagNode) -> Unit> = SimpleObjectProperty({ it -> })

    private val closeButton = Button(" × ").apply {
        padding = Insets(-1.0, 2.0, 0.0, 2.0)
        font = Font(14.0)
        textFillProperty().bind(
                When(hoverProperty())
                        .then(Color.WHITE)
                        .otherwise(Color.rgb(200, 200, 200)))
        backgroundProperty().bind(
                When(hoverProperty())
                        .then(buttonBackgroundHovered)
                        .otherwise(buttonBackground))
        onAction = EventHandler { onCloseClickedProperty.get().invoke(this@TagNode) }
    }
    init {
        children.addAll(
                Pane(closeButton).apply {
                    managedProperty().set(false)
                    visibleProperty().bind(this@TagNode.hoverProperty())
                    layoutXProperty().bind(label.layoutXProperty().subtract(closeButton.widthProperty().subtract(2)))
                },
                label
        )

    }
}