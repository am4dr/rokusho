package com.github.am4dr.image.tagger.app

import javafx.application.Application
import javafx.stage.Stage
import org.apache.commons.cli.CommandLine
import org.apache.commons.cli.DefaultParser
import org.apache.commons.cli.Options
import org.slf4j.LoggerFactory

fun main(args: Array<String>) = Application.launch(Main::class.java, *args)

/*
    アプリケーションを構築するためのクラス
    - JavaFXコンテキストの生成
    - メインとなるフレームの作成
    - 構成要素の生成と相互接続
 */
class Main : Application() {
    private val log = LoggerFactory.getLogger(this.javaClass)
    private val options = makeOptions()

    override fun init() {
        log.info("launched with the params: ${parameters.raw}")
    }
    override fun start(stage: Stage) {
        MainFrame(stage, parseArgs(parameters.raw.toTypedArray())).show()
    }
    private fun parseArgs(args: Array<String>): CommandLine  = DefaultParser().parse(options, args)
    private fun makeOptions(): Options = with(Options()) {
        addOption(null, "saveto", true, "specify the directory path to save the tag file")
    }
}
