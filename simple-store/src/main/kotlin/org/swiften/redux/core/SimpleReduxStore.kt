/*
 * Copyright (c) haipham 2018. All rights reserved.
 * Any attempt to reproduce this source code in any form shall be met with legal actions.
 */

package org.swiften.redux.core

import java.util.concurrent.locks.ReentrantReadWriteLock
import kotlin.concurrent.read
import kotlin.concurrent.write

/**
 * Created by haipham on 2018/12/16.
 */
/**
 * [SimpleReduxStore] is a store implementation that supports thread-safe
 * accesses and modifications. Pass in the initial [state] and the store's
 * [reducer] in the constructor.
 */
class SimpleReduxStore<State>(
  internal var state: State,
  internal val reducer: Redux.IReducer<State>
): Redux.IStore<State> {
  private val lock = ReentrantReadWriteLock()
  private val subscribers = HashMap<String, (State) -> Unit>()

  override val lastState = object : Redux.ILastState<State> {
    override operator fun invoke(): State {
      return this@SimpleReduxStore.lock.read { this@SimpleReduxStore.state }
    }
  }

  override var dispatch = object : Redux.IDispatcher {
    override operator fun invoke(action: Redux.IAction) {
      this@SimpleReduxStore.lock.write {
        this@SimpleReduxStore.state =
          this@SimpleReduxStore.reducer(this@SimpleReduxStore.state, action)
      }

      return this@SimpleReduxStore.lock.read {
        this@SimpleReduxStore.subscribers.forEach {
          it.value(this@SimpleReduxStore.state)
        }
      }
    }
  }

  override val subscribe = object : Redux.ISubscribe<State> {
    override operator fun invoke(
      subscriberId: String,
      callback: (State) -> Unit
    ): Redux.Subscription {
      this@SimpleReduxStore.lock.write {
        this@SimpleReduxStore.subscribers[subscriberId] = callback
      }

      /**
       * Relay the last [State] to this subscriber.
       */
      this@SimpleReduxStore.lock.read { callback(this@SimpleReduxStore.state) }

      return Redux.Subscription {
        this@SimpleReduxStore.lock.write {
          this@SimpleReduxStore.subscribers.remove(subscriberId)
        }
      }
    }
  }
}