package com.github.am4dr.rokusho.gui.util

import javafx.scene.control.Control

class Dummy : Control() {

    override fun createDefaultSkin(): DummySkin<Dummy> = DummySkin(this)
}