/*
 * Copyright (c) haipham 2019. All rights reserved.
 * Any attempt to reproduce this source code in any form shall be met with legal actions.
 */

package com.google.samples.apps.sunflower.dependency

import android.app.Application
import com.google.samples.apps.sunflower.GardenActivity
import com.google.samples.apps.sunflower.PlantListFragmentDirections
import org.swiften.redux.android.router.createSingleActivityRouter
import org.swiften.redux.router.IReduxRouter

/** Created by haipham on 2019/01/18 */
@Suppress("MoveLambdaOutsideParentheses")
class Router(app: Application) : IReduxRouter<Redux.Screen>
by createSingleActivityRouter<GardenActivity, Redux.Screen>(app, { a, s ->
  val navController = a.navController

  when (s) {
    is Redux.Screen.PlantDetail -> {
      val id = s.plantId
      val direction = PlantListFragmentDirections.ActionPlantListFragmentToPlantDetailFragment(id)
      navController.navigate(direction)
    }
  }
})