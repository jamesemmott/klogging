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

import io.kotest.core.spec.style.DescribeSpec
import io.kotest.matchers.shouldBe
import kotlinx.datetime.Clock
import kotlinx.datetime.Instant
import kotlin.time.Duration.Companion.days
import kotlin.time.Duration.Companion.hours

class StoppedClockTest :
    DescribeSpec({
        describe("`StoppedClock` class") {
            val anInstant = Instant.parse("2019-05-05T00:00:00Z")
            it("returns the starting instant if unchanged") {
                StoppedClock(anInstant).now() shouldBe anInstant
            }
            it("returns a new instant after being reset") {
                val clock = StoppedClock(Clock.System.now())
                clock.reset(anInstant)
                clock.now() shouldBe anInstant
            }
            it("advances by a specified duration") {
                val clock = StoppedClock(anInstant)
                clock.advance(3.hours)
                clock.now() shouldBe Instant.parse("2019-05-05T03:00:00Z")
            }
            it("retards by a specified duration") {
                val clock = StoppedClock(anInstant)
                clock.retard(7.days)
                clock.now() shouldBe Instant.parse("2019-04-28T00:00:00Z")
            }
            it("isn’t needed if you don’t need to change the time") {
                val clock =
                    object : Clock {
                        override fun now(): Instant = anInstant
                    }
                clock.now() shouldBe anInstant
            }
        }
    })
