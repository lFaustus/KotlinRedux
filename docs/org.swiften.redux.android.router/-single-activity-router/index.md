[docs](../../index.md) / [org.swiften.redux.android.router](../index.md) / [SingleActivityRouter](./index.md)

# SingleActivityRouter

`@PublishedApi internal class SingleActivityRouter<AT, Screen : `[`IRouterScreen`](../../org.swiften.redux.core/-i-router-screen.md)`> : `[`IRouter`](../../org.swiften.redux.core/-i-router/index.md)`<`[`Screen`](index.md#Screen)`>` [(source)](https://github.com/protoman92/KotlinRedux/tree/master/android/android-router/src/main/java/org/swiften/redux/android/router/SingleActivityRouter.kt#L21)

[IRouter](../../org.swiften.redux.core/-i-router/index.md) that works for a single [AppCompatActivity](#) and multiple [Fragment](#)

### Constructors

| Name | Summary |
|---|---|
| [&lt;init&gt;](-init-.md) | `SingleActivityRouter(cls: `[`Class`](http://docs.oracle.com/javase/6/docs/api/java/lang/Class.html)`<`[`AT`](index.md#AT)`>, application: <ERROR CLASS>, runner: `[`IMainThreadRunner`](../../org.swiften.redux.android.util/-android-util/-i-main-thread-runner/index.md)`, navigate: (`[`AT`](index.md#AT)`, `[`Screen`](index.md#Screen)`) -> `[`Unit`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-unit/index.html)`)`<br>[IRouter](../../org.swiften.redux.core/-i-router/index.md) that works for a single [AppCompatActivity](#) and multiple [Fragment](#) |

### Properties

| Name | Summary |
|---|---|
| [activity](activity.md) | `var activity: `[`AT`](index.md#AT)`?` |
| [application](application.md) | `val application: <ERROR CLASS>` |
| [callbacks](callbacks.md) | `val callbacks: <ERROR CLASS>` |
| [cls](cls.md) | `val cls: `[`Class`](http://docs.oracle.com/javase/6/docs/api/java/lang/Class.html)`<`[`AT`](index.md#AT)`>` |
| [deinitialize](deinitialize.md) | `val deinitialize: `[`IDeinitializer`](../../org.swiften.redux.core/-i-deinitializer.md) |
| [navigate](navigate.md) | `val navigate: (`[`AT`](index.md#AT)`, `[`Screen`](index.md#Screen)`) -> `[`Unit`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-unit/index.html) |
| [runner](runner.md) | `val runner: `[`IMainThreadRunner`](../../org.swiften.redux.android.util/-android-util/-i-main-thread-runner/index.md) |

### Functions

| Name | Summary |
|---|---|
| [navigate](navigate.md) | `fun navigate(screen: `[`Screen`](index.md#Screen)`): `[`Unit`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-unit/index.html)<br>Navigate to an [IRouterScreen](../../org.swiften.redux.core/-i-router-screen.md). How this is done is left to the app's specific implementation. |