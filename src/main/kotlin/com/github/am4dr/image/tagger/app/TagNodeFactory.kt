package com.github.am4dr.image.tagger.app

import com.github.am4dr.image.tagger.core.Tag
import com.github.am4dr.image.tagger.core.TagInfo
import com.github.am4dr.image.tagger.core.TagType
import javafx.beans.binding.StringBinding
import javafx.beans.property.MapProperty
import javafx.geometry.Insets
import javafx.scene.Node
import javafx.scene.control.Label
import javafx.scene.layout.Background
import javafx.scene.layout.BackgroundFill
import javafx.scene.layout.CornerRadii
import javafx.scene.paint.Color
import javafx.scene.text.Font

class TagNodeFactory(val prototypes: MapProperty<String, TagInfo>) {
    fun createTagNode(tag: Tag): Node =
        createBaseNode().apply {
            textProperty().bind(object : StringBinding() {
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
    private fun createBaseNode(): Label =
            Label().apply {
                textFill = Color.rgb(200, 200, 200)
                padding = Insets(-1.0, 2.0, 0.0, 2.0)
                font = Font(14.0)
                background = Background(BackgroundFill(Color.BLACK, CornerRadii(2.0), null))
            }

}