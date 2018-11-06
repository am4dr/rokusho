package com.github.am4dr.rokusho.javafx.util

import javafx.scene.control.Control

class Dummy : Control() {

    override fun createDefaultSkin(): DummySkin<Dummy> = DummySkin(this)
}