[docs](../index.md) / [org.swiften.redux.core](index.md) / [applyMiddlewares](./apply-middlewares.md)

# applyMiddlewares

`fun <GlobalState> applyMiddlewares(middlewares: `[`Collection`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.collections/-collection/index.html)`<`[`IMiddleware`](-i-middleware.md)`<`[`GlobalState`](apply-middlewares.md#GlobalState)`>>): (`[`IReduxStore`](-i-redux-store.md)`<`[`GlobalState`](apply-middlewares.md#GlobalState)`>) -> `[`IReduxStore`](-i-redux-store.md)`<`[`GlobalState`](apply-middlewares.md#GlobalState)`>` [(source)](https://github.com/protoman92/KotlinRedux/tree/master/common/common-core/src/main/kotlin/org/swiften/redux/core/Middleware.kt#L79)

Apply [middlewares](apply-middlewares.md#org.swiften.redux.core$applyMiddlewares(kotlin.collections.Collection((kotlin.Function1((org.swiften.redux.core.MiddlewareInput((org.swiften.redux.core.applyMiddlewares.GlobalState)), kotlin.Function1((org.swiften.redux.core.DispatchWrapper, )))))))/middlewares) to a [IReduxStore](-i-redux-store.md) instance to get an enhanced [IReduxStore](-i-redux-store.md)

### Parameters

`GlobalState` - The global state type.

`middlewares` - The [IMiddleware](-i-middleware.md) instances to be applied to an [IReduxStore](-i-redux-store.md).

**Return**
Function that maps an [IReduxStore](-i-redux-store.md) to an [EnhancedReduxStore](-enhanced-redux-store/index.md).

`fun <GlobalState> applyMiddlewares(vararg middlewares: `[`IMiddleware`](-i-middleware.md)`<`[`GlobalState`](apply-middlewares.md#GlobalState)`>): (`[`IReduxStore`](-i-redux-store.md)`<`[`GlobalState`](apply-middlewares.md#GlobalState)`>) -> `[`IReduxStore`](-i-redux-store.md)`<`[`GlobalState`](apply-middlewares.md#GlobalState)`>` [(source)](https://github.com/protoman92/KotlinRedux/tree/master/common/common-core/src/main/kotlin/org/swiften/redux/core/Middleware.kt#L93)

Apply [middlewares](apply-middlewares.md#org.swiften.redux.core$applyMiddlewares(kotlin.Array((kotlin.Function1((org.swiften.redux.core.MiddlewareInput((org.swiften.redux.core.applyMiddlewares.GlobalState)), kotlin.Function1((org.swiften.redux.core.DispatchWrapper, )))))))/middlewares) to a [IReduxStore](-i-redux-store.md) instance. This is a convenience method that uses
varargs.

### Parameters

`GlobalState` - The global state type.

`middlewares` - The [IMiddleware](-i-middleware.md) instances to be applied to an [IReduxStore](-i-redux-store.md).

**Return**
Function that maps an [IReduxStore](-i-redux-store.md) to an [EnhancedReduxStore](-enhanced-redux-store/index.md).
