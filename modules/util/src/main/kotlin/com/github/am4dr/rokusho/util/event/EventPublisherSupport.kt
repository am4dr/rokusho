package com.github.am4dr.rokusho.util.event

import com.github.am4dr.rokusho.util.log.getLogger
import com.github.am4dr.rokusho.util.log.idHash
import kotlinx.coroutines.*
import kotlinx.coroutines.channels.BroadcastChannel
import kotlinx.coroutines.channels.ClosedReceiveChannelException
import kotlin.coroutines.CoroutineContext


class EventPublisherSupport<E>(
    val publishingContext: CoroutineContext = Dispatchers.Default
) : EventPublisher<E>, CoroutineScope {

    companion object {
        private val log = getLogger<EventPublisherSupport<*>>()
    }

    @ExperimentalCoroutinesApi
    private val channel: BroadcastChannel<E> = BroadcastChannel(100) // 根拠なく適当に決められた値です

    override val coroutineContext: CoroutineContext
        get() = publishingContext

    @ExperimentalCoroutinesApi
    override fun subscribe(block: suspend (E, EventSubscription) -> Unit): EventSubscription {
        val receiveChannel = channel.openSubscription()
        val subscription = object : EventSubscription {
            override fun unsubscribe() {
                receiveChannel.cancel()
                log.info("${this@EventPublisherSupport.idHash} is unsubscribed by $idHash")
            }
            override fun close() {
                unsubscribe()
            }
        }
        launch {
            while (isActive && !receiveChannel.isClosedForReceive) {
                try {
                    val event = receiveChannel.receive() // TODO receiveOrClosedで置き換え
                    block(event, subscription)
                } catch (e: ClosedReceiveChannelException) {
                    break
                }
            }
        }
        log.info("$idHash is subscribed by ${subscription.idHash}")
        return subscription
    }

    @ExperimentalCoroutinesApi
    fun dispatch(event: E) {
        channel.offer(event)
    }
}