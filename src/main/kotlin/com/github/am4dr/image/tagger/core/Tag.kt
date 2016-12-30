package com.github.am4dr.image.tagger.core

import javafx.geometry.Insets
import javafx.scene.Node
import javafx.scene.control.Label
import javafx.scene.layout.Background
import javafx.scene.layout.BackgroundFill
import javafx.scene.layout.CornerRadii
import javafx.scene.paint.Color
import javafx.scene.text.Font

// TODO 他の種類のタグも追加する
interface Tag {
    val text: String
    fun createNode(): Node
    class Text(
            override val text: String,
            private val textFill: Color = Color.rgb(200, 200, 200),
            private val padding: Insets = Insets(-1.0, 2.0, 0.0, 2.0),
            private val font: Font = Font(14.0),
            private val background: Background = Background(BackgroundFill(Color.BLACK, CornerRadii(2.0), null))) : Tag {
        override fun createNode(): Node =
                Label(text).apply {
                    textFill = this@Text.textFill
                    padding = this@Text.padding
                    font = this@Text.font
                    background = this@Text.background
                }
        override fun toString(): String = "Tag.Text($text)"
    }
}
class TagParser {
    // TODO erase companion
    companion object {
        fun parse(string: String): Tag = Tag.Text(string)
    }
}