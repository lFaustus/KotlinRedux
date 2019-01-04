/*
 * Copyright (c) haipham 2018. All rights reserved.
 * Any attempt to reproduce this source code in any form shall be met with legal actions.
 */

package org.swiften.redux.android.sample

import kotlinx.coroutines.async
import org.swiften.redux.saga.ReduxSagaHelper.just
import org.swiften.redux.saga.ReduxSagaHelper.takeLatestAction
import org.swiften.redux.saga.map

/** Created by haipham on 2019/01/04 */
object MainSaga {
  fun sagas(api: IMainRepository) = arrayListOf(
    takeLatestAction<State, MainRedux.Action, String, Any>(
      { when (it) {
        is MainRedux.Action.UpdateAutocompleteQuery -> it.query
      } },
      { autocompleteSaga(api, it) }
    )
  )

  private fun autocompleteSaga(api: IMainRepository, query: String) =
    just<State, String>(query)
      .map { this.async { api.searchMusicStore(query) }.await() }
      .map { it as Any }
}