package com.github.am4dr.rokusho.gui.sidemenu

import com.github.am4dr.rokusho.javafx.collection.ConcatenatedList
import com.github.am4dr.rokusho.javafx.collection.TransformedList
import javafx.beans.binding.Bindings
import javafx.beans.property.*
import javafx.collections.FXCollections
import javafx.collections.ObservableList
import javafx.scene.Node
import javafx.scene.control.Label
import javafx.scene.layout.HBox
import javafx.scene.layout.Pane
import javafx.scene.layout.Priority
import javafx.scene.layout.VBox

class ExpandableSideMenu : HBox() {
    private val iconColumn: Pane = VBox()
    private val currentExpansion = SimpleObjectProperty<Node?>()
    val iconSizeProperty: DoubleProperty = SimpleDoubleProperty(48.0)
    val minExpansionWidth: Double = 100.0
    private val _expansionWidth: ReadOnlyDoubleWrapper = ReadOnlyDoubleWrapper(100.0)
    val expansionWidth: ReadOnlyDoubleProperty = _expansionWidth.readOnlyProperty
    val expandedWidth: ReadOnlyDoubleProperty = ReadOnlyDoubleWrapper().apply { bind(iconSizeProperty.add(_expansionWidth)) }.readOnlyProperty

    val onClose: ObjectProperty<() -> Unit> = SimpleObjectProperty({})
    val onExpand: ObjectProperty<() -> Unit> = SimpleObjectProperty({})

    val items: ObservableList<SideMenuItem> = FXCollections.observableArrayList()
    private val configuredItems = TransformedList(items, this::configureItem)
    private val icons = TransformedList(configuredItems, SideMenuItem::icon)
    private val expansions = TransformedList(configuredItems, SideMenuItem::expansion)
    private val dummyPane = Pane(Label("dummy")).apply {
        currentExpansion.value = this
        configureExpansion(this)
    }
    private val _children = ConcatenatedList.concat<Node>(FXCollections.observableArrayList(iconColumn, dummyPane), expansions)

    init {
        iconColumn.minWidthProperty().bind(iconSizeProperty)
        iconColumn.maxWidthProperty().bind(iconSizeProperty)
        HBox.setHgrow(iconColumn, Priority.NEVER)

        Bindings.bindContent(iconColumn.children, icons)

        Bindings.bindContent(children, _children)

        _expansionWidth.addListener { _, _old, _new ->
            val old = _old.toDouble()
            val new = _new.toDouble()
            println("expansion width: $old -> $new")
        }
        widthProperty().addListener { _, _old, _new ->
            val old = _old.toDouble()
            val new = _new.toDouble()

            if (new >= iconSizeProperty.value + minExpansionWidth) {
                _expansionWidth.value = new - iconSizeProperty.value
            }
        }
    }

    private fun configureItem(item: SideMenuItem): SideMenuItem = item.apply {
        configureExpansion(expansion)
        icon.apply {
            maxWidthProperty().bind(iconSizeProperty)
            maxHeightProperty().bind(iconSizeProperty)
            minWidthProperty().bind(iconSizeProperty)
            minHeightProperty().bind(iconSizeProperty)
            VBox.setVgrow(this, Priority.NEVER)

            setOnMouseClicked {
                clickIcon(this)
            }
        }
    }
    private fun configureExpansion(expansion: Node): Node = expansion.apply {
        HBox.setHgrow(this, Priority.ALWAYS)
        visibleProperty().bind(managedProperty())
        managedProperty().bind(Bindings.createBooleanBinding({ currentExpansion.value?.let { it === this } ?: false }, arrayOf(currentExpansion)))
    }

    fun clickIcon(icon: Node) {
        val ex = items.find { it.icon === icon }?.expansion ?: return
        if (currentExpansion.value != null && currentExpansion.value === ex && width > iconSizeProperty.value + 10.0) {
            close()
        }
        else {
            currentExpansion.value = ex
            expand()
        }
    }
    fun close() {
        println("close")
        onClose.value.invoke()
    }
    fun expand() {
        println("expand")
        onExpand.value.invoke()
    }
}