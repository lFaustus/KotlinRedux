/*
 * Copyright (c) haipham 2019. All rights reserved.
 * Any attempt to reproduce this source code in any form shall be met with legal actions.
 */

package org.swiften.redux.saga

/** Created by haipham on 2019/01/13 */
/** [ISagaEffect] whose [ISagaOutput] retries [times] if a [Throwable] is encountered */
internal class RetryEffect<R>(
  private val source: ISagaEffect<R>,
  private val times: Long
) : SagaEffect<R>() {
  override fun invoke(p1: SagaInput) = this.source.invoke(p1).retry(this.times)
}

/** Invoke a [RetryEffect] on [this] */
fun <R> SagaEffect<R>.retry(times: Long) = this.transform(CommonEffects.retry(times))
fun <R> SagaEffect<R>.retry(times: Int) = this.retry(times.toLong())
