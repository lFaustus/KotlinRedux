/*
 * Copyright (c) haipham 2019. All rights reserved.
 * Any attempt to reproduce this source code in any form shall be met with legal actions.
 */

package org.swiften.redux.android.ui.lifecycle

import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.OnLifecycleEvent
import org.swiften.redux.core.IReduxSubscription
import org.swiften.redux.ui.IPropContainer
import org.swiften.redux.ui.IPropInjector
import org.swiften.redux.ui.IPropLifecycleOwner
import org.swiften.redux.ui.IPropMapper
import org.swiften.redux.ui.ReduxProps

/** Created by haipham on 2018/12/17 */
/** Callback for use with [LifecycleObserver] */
interface ILifecycleCallback {
  /**
   * This method will be called when it is safe to perform lifecycle-aware tasks, such as
   * [IPropInjector.inject].
   */
  fun onSafeForStartingLifecycleAwareTasks()

  /**
   * This method will be called when it is safe to terminate lifecycle-aware tasks, such as
   * [IReduxSubscription.unsubscribe].
   */
  fun onSafeForEndingLifecycleAwareTasks()
}

/** Use this [LifecycleObserver] to unsubscribe from a [IReduxSubscription] */
@Suppress("unused")
open class ReduxLifecycleObserver(
  private val lifecycleOwner: LifecycleOwner,
  private val callback: ILifecycleCallback
) : LifecycleObserver, ILifecycleCallback {
  init { this.lifecycleOwner.lifecycle.addObserver(this) }

  @OnLifecycleEvent(Lifecycle.Event.ON_CREATE)
  open fun onCreate() {}

  @OnLifecycleEvent(Lifecycle.Event.ON_START)
  fun onStart() {
    this.onSafeForStartingLifecycleAwareTasks()
  }

  @OnLifecycleEvent(Lifecycle.Event.ON_RESUME)
  open fun onResume() {}

  @OnLifecycleEvent(Lifecycle.Event.ON_PAUSE)
  open fun onPause() {}

  @OnLifecycleEvent(Lifecycle.Event.ON_STOP)
  fun onStop() {
    this.onSafeForEndingLifecycleAwareTasks()
  }

  @OnLifecycleEvent(Lifecycle.Event.ON_DESTROY)
  open fun onDestroy() {}

  override fun onSafeForStartingLifecycleAwareTasks() {
    this.callback.onSafeForStartingLifecycleAwareTasks()
    this.lifecycleOwner.lifecycle.addObserver(this)
  }

  override fun onSafeForEndingLifecycleAwareTasks() {
    this.callback.onSafeForEndingLifecycleAwareTasks()
    this.lifecycleOwner.lifecycle.removeObserver(this)
  }

  override fun toString() = this.callback.toString()
}

/** Call [IPropInjector.inject] for [lifecycleOwner] */
fun <GState, GExt, LC, OP, S, A> IPropInjector<GState, GExt>.injectLifecycle(
  lifecycleOwner: LC,
  outProps: OP,
  mapper: IPropMapper<GState, GExt, OP, S, A>
): LC where
  GState : Any,
  GExt : Any,
  LC : LifecycleOwner,
  LC : IPropContainer<S, A>,
  LC : IPropLifecycleOwner<GState, GExt> {
  var subscription: IReduxSubscription? = null

  /**
   * We perform [IPropInjector.inject] in [ReduxLifecycleObserver.onStart] because by then
   * the views would have been initialized, and thus can be accessed in
   * [IPropLifecycleOwner.beforePropInjectionStarts]. To mirror this, unsubscription is done
   * in [ReduxLifecycleObserver.onStop] because said views are not destroyed yet.
   */
  ReduxLifecycleObserver(lifecycleOwner, object : ILifecycleCallback {
    override fun onSafeForStartingLifecycleAwareTasks() {
      subscription = inject(object :
        IPropContainer<S, A> by lifecycleOwner,
        IPropLifecycleOwner<GState, GExt> by lifecycleOwner {
        override var reduxProps: ReduxProps<S, A>
          get() = lifecycleOwner.reduxProps

          /**
           * If [Lifecycle.getCurrentState] is [Lifecycle.State.DESTROYED], do not set
           * [IPropContainer.reduxProps] since there's no point in doing so.
           */
          set(value) = lifecycleOwner.lifecycle.currentState
            .takeUnless { it == Lifecycle.State.DESTROYED }
            .let { lifecycleOwner.reduxProps = value }

        override fun toString() = lifecycleOwner.toString()
      }, outProps, mapper)
    }

    override fun onSafeForEndingLifecycleAwareTasks() { subscription?.unsubscribe() }
  })

  return lifecycleOwner
}

/** Call [IPropInjector.inject] for [lifecycleOwner] but it also implements [IPropMapper] */
fun <GState, GExt, LC, OP, S, A> IPropInjector<GState, GExt>.injectLifecycle(
  lifecycleOwner: LC,
  outProps: OP
): LC where
  GState : Any,
  GExt : Any,
  LC : LifecycleOwner,
  LC : IPropContainer<S, A>,
  LC : IPropLifecycleOwner<GState, GExt>,
  LC : IPropMapper<GState, GExt, OP, S, A> {
  return this.injectLifecycle(lifecycleOwner, outProps, lifecycleOwner)
}
