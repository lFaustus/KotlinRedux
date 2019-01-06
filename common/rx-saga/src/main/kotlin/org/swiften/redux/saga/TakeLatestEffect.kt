/*
 * Copyright (c) haipham 2018. All rights reserved.
 * Any attempt to reproduce this source code in any form shall be met with legal actions.
 */

package org.swiften.redux.saga

import org.swiften.redux.core.Redux

/** Created by haipham on 2018/12/23 */
/**
 * [TakeEffect] whose output switches to the latest [Redux.IAction] every time
 * one arrives. This is best used for cases whereby we are only interested in
 * the latest value, such as in an autocomplete search implementation. Contrast
 * this with [TakeEveryEffect]
 */
internal class TakeLatestEffect<State, P, R>(
  extract: Function1<Redux.IAction, P?>,
  block: Function1<P, ReduxSagaEffect<State, R>>,
  options: ReduxSaga.TakeOptions
) : TakeEffect<State, P, R>(extract, block, options) {
  override fun flattenOutput(
    nestedOutput: ReduxSaga.Output<ReduxSaga.Output<R>>
  ) = nestedOutput.switchMap { it }
}
