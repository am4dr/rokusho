package com.github.am4dr.rokusho.gui.scene

import com.github.am4dr.rokusho.javafx.collection.TransformedList
import javafx.beans.binding.Bindings
import javafx.beans.binding.Bindings.bindContent
import javafx.beans.binding.Bindings.createObjectBinding
import javafx.beans.property.LongProperty
import javafx.beans.property.ReadOnlyObjectWrapper
import javafx.beans.property.ReadOnlyStringProperty
import javafx.beans.property.SimpleLongProperty
import javafx.collections.FXCollections
import javafx.collections.ListChangeListener
import javafx.collections.ObservableList
import javafx.geometry.Pos
import javafx.scene.Node
import javafx.scene.control.Button
import javafx.scene.control.ContentDisplay
import javafx.scene.control.Label
import javafx.scene.control.TextField
import javafx.scene.layout.*
import java.util.concurrent.Callable

class ViewSelectorPaneWithSearchBox : VBox() {

    val selections: ObservableList<Selection> = FXCollections.observableArrayList()

    private val passedCount = SimpleLongProperty(0)
    private val totalCount = SimpleLongProperty(0)
    fun filterPassedCountProperty(): LongProperty = passedCount
    fun totalCountProperty(): LongProperty = totalCount

    fun filterTextProperty(): ReadOnlyStringProperty = filterInputNode.textProperty()


    private val filterInputNode = TextField()
    private val buttons = TransformedList(selections) { Button(it.label).apply { setOnAction { _ -> it.select() } } }

    private val selected = ReadOnlyObjectWrapper<Selection>()

    init {
        val buttonsPane = HBox().apply { bindContent(children, buttons) }
        val filterPane = HBox(
                Label("フィルター", filterInputNode).apply { contentDisplay = ContentDisplay.RIGHT },
                Label().apply { textProperty().bind(Bindings.concat("[", passedCount,  " / ", totalCount, "]")) }
        ).apply { alignment = Pos.BASELINE_LEFT }
        val topPanePadding = Pane().apply {
            prefWidth = 0.0
            HBox.setHgrow(this, Priority.ALWAYS)
        }
        val contentPane = BorderPane().apply {
            VBox.setVgrow(this, Priority.ALWAYS)
            centerProperty().bind(createObjectBinding(Callable { selected.get()?.node }, selected))
        }
        children.addAll(
                HBox(buttonsPane, topPanePadding, filterPane),
                contentPane
        )

        selections.addListener(ListChangeListener { if (it.list.size == 1) { select(0) } })
    }

    private fun select(n: Int) = selections.getOrNull(n)?.select()

    private fun Selection.select() = selected.set(this)


    class Selection(val label: String, val node: Node)
}