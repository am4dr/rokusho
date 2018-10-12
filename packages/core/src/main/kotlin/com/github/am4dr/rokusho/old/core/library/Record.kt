package com.github.am4dr.rokusho.old.core.library

data class Record<out T>(val key: T, val itemTags: List<ItemTag>)