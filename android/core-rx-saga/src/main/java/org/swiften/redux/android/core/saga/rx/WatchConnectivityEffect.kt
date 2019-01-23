/*
 * Copyright (c) haipham 2019. All rights reserved.
 * Any attempt to reproduce this source code in any form shall be met with legal actions.
 */

package org.swiften.redux.android.core.saga.rx

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import android.net.NetworkRequest
import android.os.Build
import io.reactivex.BackpressureStrategy
import io.reactivex.Observable
import org.swiften.redux.saga.IReduxSagaEffect
import org.swiften.redux.saga.IReduxSagaOutput
import org.swiften.redux.saga.Input
import org.swiften.redux.saga.ReduxSagaEffect
import org.swiften.redux.saga.rx.ReduxSagaOutput

/** Created by haipham on 2019/01/21/1/ */
/** [IReduxSagaEffect] whose [IReduxSagaOutput] watches for network connectivity changes */
internal class WatchConnectivityEffect(private val context: Context) : ReduxSagaEffect<Boolean>() {
  override fun invoke(p1: Input): IReduxSagaOutput<Boolean> {
    val stream = Observable.create<Boolean> { emitter ->
      val application = this@WatchConnectivityEffect.context
      val cm = application.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

      if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
        val receiver = object : BroadcastReceiver() {
          override fun onReceive(context: Context?, intent: Intent?) {
            emitter.onNext(cm.activeNetworkInfo?.isConnected ?: false)
          }
        }

        val intentFilter = IntentFilter("android.net.conn.CONNECTIVITY_CHANGE")
        application.registerReceiver(receiver, intentFilter)
        emitter.setCancellable { application.unregisterReceiver(receiver) }
      } else {
        val request = NetworkRequest.Builder()
          .addCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
          .addCapability(NetworkCapabilities.NET_CAPABILITY_NOT_RESTRICTED)
          .build()

        val callback = object : ConnectivityManager.NetworkCallback() {
          override fun onAvailable(network: Network?) {
            super.onAvailable(network)
            emitter.onNext(true)
          }

          override fun onLost(network: Network?) {
            super.onLost(network)
            emitter.onNext(false)
          }
        }

        cm.registerNetworkCallback(request, callback)
        emitter.setCancellable { cm.unregisterNetworkCallback(callback) }
      }
    }

    return ReduxSagaOutput(p1.scope, stream.toFlowable(BackpressureStrategy.BUFFER)) { }
  }
}