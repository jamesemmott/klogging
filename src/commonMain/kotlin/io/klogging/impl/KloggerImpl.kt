/*

   Copyright 2021-2022 Michael Strasser.

   Licensed under the Apache License, Version 2.0 (the "License");
   you may not use this file except in compliance with the License.
   You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

   Unless required by applicable law or agreed to in writing, software
   distributed under the License is distributed on an "AS IS" BASIS,
   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
   See the License for the specific language governing permissions and
   limitations under the License.

*/

package io.klogging.impl

import io.klogging.Klogger
import io.klogging.Level
import io.klogging.context.LogContext
import io.klogging.events.LogEvent
import io.klogging.events.currentContext
import io.klogging.events.timestampNow
import io.klogging.internal.Emitter
import io.klogging.templating.templateItems
import kotlin.coroutines.coroutineContext

public class KloggerImpl(
    override val name: String,
) : Klogger {
    override suspend fun emitEvent(level: Level, throwable: Throwable?, event: Any?) {
        val eventToLog = eventFrom(level, throwable, event, contextItems())
        Emitter.sendEvent(eventToLog)
    }

    private suspend inline fun contextItems() =
        coroutineContext[LogContext]?.getAll() ?: mapOf()

    override suspend fun e(template: String, vararg values: Any?): LogEvent {
        val items = templateItems(template, *values).mapValues { e -> e.value }
        return LogEvent(
            timestamp = timestampNow(),
            logger = this.name,
            context = currentContext(),
            level = minLevel(),
            template = template,
            message = template,
            stackTrace = null,
            items = items + contextItems(),
        )
    }
}
