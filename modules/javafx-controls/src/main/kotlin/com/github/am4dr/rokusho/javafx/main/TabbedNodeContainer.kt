package com.github.am4dr.rokusho.javafx.main

import com.github.am4dr.rokusho.javafx.binding.invoke
import com.github.am4dr.rokusho.javafx.collection.ConcatenatedList
import com.github.am4dr.rokusho.javafx.control.PlusButton
import javafx.beans.binding.Bindings
import javafx.beans.binding.When
import javafx.beans.property.*
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
    val showAddButton: BooleanProperty = SimpleBooleanProperty(true)
    val onAddClicked: ObjectProperty<(() -> Unit)?> = SimpleObjectProperty()
    val emptyPane: ObjectProperty<Node?> = SimpleObjectProperty()

    private val addViewTab = PlusButton().apply {
        onClicked.set { onAddClicked() }
        visibleProperty().bind(showAddButton)
    }
    private val tabsAndAdd = ConcatenatedList.concat(tabs, observableArrayList<Node>(addViewTab))

    init {
        val fp = HBox().apply {
            setMinSize(Region.USE_COMPUTED_SIZE, Region.USE_PREF_SIZE)
            setMaxSize(Region.USE_COMPUTED_SIZE, Region.USE_PREF_SIZE)
            prefHeightProperty().bind(tabHeight)
            Bindings.bindContent(children, tabsAndAdd)
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