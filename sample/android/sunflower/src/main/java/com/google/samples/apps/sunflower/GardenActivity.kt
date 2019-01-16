/*
 * Copyright 2018 Google LLC
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.google.samples.apps.sunflower

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.databinding.DataBindingUtil
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.FragmentManager
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.google.samples.apps.sunflower.databinding.ActivityGardenBinding
import com.google.samples.apps.sunflower.dependency.MainRedux
import org.swiften.redux.android.ui.core.endFragmentInjection
import org.swiften.redux.android.ui.core.startFragmentInjection
import org.swiften.redux.ui.EmptyReduxPropLifecycleOwner
import org.swiften.redux.ui.IReduxPropContainer
import org.swiften.redux.ui.IReduxPropLifecycleOwner
import org.swiften.redux.ui.ReduxProps

class GardenActivity : AppCompatActivity(),
  IReduxPropContainer<MainRedux.State, Unit, Unit>,
  IReduxPropLifecycleOwner by EmptyReduxPropLifecycleOwner {
  override lateinit var reduxProps: ReduxProps<MainRedux.State, Unit, Unit>

  private lateinit var fragmentCallbacks: FragmentManager.FragmentLifecycleCallbacks

  private lateinit var drawerLayout: DrawerLayout
  private lateinit var appBarConfiguration: AppBarConfiguration
  private lateinit var navController: NavController

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)

    val binding: ActivityGardenBinding = DataBindingUtil.setContentView(this,
      R.layout.activity_garden)
    drawerLayout = binding.drawerLayout

    navController = Navigation.findNavController(this, R.id.garden_nav_fragment)
    appBarConfiguration = AppBarConfiguration(navController.graph, drawerLayout)

    // Set up ActionBar
    setSupportActionBar(binding.toolbar)
    setupActionBarWithNavController(navController, appBarConfiguration)

    // Set up navigation menu
    binding.navigationView.setupWithNavController(navController)

    this.fragmentCallbacks = startFragmentInjection(this) {}
  }

  override fun onDestroy() {
    super.onDestroy()
    endFragmentInjection(this, this.fragmentCallbacks)
  }

  override fun onSupportNavigateUp(): Boolean {
    return navController.navigateUp(appBarConfiguration) || super.onSupportNavigateUp()
  }

  override fun onBackPressed() {
    if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
      drawerLayout.closeDrawer(GravityCompat.START)
    } else {
      super.onBackPressed()
    }
  }
}
