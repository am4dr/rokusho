package com.github.am4dr.rokusho.util.event

import java.lang.ref.WeakReference

interface EventPublisher<E> {

    fun subscribe(block: suspend (E, EventSubscription) -> Unit): EventSubscription

    fun subscribe(block: suspend (E) -> Unit): EventSubscription =
        subscribe { event, _ -> block(event) }

    /**
     * 通知されるイベントを反映する対象が存在しなくなった際に自動的に[EventSubscription.unsubscribe]が実行される[EventSubscription]を作成する
     * イベントが発生するたびに[targetSupplier]を実行し、戻り値が null のときに対象が存在しなくなったとみなす
     */
    fun <T> subscribe(targetSupplier: () -> T?, block: suspend (E, T, EventSubscription) -> Unit): EventSubscription =
        subscribe block@{ event, subscription ->
            val target = targetSupplier() ?: return@block subscription.unsubscribe()
            block(event, target, subscription)
        }

    fun <T> subscribe(targetSupplier: () -> T?, block: suspend (E, T) -> Unit): EventSubscription =
        subscribe(targetSupplier) { event, target, _ -> block(event, target) }

    /**
     * イベントの反映対象となるインスタンスがガベージコレクタによって解放されることで対象が存在しなくなったとみなす[subscribe]
     * 対象が解放されなくなってしまうので[block]の中では反映対象をラムダ式のパラメータ経由で参照するようにし、直接参照しないように気を付ける必要がある
     * 内部的には[java.lang.ref.WeakReference]を利用している
     */
    fun <T> subscribeFor(instance: T, block: suspend (E, T, EventSubscription) -> Unit): EventSubscription =
        subscribe(WeakReference(instance)::get, block)

    fun <T> subscribeFor(instance: T, block: suspend (E, T) -> Unit): EventSubscription =
        subscribeFor(instance) { event, target, _ -> block(event, target) }
}
