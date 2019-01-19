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
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.RecyclerView
import com.google.samples.apps.sunflower.adapters.GardenPlantingAdapter
import com.google.samples.apps.sunflower.dependency.Redux
import com.google.samples.apps.sunflower.utilities.InjectorUtils
import kotlinx.android.synthetic.main.fragment_garden.view.empty_garden
import kotlinx.android.synthetic.main.fragment_garden.view.garden_list
import org.swiften.redux.android.ui.recyclerview.injectRecyclerViewProps
import org.swiften.redux.core.IReduxDispatcher
import org.swiften.redux.ui.EmptyReduxPropLifecycleOwner
import org.swiften.redux.ui.IReduxPropContainer
import org.swiften.redux.ui.IReduxPropLifecycleOwner
import org.swiften.redux.ui.IReduxPropMapper
import org.swiften.redux.ui.ObservableReduxProps

class GardenFragment : Fragment(),
  IReduxPropContainer<Redux.State, GardenFragment.S, GardenFragment.A>,
  IReduxPropMapper<Redux.State, Unit, GardenFragment.S, GardenFragment.A> by GardenFragment {
  class S(val gardenPlantingCount: Int)
  class A

  companion object : IReduxPropMapper<Redux.State, Unit, S, A> {
    override fun mapState(state: Redux.State, outProps: Unit) = S(state.gardenPlantings?.size ?: 0)
    override fun mapAction(dispatch: IReduxDispatcher, state: Redux.State, outProps: Unit) = A()
  }

  override var reduxProps by ObservableReduxProps<Redux.State, S, A> { prev, next ->
    next?.state?.also {
      if (it.gardenPlantingCount == 0) {
        this.gardenList.visibility = View.GONE
        this.emptyGarden.visibility = View.VISIBLE
      } else {
        this.gardenList.visibility = View.VISIBLE
        this.emptyGarden.visibility = View.GONE
      }

      if (it.gardenPlantingCount != prev?.state?.gardenPlantingCount) {
        this.gardenList.adapter?.notifyDataSetChanged()
      }
    }
  }

  private lateinit var gardenList: RecyclerView
  private lateinit var emptyGarden: TextView

  override fun onCreateView(
    inflater: LayoutInflater,
    container: ViewGroup?,
    savedInstanceState: Bundle?
  ): View? = inflater.inflate(R.layout.fragment_garden, container, false)

  override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
    this.gardenList = view.findViewById(R.id.garden_list)
    this.emptyGarden = view.findViewById(R.id.empty_garden)
  }

  override fun beforePropInjectionStarts() {
    this.gardenList.adapter = GardenPlantingAdapter().let {
      val vhMapper = GardenPlantingAdapter.ViewHolder
      this.reduxProps.static.injector.injectRecyclerViewProps(it, it, vhMapper)
    }
  }

  override fun afterPropInjectionEnds() {
    this.gardenList.adapter = null
  }
}
