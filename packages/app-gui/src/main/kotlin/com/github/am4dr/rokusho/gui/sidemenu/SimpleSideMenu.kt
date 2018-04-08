package com.github.am4dr.rokusho.gui.sidemenu

import javafx.beans.binding.Bindings
import javafx.beans.property.*
import javafx.collections.FXCollections.observableArrayList
import javafx.scene.control.Label
import javafx.scene.layout.*
import javafx.scene.paint.Color

class SimpleSideMenu(onAddClicked: () -> Unit) {

    private val _icons = ReadOnlyListWrapper<SideMenuIcon>(observableArrayList())
    val icons: ReadOnlyListProperty<SideMenuIcon> = _icons.readOnlyProperty
    val width: DoubleProperty = SimpleDoubleProperty(20.0)
    val node: VBox = VBox().apply {
        prefWidthProperty().bind(this@SimpleSideMenu.width)
        Bindings.bindContent(children, icons)
    }

    val addIcon: SideMenuIcon = SideMenuIcon().apply {
        border = Border(BorderStroke(Color.DIMGRAY, BorderStrokeStyle.DASHED, CornerRadii(4.0), BorderWidths(2.0)))
        children.add(Label("Add"))
        viewOrder = Double.MAX_VALUE
        setOnMouseClicked {
            onAddClicked.invoke()
        }
    }

    init {
        setIcons(listOf())
    }

    fun setIcons(icons: List<SideMenuIcon>) {
        _icons.setAll(icons)
        _icons.add(addIcon)
        _icons.map(SideMenuIcon::size)
                .filterNot(Property<*>::isBound)
                .forEach { it.bind(width) }
    }
}