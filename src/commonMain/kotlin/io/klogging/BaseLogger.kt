/*

   Copyright 2021 Michael Strasser.

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

package io.klogging

import io.klogging.Level.DEBUG
import io.klogging.Level.ERROR
import io.klogging.Level.FATAL
import io.klogging.Level.INFO
import io.klogging.Level.NONE
import io.klogging.Level.TRACE
import io.klogging.Level.WARN
import io.klogging.events.LogEvent
import io.klogging.events.copyWith
import io.klogging.internal.KloggingState

/**
 * Base interface of [Klogger] interface for use in coroutines, and
 * [NoCoLogger] interface when not using coroutines.
 */
public interface BaseLogger {

    /** Name of the logger: usually a class name in Java. */
    public val name: String

    /**
     * Minimum level at which to emit log events, determined from current
     * configuration.
     */
    public fun minLevel(): Level = KloggingState.minimumLevelOf(name)

    /**
     * Check whether this logger will emit log events at the specified logging
     * level.
     */
    public fun isLevelEnabled(level: Level): Boolean = when {
        NONE == level -> false
        else -> minLevel() <= level
    }

    /** Is this logger enabled to emit [TRACE] events? */
    public fun isTraceEnabled(): Boolean = isLevelEnabled(TRACE)

    /** Is this logger enabled to emit [DEBUG] events? */
    public fun isDebugEnabled(): Boolean = isLevelEnabled(DEBUG)

    /** Is this logger enabled to emit [INFO] events? */
    public fun isInfoEnabled(): Boolean = isLevelEnabled(INFO)

    /** Is this logger enabled to emit [WARN] events? */
    public fun isWarnEnabled(): Boolean = isLevelEnabled(WARN)

    /** Is this logger enabled to emit [ERROR] events? */
    public fun isErrorEnabled(): Boolean = isLevelEnabled(ERROR)

    /** Is this logger enabled to emit [FATAL] events? */
    public fun isFatalEnabled(): Boolean = isLevelEnabled(FATAL)
}

/**
 * Extension function on [BaseLogger] that constructs a [LogEvent] from a range of types.
 *
 * - If the object is an event already, update it with level, stack trace (if present)
 *   and context items.
 * - Otherwise, construct an event with supplied information.
 */
internal fun BaseLogger.eventFrom(
    level: Level,
    exception: Exception?,
    eventObject: Any?,
    contextItems: Map<String, Any?> = mapOf(),
): LogEvent {
    return when (eventObject) {
        is LogEvent ->
            eventObject.copyWith(level, exception?.stackTraceToString(), contextItems)
        else -> {
            val (message, stackTrace) = messageAndStackTrace(eventObject, exception)
            LogEvent(
                logger = this.name,
                level = level,
                message = message,
                stackTrace = stackTrace,
                items = contextItems,
            )
        }
    }
}

/**
 * Extract message and stack trace values from a non-[LogEvent] object.
 *
 * @param obj an object that has been sent in a logging function call.
 *
 * @param exception an exception that may have been sent in a logging function call.
 *
 * @return a pair with the message to show and any stack trace that is present:
 *   - If the object is an exception, return its message and stack trace.
 *   - If the object is not an exception, return `toString()` on the object
 *     and any stack trace on the supplied exception.
 */
private fun messageAndStackTrace(obj: Any?, exception: Exception?): Pair<String, String?> =
    when (obj) {
        is Exception -> (obj.message ?: "Exception") to obj.stackTraceToString()
        else -> obj.toString() to exception?.stackTraceToString()
    }
