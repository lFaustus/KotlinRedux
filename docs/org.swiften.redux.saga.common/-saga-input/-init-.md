[docs](../../index.md) / [org.swiften.redux.saga.common](../index.md) / [SagaInput](index.md) / [&lt;init&gt;](./-init-.md)

# &lt;init&gt;

`SagaInput(context: <ERROR CLASS> = EmptyCoroutineContext, dispatch: `[`IActionDispatcher`](../../org.swiften.redux.core/-i-action-dispatcher.md)` = NoopActionDispatcher, lastState: `[`IStateGetter`](../../org.swiften.redux.core/-i-state-getter.md)`<*> = {}, monitor: `[`ISagaMonitor`](../-i-saga-monitor/index.md)` = SagaMonitor(), scheduler: <ERROR CLASS> = Schedulers.io())`

[SagaInput](index.md) for an [ISagaEffect](../-i-saga-effect.md), which exposes a [IReduxStore](../../org.swiften.redux.core/-i-redux-store.md)'s internal functionalities.

### Parameters

`context` - A [CoroutineScope](#) instance.

`lastState` - See [IReduxStore.lastState](../../org.swiften.redux.core/-i-state-getter-provider/last-state.md).

`dispatch` - See [IReduxStore.dispatch](../../org.swiften.redux.core/-i-dispatcher-provider/dispatch.md).