package com.github.am4dr.rokusho.presenter

interface ItemTagData {

    val name: String

    operator fun get(key: String): String?
}
