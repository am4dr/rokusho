package com.github.am4dr.rokusho.gui.sidemenu

import com.github.am4dr.rokusho.javafx.collection.TransformedList
import javafx.beans.binding.Bindings
import javafx.beans.property.*
import javafx.collections.FXCollections
import javafx.collections.ListChangeListener
import javafx.collections.ObservableList
import javafx.scene.Cursor
import javafx.scene.Node
import javafx.scene.layout.*
import javafx.scene.paint.Color

class SideMenuPane : HBox() {

    val content: ObjectProperty<Node> = SimpleObjectProperty(null)

    val iconSize: DoubleProperty = SimpleDoubleProperty(48.0)
    val expansionWidth: DoubleProperty = SimpleDoubleProperty(150.0)
    val minExpansionWidth: DoubleProperty = SimpleDoubleProperty(100.0)

    private val iconColumn = VBox()
    private val expansionColumn = BorderPane()
    private val contentColumn = StackPane()

    val items: ObservableList<SideMenuItem> = FXCollections.observableArrayList()
    private val icons = TransformedList(items, SideMenuItem::icon)
    private val currentItem = SimpleObjectProperty<SideMenuItem>(null)
    val showExpansion: BooleanProperty = SimpleBooleanProperty(true)

    init {
        HBox.setHgrow(iconColumn, Priority.NEVER)
        HBox.setHgrow(expansionColumn, Priority.NEVER)
        HBox.setHgrow(contentColumn, Priority.ALWAYS)

        iconColumn.apply {
            maxWidthProperty().bind(iconSize)
            minWidthProperty().bind(iconSize)
            Bindings.bindContent(children, icons)
            setOnMouseClicked { e ->
                val icon = icons.find { it.contains(e.x, e.y) } ?: return@setOnMouseClicked
                val item = items.find { it.icon === icon } ?: return@setOnMouseClicked

                if (currentItem.value !== item) {
                    currentItem.value = item
                    showWithMinWidth()
                }
                else {
                    if (showExpansion.value) showExpansion.value = false
                    else showWithMinWidth()
                }
            }
        }

        expansionWidth.addListener { _, _, new ->
            if (new.toDouble() >= minExpansionWidth.value) {
                showExpansion.value = true
            }
            else if (new.toDouble() <= minExpansionWidth.value) {
                showExpansion.value = false
            }
        }
        expansionColumn.apply {
            maxWidthProperty().bind(expansionWidth)
            minWidthProperty().bind(expansionWidth)
            managedProperty().bind(showExpansion)
            visibleProperty().bind(showExpansion)
            centerProperty().bind(Bindings.createObjectBinding({ currentItem.value?.expansion }, arrayOf(currentItem)))
        }

        contentColumn.apply {
            children.addAll(BorderPane().apply { centerProperty().bind(content) }, resizeHandleNode())
        }
        items.addListener(ListChangeListener{ c ->
            while (c.next()) {
                if (c.wasRemoved() && items.size == c.removedSize) {
                    currentItem.value = null
                }
                if (c.wasAdded() && items.size == c.addedSize) {
                    currentItem.value = c.addedSubList[0]
                }
            }
        })

        children.addAll(iconColumn, expansionColumn, contentColumn)
    }

    private fun showWithMinWidth() {
        if (expansionWidth.value < minExpansionWidth.value) {
            expansionWidth.value = minExpansionWidth.value
        }
        showExpansion.value = true
    }

    private fun resizeHandleNode(): Node {
        return HBox().apply {
            pickOnBoundsProperty().value = false
            children.addAll(Pane().apply {
                minWidth = 5.0
                maxWidth = 5.0
                background = Background(BackgroundFill(Color.rgb(0, 0, 0, 0.001), null, null))
                HBox.setHgrow(this, Priority.NEVER)
                var oldCursor = cursor
                setOnMouseEntered {
                    oldCursor = cursor
                    cursor = Cursor.H_RESIZE
                }
                setOnMouseExited { cursor = oldCursor }
                var startWidth = 0.0
                var startScreenX = 0.0
                setOnMousePressed {
                    startScreenX = it.screenX
                    startWidth = if (showExpansion.value) expansionWidth.value else 0.0
                }
                setOnMouseDragged {
                    expansionWidth.value = startWidth + it.screenX - startScreenX
                }
                setOnMouseReleased {
                    if (!showExpansion.value) {
                        expansionWidth.value = 0.0
                    }
                }
            }, Pane().apply {
                pickOnBoundsProperty().value = false
                HBox.setHgrow(this, Priority.ALWAYS)
            })
        }
    }
}