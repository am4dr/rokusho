package com.github.am4dr.rokusho.library2

import com.github.am4dr.rokusho.util.event.EventSubscription
import java.util.concurrent.locks.ReentrantReadWriteLock
import kotlin.concurrent.read
import kotlin.concurrent.write

// TODO 不要かも。これがどのクラスの実装の切り出しなのかを考える。さもなくば適切につくれない
class AutoSaveSupport {

    private var subscription: EventSubscription? = null
    private val lock = ReentrantReadWriteLock()

    fun isActive(): Boolean =
        lock.read { subscription != null }

    fun activate() = lock.write {
        TODO()
        subscription
    }

    fun inactivate() = lock.write {
        TODO()
        subscription = null
    }
}