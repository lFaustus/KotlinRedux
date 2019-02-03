/*
 * Copyright (c) haipham 2018. All rights reserved.
 * Any attempt to reproduce this source code in any form shall be met with legal actions.
 */

package org.swiften.redux.ui

import org.swiften.redux.core.IActionDispatcher
import org.swiften.redux.core.IDeinitializerProvider
import org.swiften.redux.core.IDispatcherProvider
import org.swiften.redux.core.IReduxStore
import org.swiften.redux.core.IReduxSubscription
import org.swiften.redux.core.IStateGetterProvider
import org.swiften.redux.core.ReduxSubscription
import java.util.UUID
import java.util.concurrent.locks.ReentrantReadWriteLock
import kotlin.concurrent.read
import kotlin.concurrent.write

/** Created by haipham on 2018/12/16 */
/**
 * Handle lifecycles for a target of [IPropInjector].
 * @param GlobalState The global state type.
 * @param GlobalExt The global external argument.
 */
interface IPropLifecycleOwner<GlobalState, GlobalExt> {
  /** This is called before [IPropInjector.inject] is called. */
  fun beforePropInjectionStarts(sp: StaticProps<GlobalState, GlobalExt>)

  /** This is called after [IReduxSubscription.unsubscribe] is called. */
  fun afterPropInjectionEnds()
}

/**
 * Treat this as a delegate for [IPropLifecycleOwner] that does not hold any logic.
 * @param GlobalState The global state type.
 * @param GlobalExt The global external argument.
 */
class EmptyPropLifecycleOwner<GlobalState, GlobalExt> : IPropLifecycleOwner<GlobalState, GlobalExt> {
  override fun beforePropInjectionStarts(sp: StaticProps<GlobalState, GlobalExt>) {}
  override fun afterPropInjectionEnds() {}
}

/**
 * Represents a container for [ReduxProps].
 * @param State The container state.
 * @param Action the container action.
 */
interface IPropContainer<State, Action> {
  var reduxProps: ReduxProps<State, Action>
}

/**
 * Represents static dependencies for [IActionMapper]. [IActionMapper.mapAction] will have access
 * to [IDispatcherProvider.dispatch] and [external].
 * @param GlobalExt The global external argument.
 */
interface IActionDependency<GlobalExt> : IDispatcherProvider {
  val external: GlobalExt
}

/**
 * Maps [GlobalState] to [State] for a [IPropContainer].
 * @param GlobalState The global state type.
 * @param OutProps Property as defined by a view's parent.
 * @param State The container state.
 */
interface IStateMapper<GlobalState, OutProps, State> {
  /**
   * Map [GlobalState] to [State] using [OutProps]
   * @param state The latest [GlobalState] instance.
   * @param outProps The [OutProps] instance.
   * @return A [State] instance.
   */
  fun mapState(state: GlobalState, outProps: OutProps): State
}

/**
 * Maps [IActionDispatcher] and [GlobalState] to [Action] for a [IPropContainer]. Note that [Action]
 * can include external, non-Redux related dependencies provided by [GlobalExt].
 *
 * For example, if the app wants to load an image into a view, it's probably not a good idea to
 * download that image and store in the [GlobalState] to be mapped into [VariableProps.state]. It
 * is better to inject an image downloader in [Action] using [GlobalExt].
 * @param GlobalState The global state type.
 * @param GlobalExt The global external argument.
 * @param OutProps Property as defined by a view's parent.
 * @param Action The container action.
 */
interface IActionMapper<GlobalState, GlobalExt, OutProps, Action> {
  /**
   * Map [IActionDispatcher] to [Action] using [GlobalState], [GlobalExt] and [OutProps]
   * @param static An [IActionDependency] instance.
   * @param state The latest [GlobalState] instance.
   * @param ext The [GlobalExt] instance.
   * @param outProps The [OutProps] instance.
   * @return An [Action] instance.
   */
  fun mapAction(static: IActionDependency<GlobalExt>, state: GlobalState, outProps: OutProps): Action
}

/**
 * Maps [GlobalState] to [State] and [Action] for a [IPropContainer]. [OutProps] is the view's
 * immutable property as dictated by its parent.
 *
 * For example, a parent view, which contains a list of child views, wants to call
 * [IPropInjector.inject] for said children. The [OutProps] generic for these children
 * should therefore be an [Int] that corresponds to their respective indexes in the parent.
 * Generally, though, [OutProps] tends to be [Unit].
 *
 * @param GlobalState The global state type.
 * @param GlobalExt The global external argument.
 * @param OutProps Property as defined by a view's parent.
 * @param State The container state.
 * @param Action The container action.
 */
interface IPropMapper<GlobalState, GlobalExt, OutProps, State, Action> :
  IStateMapper<GlobalState, OutProps, State>,
  IActionMapper<GlobalState, GlobalExt, OutProps, Action>

/**
 * Inject state and actions into an [IPropContainer].
 * @param GlobalState The global state type.
 * @param GlobalExt The global external argument.
 */
interface IPropInjector<GlobalState, GlobalExt> :
  IActionDependency<GlobalExt>,
  IStateGetterProvider<GlobalState>,
  IDeinitializerProvider {
  /**
   * Inject [State] and [Action] into [view]. This method does not handle lifecycles, so
   * platform-specific methods can be defined for this purpose.
   * @param OutProps Property as defined by a view's parent.
   * @param State The container state.
   * @param Action The container action.
   * @param view An [IPropContainer] instance.
   * @param outProps An [OutProps] instance.
   * @param mapper An [IPropMapper] instance.
   * @return An [IReduxSubscription] instance.
   */
  fun <OutProps, View, State, Action> inject(
    view: View,
    outProps: OutProps,
    mapper: IPropMapper<GlobalState, GlobalExt, OutProps, State, Action>
  ): IReduxSubscription where
    View : IPropContainer<State, Action>,
    View : IPropLifecycleOwner<GlobalState, GlobalExt>
}

/**
 * A [IPropInjector] implementation that handles [inject] in a thread-safe manner. It
 * also invokes [IPropLifecycleOwner.beforePropInjectionStarts] and
 * [IPropLifecycleOwner.afterPropInjectionEnds] when appropriate.
 * @param GlobalState The global state type.
 * @param GlobalExt The global external argument.
 * @param store An [IReduxStore] instance.
 */
open class PropInjector<GlobalState, GlobalExt>(
  private val store: IReduxStore<GlobalState>,
  override val external: GlobalExt
) :
  IPropInjector<GlobalState, GlobalExt>,
  IDispatcherProvider by store,
  IStateGetterProvider<GlobalState> by store,
  IDeinitializerProvider by store {
  override fun <OutProps, View, State, Action> inject(
    view: View,
    outProps: OutProps,
    mapper: IPropMapper<GlobalState, GlobalExt, OutProps, State, Action>
  ): IReduxSubscription where
    View : IPropContainer<State, Action>,
    View : IPropLifecycleOwner<GlobalState, GlobalExt> {
    /**
     * It does not matter what the id is, as long as it is unique. This is because we will be
     * passing along a [ReduxSubscription] to handle unsubscribe, so there's no need to keep
     * track of this id.
     */
    val subscriberId = "${view}${UUID.randomUUID()}"

    /** If [view] has received an injection before, unsubscribe from that */
    view.unsubscribeSafely()

    /**
     * Since [IReduxStore.subscribe] has not been called yet, we pass in a placebo
     * [ReduxSubscription], but [StaticProps.injector] is still available for
     * [IPropLifecycleOwner.beforePropInjectionStarts]
     */
    val staticProps = StaticProps(this, ReduxSubscription(subscriberId) {})

    /**
     * Inject [StaticProps] with a placebo [StaticProps.subscription] because we want
     * [ReduxProps.s] to be available in [IPropLifecycleOwner.beforePropInjectionStarts]
     * in case [view] needs to perform [inject] on its children.
     *
     * Beware that accessing [IPropContainer.reduxProps] before this point will probably throw
     * a [Throwable], most notably [UninitializedPropertyAccessException] if [view] uses
     * [ObservableReduxProps] as delegate property.
     */
    view.reduxProps = ReduxProps(staticProps.subscription, null)
    view.beforePropInjectionStarts(staticProps)
    val lock = ReentrantReadWriteLock()
    var previousState: State? = null

    val onStateUpdate: (GlobalState) -> Unit = {
      val next = mapper.mapState(it, outProps)
      val prev = lock.read { previousState }

      lock.write {
        previousState = next

        if (next != prev) {
          val actions = mapper.mapAction(this, it, outProps)
          view.reduxProps = view.reduxProps.copy(v = VariableProps(next, actions))
        }
      }
    }

    /**
     * Immediately set [IPropContainer.reduxProps] based on [store]'s last [GlobalState], in case
     * this [store] does not relay last [GlobalState] on subscription.
     */
    onStateUpdate(this.store.lastState())
    val subscription = this.store.subscribe(subscriberId, onStateUpdate)

    /** Wrap a [ReduxSubscription] to perform [IPropLifecycleOwner.afterPropInjectionEnds] */
    val wrappedSub = ReduxSubscription(subscription.id) {
      subscription.unsubscribe()
      view.afterPropInjectionEnds()
    }

    view.reduxProps = view.reduxProps.copy(wrappedSub)
    return wrappedSub
  }
}

/**
 * Unsubscribe from [IPropContainer.reduxProps] safely, i.e. catch
 * [UninitializedPropertyAccessException] because this is most probably declared as lateinit in
 * Kotlin code, and catch [NullPointerException] to satisfy Java code. Also return the
 * [ReduxSubscription.id] that can be used to track and remove the relevant [ReduxSubscription]
 * from other containers.
 * @return The [IReduxSubscription.id].
 */
fun IPropContainer<*, *>.unsubscribeSafely(): String? {
  try {
    val subscription = this.reduxProps.s
    subscription.unsubscribe()
    return subscription.id
  } catch (e: UninitializedPropertyAccessException) {
  } catch (e: NullPointerException) { }

  return null
}
