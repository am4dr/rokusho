package com.github.am4dr.rokusho.library.internal

import java.util.concurrent.locks.ReentrantReadWriteLock
import kotlin.concurrent.read
import kotlin.concurrent.write

/**
 * 変更可能な内部データにアクセスする際に確実にロックするためのクラス
 */
internal class DataLocker<E>(val data: E) {
    private val lock = ReentrantReadWriteLock()

    inline fun <R> read(block: (E) -> R): R = lock.read { block(data) }
    inline fun <R> write(block: (E) -> R): R = lock.write { block(data) }
}
