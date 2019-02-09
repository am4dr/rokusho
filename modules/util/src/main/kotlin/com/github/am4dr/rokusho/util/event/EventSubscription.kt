package com.github.am4dr.rokusho.util.event

interface EventSubscription : AutoCloseable {

    fun unsubscribe()
}