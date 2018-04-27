package com.github.am4dr.rokusho.app.savedata.yaml

enum class Versions(s: String) {

    V1("1"), UNKNOWN("unknown");

    val string: String = s

    companion object {
        val CURRENT: Versions = V1
        fun getOrUnknown(string: String): Versions = values().find { it.string == string } ?: UNKNOWN
    }
}
