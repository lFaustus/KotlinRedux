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
import androidx.appcompat.widget.Toolbar
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.navigation.NavigationView
import com.google.samples.apps.sunflower.dependency.Redux
import org.swiften.redux.ui.EmptyPropLifecycleOwner
import org.swiften.redux.ui.IPropContainer
import org.swiften.redux.ui.IPropLifecycleOwner
import org.swiften.redux.ui.ReduxProps

class GardenActivity : AppCompatActivity(),
  IPropContainer<Redux.State, Unit, Unit>,
  IPropLifecycleOwner<Redux.State> by EmptyPropLifecycleOwner() {
  override var reduxProps: ReduxProps<Redux.State, Unit, Unit>? = null

  private lateinit var drawerLayout: DrawerLayout
  private lateinit var appBarConfiguration: AppBarConfiguration
  lateinit var navController: NavController

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    this.setContentView(R.layout.activity_garden)
    this.drawerLayout = this.findViewById(R.id.drawer_layout)
    this.navController = Navigation.findNavController(this, R.id.garden_nav_fragment)
    this.appBarConfiguration = AppBarConfiguration(navController.graph, drawerLayout)

    // Set up ActionBar
    val toolbar: Toolbar = this.findViewById(R.id.toolbar)
    setSupportActionBar(toolbar)
    setupActionBarWithNavController(navController, appBarConfiguration)

    // Set up navigation menu
    val navigationView: NavigationView = this.findViewById(R.id.navigation_view)
    navigationView.setupWithNavController(navController)
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