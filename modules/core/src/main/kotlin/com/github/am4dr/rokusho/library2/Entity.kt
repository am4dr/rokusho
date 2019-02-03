package com.github.am4dr.rokusho.library2


interface Entity<T : Entity<T>> {

    fun isSameEntity(other: T): Boolean
}

fun <T : Entity<T>> MutableSet<T>.putOrReplaceEntity(new: T): T? {
    removeIf { new.isSameEntity(it) }
    add(new)
    return new
}