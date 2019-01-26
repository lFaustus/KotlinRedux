/*
 * Copyright (c) haipham 2018. All rights reserved.
 * Any attempt to reproduce this source code in any form shall be met with legal actions.
 */

package org.swiften.redux.android.sample;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import kotlin.Unit;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.swiften.redux.ui.IPropContainer;
import org.swiften.redux.ui.ReduxProps;
import org.swiften.redux.ui.StaticProps;

/** Created by haipham on 2018/12/19 */
public final class MainActivity extends AppCompatActivity implements
  IPropContainer<State, Unit, Unit> {
  @Nullable private ReduxProps<State, Unit, Unit> reduxProps;

  @Override
  protected void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);

    if (savedInstanceState == null) {
      this.getSupportFragmentManager()
        .beginTransaction()
        .replace(R.id.fragment, new MainFragment())
        .commit();
    }
  }

  @Nullable
  @Override
  public ReduxProps<State, Unit, Unit> getReduxProps() {
    return this.reduxProps;
  }

  @Override
  public void setReduxProps(@Nullable ReduxProps<State, Unit, Unit> reduxProps) {
    this.reduxProps = reduxProps;
  }

  @Override
  public void beforePropInjectionStarts(@NotNull StaticProps<State> sp) { }

  @Override
  public void afterPropInjectionEnds() { }
}