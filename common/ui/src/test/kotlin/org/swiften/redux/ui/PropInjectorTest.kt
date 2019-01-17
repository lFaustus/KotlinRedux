/*
 * Copyright (c) haipham 2018. All rights reserved.
 * Any attempt to reproduce this source code in any form shall be met with legal actions.
 */

package org.swiften.redux.ui

import org.junit.Assert
import org.junit.Before
import org.junit.Test
import org.swiften.redux.core.IReduxAction
import org.swiften.redux.core.IReduxDispatcher
import org.swiften.redux.core.IReduxStore
import org.swiften.redux.core.IReduxSubscriber
import org.swiften.redux.core.ReduxSubscription
import org.swiften.redux.store.SimpleReduxStore

/** Created by haipham on 2018/12/20 */
class PropInjectorTest {
  data class S(val query: String = "")
  class A

  sealed class Action : IReduxAction {
    data class SetQuery(val query: String) : Action()
  }

  class StoreWrapper(val store: IReduxStore<S>) : IReduxStore<S> by store {
    var unsubscribeCount: Int = 0

    override val subscribe = object : IReduxSubscriber<S> {
      override operator fun invoke(
        subscriberId: String,
        callback: Function1<S, Unit>
      ): ReduxSubscription {
        val sub = this@StoreWrapper.store.subscribe(subscriberId, callback)

        return ReduxSubscription {
          this@StoreWrapper.unsubscribeCount += 1
          sub.unsubscribe()
        }
      }
    }
  }

  class View : IReduxPropContainer<S, S, A> {
    override var reduxProps by ObservableReduxProps<S, S, A> { _, _ ->
      this.reduxPropsInjectionCount += 1
    }

    var reduxPropsInjectionCount = 0
    var beforeInjectionCount = 0
    var afterInjectionCount = 0

    override fun beforePropInjectionStarts() {
      Assert.assertNotNull(this.reduxProps)
      this.beforeInjectionCount += 1
    }

    override fun afterPropInjectionEnds() { this.afterInjectionCount += 1 }
  }

  private lateinit var store: StoreWrapper
  private lateinit var injector: IReduxPropInjector<S>
  private lateinit var mapper: IReduxPropMapper<S, Unit, S, A>

  @Before
  fun beforeMethod() {
    val store = SimpleReduxStore(S()) { s, a ->
      when (a) {
        is Action -> when (a) {
          is Action.SetQuery -> s.copy(query = a.query)
        }
        else -> s
      }
    }

    this.store = StoreWrapper(store)
    this.injector = ReduxPropInjector(this.store)

    this.mapper = object : IReduxPropMapper<S, Unit, S, A> {
      override fun mapState(state: S, outProps: Unit) = state
      override fun mapAction(dispatch: IReduxDispatcher, state: S, outProps: Unit) = A()
    }
  }

  @Test
  fun `Injecting same state props - should not trigger set event`() {
    // Setup
    val view = View()

    // When
    this.injector.injectProps(view, Unit, this.mapper)
    this.store.dispatch(Action.SetQuery("1"))
    this.store.dispatch(Action.SetQuery("1"))
    this.store.dispatch(Action.SetQuery("2"))
    this.store.dispatch(Action.SetQuery("2"))
    this.store.dispatch(Action.SetQuery("3"))
    this.store.dispatch(Action.SetQuery("3"))
    this.injector.injectProps(view, Unit, this.mapper)

    // Then
    Assert.assertEquals(this.store.unsubscribeCount, 1)
    Assert.assertEquals(view.reduxPropsInjectionCount, 4)
    Assert.assertEquals(view.beforeInjectionCount, 2)
    Assert.assertEquals(view.afterInjectionCount, 1)
  }
}
