/*
 * Copyright (c) haipham 2019. All rights reserved.
 * Any attempt to reproduce this source code in any form shall be met with legal actions.
 */

package org.swiften.redux.android.dagger.business1

import dagger.Subcomponent
import org.swiften.redux.android.dagger.DependencyLevel1

/** Created by viethai.pham on 2019/02/21 */
@Business1Scope
@Subcomponent(modules = [Business1SagaModule::class, APIModule::class, RepositoryModule::class])
interface Business1SagaComponent : ISearchRepositoryProvider

interface Business1SagaComponentProvider {
  fun plus(module: Business1SagaModule): Business1SagaComponent
}

@Business1Scope
@Subcomponent(modules = [Parent1Module::class, Business1Module::class])
interface Business1Component {
  fun parent1Dependency(): DependencyLevel1
}
