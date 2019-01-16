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
import org.swiften.redux.android.ui.recyclerview.ReduxRecyclerViewAdapter
import org.swiften.redux.android.ui.recyclerview.injectRecyclerViewProps
import org.swiften.redux.core.IReduxDispatcher
import org.swiften.redux.ui.IReduxPropContainer
import org.swiften.redux.ui.IReduxPropLifecycleOwner
import org.swiften.redux.ui.IReduxPropMapper
import org.swiften.redux.ui.IReduxStatePropMapper
import org.swiften.redux.ui.ObservableReduxProps

/** Created by haipham on 2018/12/20 */
class SearchFragment : Fragment(),
  IReduxPropContainer<State, SearchFragment.S, SearchFragment.A>,
  IReduxPropMapper<State, Unit, SearchFragment.S, SearchFragment.A> by SearchFragment,
  IReduxPropLifecycleOwner {
  data class S(val query: String?, val resultCount: Int?, val loading: Boolean?)
  class A(val updateQuery: (String?) -> Unit)

  class Adapter : ReduxRecyclerViewAdapter<ViewHolder>(),
    IReduxStatePropMapper<State, Unit, Int> by Adapter {
    companion object : IReduxStatePropMapper<State, Unit, Int> {
      override fun mapState(state: State, outProps: Unit) = state.musicResult?.resultCount ?: 0
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
    IReduxPropContainer<State, ViewHolder.S1, ViewHolder.A1>,
    IReduxPropMapper<State, Int, ViewHolder.S1, ViewHolder.A1> by ViewHolder {
    data class S1(val trackName: String? = null, val artistName: String? = null)
    data class A1(val goToMusicDetail: () -> Unit)

    companion object : IReduxPropMapper<State, Int, S1, A1> {
      override fun mapAction(dispatch: IReduxDispatcher, state: State, outProps: Int) =
        A1 { dispatch(MainRedux.Screen.MusicDetail(outProps)) }

      override fun mapState(state: State, outProps: Int) =
        state.musicResult?.results
          ?.elementAtOrNull(outProps)?.let { S1(it.trackName, it.artistName) }
          ?: S1()
    }

    override var reduxProps by ObservableReduxProps<State, S1, A1> { _, next ->
      next.variable?.state?.also {
        this.trackName.text = it.trackName
        this.artistName.text = it.artistName
      }
    }

    private val clickListener = View.OnClickListener { _ ->
      this.reduxProps.variable?.actions?.also { it.goToMusicDetail() }
    }

    override fun beforePropInjectionStarts() {
      this.parent.setOnClickListener(this.clickListener)
    }

    override fun afterPropInjectionEnds() {
      this.parent.setOnClickListener(null)
    }
  }

  companion object : IReduxPropMapper<State, Unit, S, A> {
    override fun mapAction(
      dispatch: IReduxDispatcher,
      state: State,
      outProps: Unit
    ) = A { dispatch(MainRedux.Action.UpdateAutocompleteQuery(it)) }

    override fun mapState(state: State, outProps: Unit) = S(
      state.autocompleteQuery,
      state.musicResult?.resultCount,
      state.loadingMusic
    )
  }

  override var reduxProps by ObservableReduxProps<State, S, A> { prev, next ->
    if (next.variable?.state?.loading == true) {
      this.backgroundDim.visibility = View.VISIBLE
      this.progressBar.visibility = View.VISIBLE
    } else {
      this.backgroundDim.visibility = View.GONE
      this.progressBar.visibility = View.GONE
    }

    if (next.variable?.state?.resultCount != prev?.variable?.state?.resultCount) {
      this.searchResult.adapter?.notifyDataSetChanged()
    }
  }

  private val querySearchWatcher = object : TextWatcher {
    override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

    override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}

    override fun afterTextChanged(s: Editable?) {
      this@SearchFragment.reduxProps.variable?.also { it.actions.updateQuery(s?.toString()) }
    }
  }

  override fun onCreateView(
    inflater: LayoutInflater,
    container: ViewGroup?,
    savedInstanceState: Bundle?
  ): View? = inflater.inflate(R.layout.fragment_search, container, false)

  override fun beforePropInjectionStarts() {
    this.querySearch.also { it.addTextChangedListener(this.querySearchWatcher) }

    this.searchResult.also {
      it.setHasFixedSize(true)
      it.layoutManager = LinearLayoutManager(this.context)

      it.adapter = Adapter().let { a ->
        this.reduxProps.static.injector.injectRecyclerViewProps(a, a, ViewHolder)
      }
    }
  }

  override fun afterPropInjectionEnds() {
    this.querySearch.removeTextChangedListener(this.querySearchWatcher)
    this.searchResult.adapter = null
  }
}