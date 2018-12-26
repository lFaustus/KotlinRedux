/*
 * Copyright (c) haipham 2018. All rights reserved.
 * Any attempt to reproduce this source code in any form shall be met with legal actions.
 */

package org.swiften.redux.saga

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.ExperimentalCoroutinesApi

/**
 * Created by haipham on 2018/12/26.
 */
/**
 * [ReduxSaga.IEffect] whose [ReduxSaga.Output] enforces ordering for two
 * [ReduxSaga.Output] created by two other [ReduxSaga.IEffect].
 */
internal class ThenEffect<State, R, R2, R3>(
  private val source1: ReduxSaga.IEffect<State, R>,
  private val source2: ReduxSaga.IEffect<State, R2>,
  private val selector: Function2<R, R2, R3>
) : ReduxSaga.IEffect<State, R3> {
  @ExperimentalCoroutinesApi
  @Suppress("PARAMETER_NAME_CHANGED_ON_OVERRIDE")
  override fun invoke(input: ReduxSaga.Input<State>) =
    this.source1.invoke(input).flatMap(object : ReduxSaga.Output.IFlatMapper<R, R3> {
      override suspend fun invoke(scope: CoroutineScope, value1: R) =
        this@ThenEffect.source2.invoke(input)
          .map(object : ReduxSaga.Output.IMapper<R2, R3> {
            override suspend fun invoke(scope: CoroutineScope, value2: R2) =
              this@ThenEffect.selector(value1, value2)
          })
    })
}

/** Invoke a [ThenEffect] on the current [ReduxSaga.IEffect] */
fun <State, R, R2, R3> ReduxSaga.IEffect<State, R>.then(
  effect: ReduxSaga.IEffect<State, R2>,
  selector: Function2<R, R2, R3>
) = ReduxSagaEffect.then(this, effect, selector)