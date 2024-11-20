package com.example.uber.core.RxBus

import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.subjects.PublishSubject
import java.util.concurrent.TimeUnit

object RxBus {

    private val publisher = PublishSubject.create<Any>()

    fun publish(event: Any) {
        publisher.onNext(event)
    }
    fun <T : Any> listen(eventType: Class<T>): Observable<T> = publisher.ofType(eventType)

    fun sendWithDelay(event: Any, delayMillis: Long) {
        Observable.timer(delayMillis, TimeUnit.MILLISECONDS)
            .subscribe {
                publish(event)
            }
    }
}