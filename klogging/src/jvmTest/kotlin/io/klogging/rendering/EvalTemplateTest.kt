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

package io.klogging.rendering

import io.klogging.logEvent
import io.kotest.core.spec.style.DescribeSpec
import io.kotest.matchers.shouldBe

class EvalTemplateTest :
    DescribeSpec({
        describe("`LogEvent.evalTemplate()` extension function") {
            it("leaves the message unchanged if there are no items") {
                val message = "This is a {test} message"
                logEvent(message = message)
                    .evalTemplate() shouldBe message
            }
            it("leaves the message unchanged if there are no placeholders") {
                val message = "This is a test message"
                logEvent(message = message, items = mapOf("test" to "TEST"))
                    .evalTemplate() shouldBe message
            }
            it("replaces placeholders that match item keys with item values") {
                val message = "This is a {test} message"
                logEvent(message = message, items = mapOf("test" to "TEST", "test2" to "TEST2"))
                    .evalTemplate() shouldBe "This is a TEST message"
            }
            it("does not replace placeholders if item keys are different cases") {
                val message = "This is a {test} message"
                logEvent(message = message, items = mapOf("TEST" to "TEST"))
                    .evalTemplate() shouldBe message
            }
        }
    })
