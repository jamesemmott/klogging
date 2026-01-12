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

import io.klogging.context.withLogContext
import io.kotest.core.spec.style.DescribeSpec
import io.kotest.matchers.maps.shouldContain
import io.kotest.matchers.shouldBe

class LoggerExtensionTests :
    DescribeSpec({
        describe("`Klogger.toNoCoLogger()` extension function") {
            it("copies the source logger name if a new name is not specified") {
                val sourceName = randomString()
                logger(sourceName).toNoCoLogger().name shouldBe sourceName
                logger(sourceName).toNoCoLogger(randomString() to randomString()).name shouldBe sourceName
            }
            it("uses a new name if specified") {
                val sourceName = randomString()
                val sourceLogger = logger(sourceName)
                val newName = randomString()
                sourceLogger.toNoCoLogger(newName).name shouldBe newName
                sourceLogger.toNoCoLogger(newName, randomString() to randomString()).name shouldBe newName
            }
            it("copies context items from the source logger") {
                val sourceContext = randomString() to randomString()
                logger(randomString(), sourceContext).toNoCoLogger().loggerContextItems shouldContain sourceContext
            }
            it("adds new logger context items if specified") {
                val contextItems = randomString() to randomString()

                logger(randomString()).toNoCoLogger(contextItems).loggerContextItems shouldContain contextItems
                logger(randomString())
                    .toNoCoLogger(
                        randomString(),
                        contextItems,
                    ).loggerContextItems shouldContain contextItems
            }
            it("adds scope context items if present") {
                val scopeContext = randomString() to randomString()
                withLogContext(scopeContext) {
                    logger(randomString()).toNoCoLogger().loggerContextItems shouldContain scopeContext
                }
            }
        }
        describe("`NoCoLogger.toKlogger()` extension function") {
            it("copies the source logger name if a new name is not specified") {
                val sourceName = randomString()
                noCoLogger(sourceName).toKlogger().name shouldBe sourceName
                noCoLogger(sourceName).toKlogger(randomString() to randomString()).name shouldBe sourceName
            }
            it("uses a new name if specified") {
                val sourceName = randomString()
                val sourceLogger = noCoLogger(sourceName)
                val newName = randomString()
                sourceLogger.toKlogger(newName).name shouldBe newName
                sourceLogger.toKlogger(newName, randomString() to randomString()).name shouldBe newName
            }
            it("copies context items from the source logger") {
                val sourceContext = randomString() to randomString()
                noCoLogger(
                    randomString(),
                    sourceContext,
                ).toKlogger().loggerContextItems shouldContain sourceContext
                noCoLogger(
                    randomString(),
                    sourceContext,
                ).toKlogger(randomString()).loggerContextItems shouldContain sourceContext
            }
            it("adds new logger context items if specified") {
                val contextItems = randomString() to randomString()

                noCoLogger(randomString()).toKlogger(contextItems).loggerContextItems shouldContain contextItems
                noCoLogger(randomString())
                    .toKlogger(
                        randomString(),
                        contextItems,
                    ).loggerContextItems shouldContain contextItems
            }
        }
    })
