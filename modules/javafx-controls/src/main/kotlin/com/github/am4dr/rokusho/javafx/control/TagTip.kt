package com.github.am4dr.rokusho.javafx.control

import javafx.geometry.Insets
import javafx.scene.control.Labeled
import javafx.scene.control.Skin
import javafx.scene.control.skin.LabeledSkinBase
import javafx.scene.layout.Background
import javafx.scene.layout.BackgroundFill
import javafx.scene.layout.CornerRadii
import javafx.scene.paint.Color

class TagTip : Labeled() {

    override fun createDefaultSkin(): Skin<*> = TagTipSkin(this)

    init {
        background = Background(BackgroundFill(Color.LIGHTGRAY, CornerRadii(3.0), null))
        padding = Insets(0.0, 2.0, 0.0, 2.0)
    }
}

class TagTipSkin(tagTip: TagTip) : LabeledSkinBase<TagTip>(tagTip)