/*
 * Copyright (c) haipham 2019. All rights reserved.
 * Any attempt to reproduce this source code in any form shall be met with legal actions.
 */

package org.swiften.redux.android.ui.recyclerview

import androidx.lifecycle.LifecycleOwner
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import org.swiften.redux.android.ui.lifecycle.ILifecycleCallback
import org.swiften.redux.android.ui.lifecycle.ReduxLifecycleObserver
import org.swiften.redux.ui.IPropContainer
import org.swiften.redux.ui.IPropInjector
import org.swiften.redux.ui.IPropLifecycleOwner
import org.swiften.redux.ui.IPropMapper
import org.swiften.redux.ui.IStateMapper
import org.swiften.redux.ui.unsubscribeSafely

/** Created by haipham on 2019/01/24 */
/** Perform [injectRecyclerAdapter], but also handle lifecycle with [lifecycleOwner] */
fun <GState, GExt, VH, VHState, VHAction> IPropInjector<GState, GExt>.injectRecyclerAdapter(
  lifecycleOwner: LifecycleOwner,
  adapter: RecyclerView.Adapter<VH>,
  adapterMapper: IStateMapper<GState, Unit, Int>,
  vhMapper: IPropMapper<GState, GExt, Int, VHState, VHAction>
): RecyclerView.Adapter<VH> where
  GState : Any,
  GExt : Any,
  VH : RecyclerView.ViewHolder,
  VH : IPropContainer<VHState, VHAction>,
  VH : IPropLifecycleOwner<GState, GExt> {
  val wrappedAdapter = this.injectRecyclerAdapter(adapter, adapterMapper, vhMapper)

  ReduxLifecycleObserver(lifecycleOwner, object : ILifecycleCallback {
    override fun onSafeForStartingLifecycleAwareTasks() {}
    override fun onSafeForEndingLifecycleAwareTasks() { wrappedAdapter.unsubscribeSafely() }
  })

  return wrappedAdapter
}

fun <GState, GExt, Adapter, VH, VHState, VHAction> IPropInjector<GState, GExt>.injectRecyclerAdapter(
  lifecycleOwner: LifecycleOwner,
  adapter: Adapter,
  vhMapper: IPropMapper<GState, GExt, Int, VHState, VHAction>
): RecyclerView.Adapter<VH> where
  GState : Any,
  GExt : Any,
  VH : RecyclerView.ViewHolder,
  VH : IPropContainer<VHState, VHAction>,
  VH : IPropLifecycleOwner<GState, GExt>,
  Adapter : RecyclerView.Adapter<VH>,
  Adapter : IStateMapper<GState, Unit, Int> {
  return this.injectRecyclerAdapter(lifecycleOwner, adapter, adapter, vhMapper)
}

/** Perform [injectDiffedAdapter], but also handle lifecycle with [lifecycleOwner] */
fun <GState, GExt, VH, VHS, VHA> IPropInjector<GState, GExt>.injectDiffedAdapter(
  lifecycleOwner: LifecycleOwner,
  adapter: RecyclerView.Adapter<VH>,
  adapterMapper: IPropMapper<GState, GExt, Unit, List<VHS>, VHA>,
  diffCallback: DiffUtil.ItemCallback<VHS>
): ListAdapter<VHS, VH> where
  GState : Any,
  GExt : Any,
  VH : RecyclerView.ViewHolder,
  VH : IPropContainer<VHS, VHA>,
  VH : IPropLifecycleOwner<GState, GExt> {
  val wrappedAdapter = this.injectDiffedAdapter(adapter, adapterMapper, diffCallback)

  ReduxLifecycleObserver(lifecycleOwner, object : ILifecycleCallback {
    override fun onSafeForStartingLifecycleAwareTasks() {}

    override fun onSafeForEndingLifecycleAwareTasks() {
      wrappedAdapter.unsubscribeSafely()
      wrappedAdapter.vhSubscription.unsubscribe()
    }
  })

  return wrappedAdapter
}

/** Instead of [DiffUtil.ItemCallback], use [IDiffItemCallback] to avoid abstract class */
fun <GState, GExt, VH, VHS, VHA> IPropInjector<GState, GExt>.injectDiffedAdapter(
  lifecycleOwner: LifecycleOwner,
  adapter: RecyclerView.Adapter<VH>,
  adapterMapper: IPropMapper<GState, GExt, Unit, List<VHS>, VHA>,
  diffCallback: IDiffItemCallback<VHS>
): ListAdapter<VHS, VH> where
  GState : Any,
  GExt : Any,
  VH : RecyclerView.ViewHolder,
  VH : IPropContainer<VHS, VHA>,
  VH : IPropLifecycleOwner<GState, GExt> {
  return this.injectDiffedAdapter(lifecycleOwner, adapter, adapterMapper,
    object : DiffUtil.ItemCallback<VHS>() {
      override fun areItemsTheSame(oldItem: VHS, newItem: VHS): Boolean {
        return diffCallback.areItemsTheSame(oldItem, newItem)
      }

      override fun areContentsTheSame(oldItem: VHS, newItem: VHS): Boolean {
        return diffCallback.areContentsTheSame(oldItem, newItem)
      }
    })
}

/**
 * Convenience [injectDiffedAdapter] for when [mapper] implements both [IPropMapper] and
 * [DiffUtil.ItemCallback].
 */
fun <GState, GExt, Mapper, VH, VHS, VHA> IPropInjector<GState, GExt>.injectDiffedAdapter(
  lifecycleOwner: LifecycleOwner,
  adapter: RecyclerView.Adapter<VH>,
  mapper: Mapper
): ListAdapter<VHS, VH> where
  GState : Any,
  GExt : Any,
  VH : RecyclerView.ViewHolder,
  VH : IPropContainer<VHS, VHA>,
  VH : IPropLifecycleOwner<GState, GExt>,
  Mapper : IPropMapper<GState, GExt, Unit, List<VHS>, VHA>,
  Mapper : IDiffItemCallback<VHS> {
  return this.injectDiffedAdapter(lifecycleOwner, adapter, mapper, mapper)
}

/**
 * Convenience [injectDiffedAdapter] for when [adapter] implements both [RecyclerView.Adapter],
 * [IPropMapper] and [DiffUtil.ItemCallback].
 */
fun <GState, GExt, Adapter, VH, VHS, VHA> IPropInjector<GState, GExt>.injectDiffedAdapter(
  lifecycleOwner: LifecycleOwner,
  adapter: Adapter
): ListAdapter<VHS, VH> where
  GState : Any,
  GExt : Any,
  VH : RecyclerView.ViewHolder,
  VH : IPropContainer<VHS, VHA>,
  VH : IPropLifecycleOwner<GState, GExt>,
  Adapter : RecyclerView.Adapter<VH>,
  Adapter : IPropMapper<GState, GExt, Unit, List<VHS>, VHA>,
  Adapter : IDiffItemCallback<VHS> {
  return this.injectDiffedAdapter(lifecycleOwner, adapter, adapter)
}
