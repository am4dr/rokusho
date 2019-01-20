package com.github.am4dr.rokusho.presenter.viewer.multipane.pane.thumbnail

import com.github.am4dr.rokusho.javafx.control.RemovableTag
import com.github.am4dr.rokusho.javafx.thumbnail.ThumbnailTagEditorViewModel
import com.github.am4dr.rokusho.library.LibraryItemTag

class LibraryItemTagTagNodeFactory(private val editor: ThumbnailTagEditorViewModel<LibraryItemTag>) {

    companion object {
        // TODO 仕様の詳細を知りすぎているので取り除く(typeとかvalueとかのキー名をなぜ知っているのかなど)
        private fun tagToTagText(tag: LibraryItemTag): String {
            val type = tag["type"]
            val value = tag["value"]
            val name = tag.name
            return when (type) {
                "text" -> value ?: name
                "value" -> "$name | ${value?.takeIf { it.isNotBlank() } ?: "-"}"
                "selection" -> "$name | ${value?.takeIf { it.isNotBlank() } ?: "-"}"
                else -> name
            }
        }
    }

    fun create(tag: LibraryItemTag): RemovableTag {
        return RemovableTag().apply {
            textProperty.set(tagToTagText(tag))
            onRemoved.set { editor.remove(tag) }
        }
    }
}