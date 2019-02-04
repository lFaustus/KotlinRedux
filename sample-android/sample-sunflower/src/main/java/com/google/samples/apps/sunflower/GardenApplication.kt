/*
 * Copyright (c) haipham 2019. All rights reserved.
 * Any attempt to reproduce this source code in any form shall be met with legal actions.
 */

package com.google.samples.apps.sunflower

import android.app.Application
import com.google.samples.apps.sunflower.dependency.Redux
import com.google.samples.apps.sunflower.dependency.Router
import com.google.samples.apps.sunflower.utilities.InjectorUtils
import com.squareup.leakcanary.LeakCanary
import org.swiften.redux.android.ui.AndroidPropInjector
import org.swiften.redux.android.ui.lifecycle.injectLifecycle
import org.swiften.redux.android.ui.lifecycle.injectActivityParcelable
import org.swiften.redux.async.createAsyncMiddleware
import org.swiften.redux.core.FinalStore
import org.swiften.redux.core.applyMiddlewares
import org.swiften.redux.core.createLoggingMiddleware
import org.swiften.redux.core.createRouterMiddleware
import org.swiften.redux.saga.common.createSagaMiddleware

/** Created by haipham on 2019/01/17 */
class GardenApplication : Application() {
  override fun onCreate() {
    super.onCreate()
    if (LeakCanary.isInAnalyzerProcess(this)) { return }
    LeakCanary.install(this)

    val store = applyMiddlewares<Redux.State>(
      createRouterMiddleware(Router(this)),
      createSagaMiddleware(
        arrayListOf(
          arrayListOf(Redux.Saga.CoreSaga.watchNetworkConnectivity(this)),
          Redux.Saga.GardenPlantingSaga.allSagas(InjectorUtils.getGardenPlantingRepository(this)),
          Redux.Saga.PlantSaga.allSagas(InjectorUtils.getPlantRepository(this))
        ).flatten()
      ),
      createAsyncMiddleware()
    )(FinalStore(Redux.State(), Redux.Reducer))

    val injector = AndroidPropInjector(store, Unit)

    injector.injectActivityParcelable(this) {
      when (it) {
        is GardenFragment -> this.injectLifecycle(it, Unit, GardenFragment)
        is PlantDetailFragment -> this.injectLifecycle(it, Unit, PlantDetailFragment)
        is PlantListFragment -> this.injectLifecycle(it, Unit, PlantListFragment)
      }
    }
  }
}
