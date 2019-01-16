/*
 * Copyright (c) haipham 2018. All rights reserved.
 * Any attempt to reproduce this source code in any form shall be met with legal actions.
 */

package org.swiften.redux.android.sample

import java.io.Serializable

/** Created by haipham on 2018/12/19 */
data class State(
  val autocompleteQuery: String? = "",
  val musicResult: MusicResult? = null,
  val selectedResultIndex: Int? = null,
  val loadingMusic: Boolean? = null
) : Serializable {
  fun currentSelectedTrack() = this.selectedResultIndex?.let { index ->
    this.musicResult?.results?.elementAtOrNull(index)
  }
}