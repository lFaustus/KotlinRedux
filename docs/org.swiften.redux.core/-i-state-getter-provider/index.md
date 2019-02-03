[docs](../../index.md) / [org.swiften.redux.core](../index.md) / [IStateGetterProvider](./index.md)

# IStateGetterProvider

`interface IStateGetterProvider<GlobalState>` [(source)](https://github.com/protoman92/KotlinRedux/tree/master/common/common-core/src/main/kotlin/org/swiften/redux/core/Core.kt#L55)

Represents an object that provides [IStateGetter](../-i-state-getter.md).

### Parameters

`GlobalState` - The global state type.

### Properties

| Name | Summary |
|---|---|
| [lastState](last-state.md) | `abstract val lastState: `[`IStateGetter`](../-i-state-getter.md)`<`[`GlobalState`](index.md#GlobalState)`>` |

### Inheritors

| Name | Summary |
|---|---|
| [IPropInjector](../../org.swiften.redux.ui/-i-prop-injector/index.md) | `interface IPropInjector<GlobalState> : `[`IDispatcherProvider`](../-i-dispatcher-provider/index.md)`, `[`IStateGetterProvider`](./index.md)`<`[`GlobalState`](../../org.swiften.redux.ui/-i-prop-injector/index.md#GlobalState)`>, `[`IDeinitializerProvider`](../-i-deinitializer-provider/index.md)<br>Inject state and actions into an [IPropContainer](../../org.swiften.redux.ui/-i-prop-container/index.md). |
| [IReduxStore](../-i-redux-store.md) | `interface IReduxStore<GlobalState> : `[`IReducerProvider`](../-i-reducer-provider/index.md)`<`[`GlobalState`](../-i-redux-store.md#GlobalState)`>, `[`IDispatcherProvider`](../-i-dispatcher-provider/index.md)`, `[`IStateGetterProvider`](./index.md)`<`[`GlobalState`](../-i-redux-store.md#GlobalState)`>, `[`IReduxSubscriberProvider`](../-i-redux-subscriber-provider/index.md)`<`[`GlobalState`](../-i-redux-store.md#GlobalState)`>, `[`IDeinitializerProvider`](../-i-deinitializer-provider/index.md)<br>Represents a Redux store that can dispatch [IReduxAction](../-i-redux-action.md) with a [IActionDispatcher](../-i-action-dispatcher.md) to mutate some internal [GlobalState](../-i-redux-store.md#GlobalState). Other objects can subscribe to [GlobalState](../-i-redux-store.md#GlobalState) updates using [subscribe](../-i-redux-subscriber-provider/subscribe.md). |
| [PropInjector](../../org.swiften.redux.ui/-prop-injector/index.md) | `open class PropInjector<GlobalState> : `[`IPropInjector`](../../org.swiften.redux.ui/-i-prop-injector/index.md)`<`[`GlobalState`](../../org.swiften.redux.ui/-prop-injector/index.md#GlobalState)`>, `[`IDispatcherProvider`](../-i-dispatcher-provider/index.md)`, `[`IStateGetterProvider`](./index.md)`<`[`GlobalState`](../../org.swiften.redux.ui/-prop-injector/index.md#GlobalState)`>, `[`IDeinitializerProvider`](../-i-deinitializer-provider/index.md)<br>A [IPropInjector](../../org.swiften.redux.ui/-i-prop-injector/index.md) implementation that handles [inject](../../org.swiften.redux.ui/-prop-injector/inject.md) in a thread-safe manner. It also invokes [IPropLifecycleOwner.beforePropInjectionStarts](../../org.swiften.redux.ui/-i-prop-lifecycle-owner/before-prop-injection-starts.md) and [IPropLifecycleOwner.afterPropInjectionEnds](../../org.swiften.redux.ui/-i-prop-lifecycle-owner/after-prop-injection-ends.md) when appropriate. |