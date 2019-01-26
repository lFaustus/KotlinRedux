/*
 * Copyright (c) haipham 2018. All rights reserved.
 * Any attempt to reproduce this source code in any form shall be met with legal actions.
 */

package org.swiften.redux.android.sample

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.fragment_search.backgroundDim
import kotlinx.android.synthetic.main.fragment_search.progressBar
import kotlinx.android.synthetic.main.fragment_search.querySearch
import kotlinx.android.synthetic.main.fragment_search.searchResult
import kotlinx.android.synthetic.main.view_search_result.view.artistName
import kotlinx.android.synthetic.main.view_search_result.view.trackName
import org.swiften.redux.android.ui.recyclerview.DiffItemCallback
import org.swiften.redux.android.ui.recyclerview.ReduxRecyclerViewAdapter
import org.swiften.redux.android.ui.recyclerview.injectDiffedAdapter
import org.swiften.redux.core.IActionDispatcher
import org.swiften.redux.ui.EmptyPropLifecycleOwner
import org.swiften.redux.ui.IPropContainer
import org.swiften.redux.ui.IPropLifecycleOwner
import org.swiften.redux.ui.IPropMapper
import org.swiften.redux.ui.IVariablePropContainer
import org.swiften.redux.ui.ObservableReduxProps
import org.swiften.redux.ui.ObservableVariableProps
import org.swiften.redux.ui.StaticProps

/** Created by haipham on 2018/12/20 */
class SearchFragment : Fragment(),
  IPropContainer<State, SearchFragment.S, SearchFragment.A>,
  IPropLifecycleOwner<State> by EmptyPropLifecycleOwner(),
  IPropMapper<State, Unit, SearchFragment.S, SearchFragment.A> by SearchFragment {
  data class S(val query: String?, val loading: Boolean?)
  class A(val updateQuery: (String?) -> Unit)

  class Adapter : ReduxRecyclerViewAdapter<ViewHolder>(),
    IPropMapper<State, Unit, List<ViewHolder.S1>?, ViewHolder.A1> by Adapter,
    DiffItemCallback<ViewHolder.S1> by Adapter {
    companion object :
      IPropMapper<State, Unit, List<ViewHolder.S1>?, ViewHolder.A1>,
      DiffItemCallback<ViewHolder.S1> {
      override fun mapState(state: State, outProps: Unit) =
        state.musicResult?.results?.map { ViewHolder.S1(it.trackName, it.artistName) }
        ?: arrayListOf()

      override fun mapAction(
        dispatch: IActionDispatcher,
        state: State,
        outProps: Unit
      ) = ViewHolder.A1 { dispatch(MainRedux.Screen.MusicDetail(it)) }

      override fun areItemsTheSame(oldItem: ViewHolder.S1, newItem: ViewHolder.S1) =
        oldItem.trackName == newItem.trackName

      override fun areContentsTheSame(oldItem: ViewHolder.S1, newItem: ViewHolder.S1) =
        oldItem == newItem
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
      val view = LayoutInflater.from(parent.context)
        .inflate(R.layout.view_search_result, parent, false)

      return ViewHolder(view, view.trackName, view.artistName)
    }
  }

  class ViewHolder(
    private val parent: View,
    private val trackName: TextView,
    private val artistName: TextView
  ) : RecyclerView.ViewHolder(parent),
    IVariablePropContainer<ViewHolder.S1, ViewHolder.A1> {
    data class S1(val trackName: String? = null, val artistName: String? = null)
    data class A1(val goToMusicDetail: (Int) -> Unit)

    init {
      this.parent.setOnClickListener {
        val index = this.layoutPosition
        this.reduxProps?.action?.also { a -> a.goToMusicDetail(index) }
      }
    }

    override var reduxProps by ObservableVariableProps<S1, A1> { _, next ->
      next?.state?.also {
        this.trackName.text = it.trackName
        this.artistName.text = it.artistName
      }
    }
  }

  companion object : IPropMapper<State, Unit, S, A> {
    override fun mapAction(
      dispatch: IActionDispatcher,
      state: State,
      outProps: Unit
    ) = A { dispatch(MainRedux.Action.UpdateAutocompleteQuery(it)) }

    override fun mapState(state: State, outProps: Unit) = S(
      state.autocompleteQuery,
      state.loadingMusic
    )
  }

  override var reduxProps by ObservableReduxProps<State, S, A> { _, next ->
    if (next?.state?.loading == true) {
      this.backgroundDim.visibility = View.VISIBLE
      this.progressBar.visibility = View.VISIBLE
    } else {
      this.backgroundDim.visibility = View.GONE
      this.progressBar.visibility = View.GONE
    }
  }

  private val querySearchWatcher = object : TextWatcher {
    override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

    override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}

    override fun afterTextChanged(s: Editable?) {
      this@SearchFragment.reduxProps?.variable?.also { it.action.updateQuery(s?.toString()) }
    }
  }

  override fun onCreateView(
    inflater: LayoutInflater,
    container: ViewGroup?,
    savedInstanceState: Bundle?
  ): View? = inflater.inflate(R.layout.fragment_search, container, false)

  override fun beforePropInjectionStarts(sp: StaticProps<State>) {
    this.querySearch.also { it.addTextChangedListener(this.querySearchWatcher) }

    this.searchResult.also {
      it.setHasFixedSize(true)
      it.layoutManager = LinearLayoutManager(this.context)
      it.adapter = sp.injector.injectDiffedAdapter(this, Adapter())
    }
  }
}