/*
 * Copyright (c) haipham 2019. All rights reserved.
 * Any attempt to reproduce this source code in any form shall be met with legal actions.
 */

package org.swiften.redux.saga.cr

import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.channels.Channel
import org.testng.annotations.Test

/** Created by haipham on 2018/12/26 */
@ExperimentalCoroutinesApi
class LifecycleChannelTest {
  @Test
  fun `Send channels should invoke side effects on lifecycle change`() {
    // Setup
    var closed = 0
    val channel = LifecycleSendChannel("", Channel<Int>()) { closed += 1 }

    // When
    channel.close()
    channel.close()
    channel.close()

    // Then
    assertEquals(closed, 2)
  }

  @Test
  fun `Receive channels should invoke side effects on lifecycle change`() {
    // Setup
    var cancelled = 0
    val channel =
      LifecycleReceiveChannel("", Channel<Int>()) { cancelled += 1 }

    // When
    channel.cancel()
    channel.cancel()
    channel.cancel()

    // Then
    assertEquals(cancelled, 3)
  }

  @Test
  fun `Lifecycle channels should invoke side effects on lifecycle change`() {
    // Setup
    var closed = 0
    var cancelled = 0

    val channel = LifecycleChannel("", Channel<Int>(),
      { closed += 1 },
      { cancelled += 1 }
    )

    // When
    channel.close()
    channel.cancel()

    // Then
    assertEquals(closed, 2)
    assertEquals(cancelled, 1)
  }
}
