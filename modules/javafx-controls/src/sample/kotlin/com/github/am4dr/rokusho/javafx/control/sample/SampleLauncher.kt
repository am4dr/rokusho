package com.github.am4dr.rokusho.javafx.control.sample

import javafx.application.Application

class SampleLauncher {

    companion object {
        @JvmStatic
        fun main(args: Array<String>) = Application.launch(GUISamples::class.java, *args)
    }
}