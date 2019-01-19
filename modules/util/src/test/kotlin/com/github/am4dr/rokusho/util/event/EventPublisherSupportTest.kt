package com.github.am4dr.rokusho.util.event

import kotlinx.coroutines.cancel
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.assertIterableEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import java.util.*

internal class EventPublisherSupportTest {

    @DisplayName("EventPublisherSupportのデフォルトコンストラクタによるインスタンスについて")
    class SimpleSubscribeTest {
        lateinit var publisher: EventPublisherSupport<String>

        @BeforeEach
        fun beforeEach() {
            publisher = EventPublisherSupport()
        }

        @AfterEach
        fun afterEach() {
            publisher.publishingContext.cancel()
        }

        @Test
        fun subscribeTest() = runBlocking {
            val messages = (0..10).map { "message: $it" }

            val receivedMessages = Collections.synchronizedList(mutableListOf<String>())
            val subscription = publisher.subscribe { it -> receivedMessages.add(it) }
            subscription.use {
                messages.forEach(publisher::dispatch)
                delay(50)
            }

            assertIterableEquals(messages, receivedMessages)

            publisher.dispatch("after unsubscribed")
            delay(50)
            assertIterableEquals(messages, receivedMessages, "unsubscribe後に配信されたイベントの影響は受けてはいけない")
        }

        @Test
        fun subscribeForTest() = runBlocking {
            val messages = (0..10).map { "message: $it" }

            val receivedMessages = Collections.synchronizedList(mutableListOf<String>())
            val subscription = publisher.subscribeFor(receivedMessages) { message, messageList -> messageList.add(message) }
            System.gc()
            subscription.use {
                messages.forEach(publisher::dispatch)
                delay(50)
            }

            assertIterableEquals(messages, receivedMessages)
        }

        @Test
        @DisplayName("他から参照されていないインスタンスによってsubscribeした場合、自動的に解消される")
        fun subscribeForWeakReferenceTest() = runBlocking {
            val messages = (0..10).map { "message: $it" }

            val receivedMessages = Collections.synchronizedList(mutableListOf<String>())
            val subscription = publisher.subscribeFor(object {}) { message, _ -> receivedMessages.add(message) }
            System.gc()
            subscription.use {
                messages.forEach(publisher::dispatch)
                delay(50)
            }

            assertIterableEquals(listOf<String>(), receivedMessages)
        }
    }
}