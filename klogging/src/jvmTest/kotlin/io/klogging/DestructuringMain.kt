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

import io.klogging.config.loggingConfiguration
import io.klogging.rendering.RENDER_ECS_DOTNET
import io.klogging.sending.STDOUT
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay

data class User(
    val name: String,
    val age: Int,
)

enum class Source { MOBILE, WEB }

data class Login(
    val user: User,
    val source: Source,
)

suspend fun main() =
    coroutineScope {
        val logger = logger("io.klogging.DestructuringMain")

        loggingConfiguration(append = false) {
            kloggingMinLogLevel = Level.DEBUG
            minDirectLogLevel = Level.DEBUG
            sink("console", RENDER_ECS_DOTNET, STDOUT)
            logging { fromMinLevel(Level.DEBUG) { toSink("console") } }
        }
        val user = User("John", 23)
        val login = Login(user, Source.MOBILE)

        logger.info("Login: {@login}", login)

        delay(500)
    }
