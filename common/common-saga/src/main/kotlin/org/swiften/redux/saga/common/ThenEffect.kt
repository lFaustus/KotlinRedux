/*
 * Copyright (c) haipham 2019. All rights reserved.
 * Any attempt to reproduce this source code in any form shall be met with legal actions.
 */

package org.swiften.redux.saga.common

/** Created by haipham on 2018/12/26 */
/**
 * [ISagaEffect] whose [ISagaOutput] enforces ordering for two [ISagaOutput] created by two other
 * [ISagaEffect].
 * @param R The first source emission type.
 * @param R2 The second source emission type.
 * @param R3 The result emission type.
 * @param source1 The first source [ISagaEffect].
 * @param source2 The second source [ISagaEffect].
 * @param combiner Function that combines [R] and [R2] to produce [R3].
 */
internal class ThenEffect<R, R2, R3>(
  private val source1: ISagaEffect<R>,
  private val source2: ISagaEffect<R2>,
  private val combiner: Function2<R, R2, R3>
) : SagaEffect<R3>() where R : Any, R2 : Any, R3 : Any {
  override fun invoke(input: SagaInput): ISagaOutput<R3> {
    return this.source1.invoke(input).flatMap { value1 ->
      this@ThenEffect.source2.invoke(input).map { this@ThenEffect.combiner(value1, it) }
    }
  }
}
