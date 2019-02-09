package com.github.am4dr.rokusho.launcher

import javafx.application.Application

class GUILauncher {

    companion object {
        @JvmStatic
        fun main(args: Array<String>) = Application.launch(RokushoGUIApp::class.java, *args)
    }
}
