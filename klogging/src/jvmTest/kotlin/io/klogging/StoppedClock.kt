/*

   Copyright 2021-2026 Michael Strasser.

   Licensed under the Apache License, Version 2.0 (the "License");
   you may not use this file except in compliance with the License.
   You may obtain a copy of the License at

       https://www.apache.org/licenses/LICENSE-2.0

   Unless required by applicable law or agreed to in writing, software
   distributed under the License is distributed on an "AS IS" BASIS,
   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
   See the License for the specific language governing permissions and
   limitations under the License.

*/

package io.klogging

import kotlinx.atomicfu.atomic
import kotlinx.atomicfu.update
import kotlinx.datetime.Clock
import kotlinx.datetime.Instant
import kotlin.time.Duration

/**
 * Simple implementation of [Clock] for testing. Once created, the clock is
 * stopped and returns the same instant unless it is reset, advanced or retarded.
 *
 * @param start starting instant
 */
class StoppedClock(
    start: Instant,
) : Clock {
    /**
     * Atomic holder whose current value will be returned on every call to [now].
     */
    private val currentInstant = atomic(start)

    /**
     * Return the current value held by the clock.
     *
     * @return the current clock value.
     */
    override fun now(): Instant = currentInstant.value

    /**
     * Reset to a new instant to be returned by [now].
     */
    fun reset(resetTo: Instant) {
        currentInstant.update { resetTo }
    }

    /**
     * Advances the clock by the specified duration.
     *
     * @param duration amount to advance the clock
     */
    fun advance(duration: Duration) {
        currentInstant.update { current ->
            current + duration
        }
    }

    /**
     * Retards the clock by the specified duration.
     *
     * @param duration amount to retard the clock
     */
    fun retard(duration: Duration) {
        currentInstant.update { current ->
            current - duration
        }
    }
}
