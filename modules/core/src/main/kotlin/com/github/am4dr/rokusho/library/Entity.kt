package com.github.am4dr.rokusho.library


interface Entity<T : Entity<T>> {

    fun isSameEntity(other: T): Boolean

    override fun hashCode(): Int
    override fun equals(other: Any?): Boolean
}

fun <T : Entity<T>> MutableList<T>.addOrReplaceEntity(new: T) {
    for (i in 0..lastIndex) {
        if (get(i).isSameEntity(new)) {
            set(i, new)
        }
    }
}