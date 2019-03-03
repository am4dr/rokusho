package com.github.am4dr.rokusho.javafx.main

import com.github.am4dr.rokusho.javafx.binding.invoke
import com.github.am4dr.rokusho.javafx.collection.ConcatenatedList
import javafx.beans.binding.Bindings
import javafx.beans.binding.When
import javafx.beans.property.DoubleProperty
import javafx.beans.property.ObjectProperty
import javafx.beans.property.SimpleDoubleProperty
import javafx.beans.property.SimpleObjectProperty
import javafx.collections.FXCollections
import javafx.collections.FXCollections.observableArrayList
import javafx.collections.ObservableList
import javafx.geometry.Insets
import javafx.scene.Node
import javafx.scene.control.ScrollPane
import javafx.scene.layout.*

class TabbedNodeContainer : VBox() {

    val tabHeight: DoubleProperty = SimpleDoubleProperty(30.0)
    val tabs: ObservableList<Node> = FXCollections.observableArrayList()
    private val contentParent = BorderPane()
    val content: ObjectProperty<Node?> = SimpleObjectProperty()
    val onAddClicked: ObjectProperty<(() -> Unit)?> = SimpleObjectProperty()
    val emptyPane: ObjectProperty<Node?> = SimpleObjectProperty()

    init {
        val addViewTab = AddViewButton().apply {
            onClicked.set { onAddClicked() }
        }
        val fp = HBox().apply {
            setMinSize(Region.USE_COMPUTED_SIZE, Region.USE_PREF_SIZE)
            setMaxSize(Region.USE_COMPUTED_SIZE, Region.USE_PREF_SIZE)
            prefHeightProperty().bind(tabHeight)
            Bindings.bindContent(children, ConcatenatedList.concat(tabs, observableArrayList<Node>(addViewTab)))
        }
        val scrollPane = ScrollPane(fp).apply {
            padding = Insets.EMPTY
            minViewportHeightProperty().bind(tabHeight)
            prefViewportHeightProperty().bind(tabHeight)
            hbarPolicy = ScrollPane.ScrollBarPolicy.NEVER
            vbarPolicy = ScrollPane.ScrollBarPolicy.NEVER
            background = Background.EMPTY
        }
        VBox.setVgrow(contentParent, Priority.ALWAYS)
        contentParent.centerProperty().bind(When(content.isNotNull).then(content).otherwise(emptyPane))
        children.addAll(scrollPane, contentParent)
    }
}