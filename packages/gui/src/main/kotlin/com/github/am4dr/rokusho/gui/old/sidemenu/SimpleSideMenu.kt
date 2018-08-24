package com.github.am4dr.rokusho.gui.old.sidemenu

import javafx.beans.InvalidationListener
import javafx.beans.binding.Bindings
import javafx.beans.property.DoubleProperty
import javafx.beans.property.ObjectProperty
import javafx.beans.property.SimpleDoubleProperty
import javafx.beans.property.SimpleObjectProperty
import javafx.collections.FXCollections
import javafx.collections.ObservableList
import javafx.geometry.Pos
import javafx.scene.control.Label
import javafx.scene.control.ScrollPane
import javafx.scene.layout.*
import javafx.scene.paint.Color

class SimpleSideMenu : VBox() {

    val icons: ObservableList<SideMenuIcon> = FXCollections.observableArrayList()
    val width: DoubleProperty = SimpleDoubleProperty(20.0)
    val onAddClicked: ObjectProperty<()->Unit> = SimpleObjectProperty {}

    private val addIcon: SideMenuIcon = SideMenuIcon().apply {
        size.bind(this@SimpleSideMenu.width)
        border = Border(BorderStroke(Color.DIMGRAY, BorderStrokeStyle.DASHED, CornerRadii(4.0), BorderWidths(2.0)))
        children.add(Label("Add"))
        viewOrder = Double.MAX_VALUE
        setOnMouseClicked {
            onAddClicked.get()?.invoke()
        }
    }

    private val iconPane = VBox().apply {
        Bindings.bindContent(children, icons)
    }

    private val iconScrollPane = ScrollPane().apply {
        minHeight = 0.0
        content = iconPane
        hbarPolicy = ScrollPane.ScrollBarPolicy.NEVER
        vbarPolicy = ScrollPane.ScrollBarPolicy.NEVER
        background = Background(BackgroundFill(Color.TRANSPARENT, null, null))
    }

    init {
        updateIconWidthsBindings()
        icons.addListener(InvalidationListener { updateIconWidthsBindings() })
        alignment = Pos.TOP_CENTER
        children.addAll(iconScrollPane, addIcon)
    }

    private fun updateIconWidthsBindings() {
        icons.map(SideMenuIcon::size).forEach { it.bind(width) }
    }
}