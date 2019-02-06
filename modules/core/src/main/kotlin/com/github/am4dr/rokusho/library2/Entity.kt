package com.github.am4dr.rokusho.library2


interface Entity<T : Entity<T>> {

    fun isSameEntity(other: T): Boolean
}

fun <T : Entity<T>> MutableSet<T>.putOrReplaceEntity(new: T): T? {
    removeIf { new.isSameEntity(it) }
    add(new)
    return new
}

fun <T : Entity<T>> MutableList<T>.addOrReplaceEntity(new: T) {
    for (i in 0..lastIndex) {
        if (get(i).isSameEntity(new)) {
            set(i, new)
        }
    }
}