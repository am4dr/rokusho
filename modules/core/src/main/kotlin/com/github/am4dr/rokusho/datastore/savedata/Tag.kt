package com.github.am4dr.rokusho.datastore.savedata

data class Tag(
    val id: String,
    val type: Type,
    val data: Map<String, Any>) {

    enum class Type {
        TEXT, VALUE, SELECTION, OTHERS;
        companion object {
            fun from(string: String): Type =
                    when (string) {
                        "text" -> TEXT
                        "value" -> VALUE
                        "selection" -> SELECTION
                        else -> OTHERS
                    }
        }
    }
}
