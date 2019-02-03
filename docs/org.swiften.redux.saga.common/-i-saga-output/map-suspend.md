[docs](../../index.md) / [org.swiften.redux.saga.common](../index.md) / [ISagaOutput](index.md) / [mapSuspend](./map-suspend.md)

# mapSuspend

`abstract fun <T2> mapSuspend(transform: suspend <ERROR CLASS>.(`[`T`](index.md#T)`) -> `[`T2`](map-suspend.md#T2)`): `[`ISagaOutput`](index.md)`<`[`T2`](map-suspend.md#T2)`>` [(source)](https://github.com/protoman92/KotlinRedux/tree/master/common/common-saga/src/main/kotlin/org/swiften/redux/saga/common/CommonSaga.kt#L73)

Map emissions from [T](index.md#T) to [T2](map-suspend.md#T2) with suspending [transform](map-suspend.md#org.swiften.redux.saga.common.ISagaOutput$mapSuspend(kotlin.SuspendFunction2((, org.swiften.redux.saga.common.ISagaOutput.T, org.swiften.redux.saga.common.ISagaOutput.mapSuspend.T2)))/transform)
