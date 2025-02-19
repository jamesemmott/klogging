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

package io.klogging.internal

import io.klogging.Level
import io.klogging.config.KloggingConfiguration
import io.klogging.config.LoggingConfig
import io.klogging.config.SinkConfiguration
import io.klogging.config.configLoadedFromFile

/**
 * Object that is the centre of Klogging processing.
 *
 * All static state should be managed here.
 */
public object KloggingEngine {

    /** Default, empty configuration. */
    private val DEFAULT_CONFIG = KloggingConfiguration()

    /** Key for the current state (and only!) entry in the map. */
    private const val CURRENT_STATE = "CURRENT_STATE"

    /** Map with the current Klogging configuration. */
    private val currentState: MutableMap<String, KloggingConfiguration> =
        mutableMapOf(CURRENT_STATE to DEFAULT_CONFIG)

    /** Lazily loaded property that is only set when we have a Klogging state. */
    internal val configuration: KloggingConfiguration by lazy {
        debug("KloggingEngine", "Lazy-loading current configuration")
        configLoadedFromFile?.let { setConfig(it) }
        currentConfig
    }

    /** Set a new configuration, replacing the existing one.  */
    internal fun setConfig(config: KloggingConfiguration) {
        // No synchronisation or locking yet.
        currentState[CURRENT_STATE] = config
        setSinks(config.sinks)
    }

    /** Append a new configuration to the existing one. */
    internal fun appendConfig(config: KloggingConfiguration) {
        currentConfig.append(config)
        setSinks(currentConfig.sinks)
    }

    /** Return the current configuration, ensuring it is never null. */
    private val currentConfig: KloggingConfiguration
        get() = currentState[CURRENT_STATE] ?: DEFAULT_CONFIG

    /** Map of the current [Sink]s used for sending log events. */
    private val currentSinks: MutableMap<String, Sink> = mutableMapOf()

    /** Set new sinks, from configurations. */
    private fun setSinks(sinkConfigs: Map<String, SinkConfiguration>) {
        currentSinks.clear()
        currentSinks.putAll(
            sinkConfigs.map { (name, config) ->
                name to Sink(name, config.eventSender)
            }
        )
    }

    // Functions returning the current state.
    internal fun minimumLevelOf(name: String): Level = currentConfig.minimumLevelOf(name)

    internal fun sinks(): Map<String, Sink> = currentSinks

    internal fun sinkConfigs(): Map<String, SinkConfiguration> = currentConfig.sinks

    internal fun configs(): List<LoggingConfig> = currentConfig.configs

    internal fun kloggingMinLogLevel(): Level = currentConfig.kloggingMinLogLevel
}
