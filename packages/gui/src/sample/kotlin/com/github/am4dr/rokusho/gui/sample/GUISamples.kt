package com.github.am4dr.rokusho.gui.sample

import com.github.am4dr.javafx.sample_viewer.UpdateAwareNode
import com.github.am4dr.javafx.sample_viewer.ui.SampleApplicationSupport
import com.github.am4dr.javafx.sample_viewer.ui.SampleCollection
import com.github.am4dr.javafx.sample_viewer.ui.SampleCollectionViewer
import com.github.am4dr.javafx.sample_viewer.ui.StatusBorderedPane
import javafx.scene.Node
import javafx.scene.Parent
import javafx.scene.Scene
import javafx.stage.Stage

class GUISamples : SampleApplicationSupport() {

    lateinit var samples: SampleCollection
    lateinit var viewer: SampleCollectionViewer

    override fun start(stage: Stage) {
        samples = SampleCollection()
        viewer = SampleCollectionViewer(samples)
        stage.apply {
            title = "Rokusho GUI samples"
            scene = Scene(createView(), 600.0, 400.0)
        }.show()
    }

    private val ftStates = FittingTextFieldSample.createStates()
    private val ttStates = TagTipSample.createStates()
    private fun createView(): Parent {
        addSample<SampleSample>("sample")
        addSample<FittingTextFieldSample>(ftStates)
        addSample<TagTipSample>(ttStates)
        return viewer.view
    }

    private inline fun <reified T : Node> addSample(context: Map<String, Any> = mapOf()) {
        addSample<T>(T::class.java.simpleName, context)
    }
    private inline fun <reified T : Node> addSample(title: String, context: Map<String, Any> = mapOf()) {
        val node = UpdateAwareNode.build<T> { b ->
            b.type(T::class.java)
                    .classloader { createWatcher() }
                    .context(context)
        }
        val statusBorderedPane = StatusBorderedPane(node)
        samples.addSample(title, statusBorderedPane)
    }
}