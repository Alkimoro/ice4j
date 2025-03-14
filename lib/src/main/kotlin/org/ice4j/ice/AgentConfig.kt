/*
 * Copyright @ 2020 - Present, 8x8 Inc
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.ice4j.ice

class AgentConfig {
    var consentFreshnessInterval = 15 * 1000

    var randomizeConsentFreshnessInterval = true

    var consentFreshnessOriginalWaitInterval = 500

    var consentFreshnessMaxWaitInterval = 500

    var maxConsentFreshnessRetransmissions: Int = 30

    var terminationDelay = 3 * 1000

    var maxCheckListSize: Int = 100

    /** The value of the SOFTWARE attribute that ice4j should include in all outgoing messages. */
    var software: String? = "ice4j.org"

    /** Whether remote IP addresses should be redacted in logs */
    var redactRemoteAddresses: Boolean = false

    /**
     * Whether the per-component merging socket should be enabled by default (the default value can be
     * overridden with the [Agent] API).
     * If enabled, the user of the library must use the socket instance provided by [Component.getSocket]. Otherwise,
     * the socket instance from the desired [CandidatePair] must be used.
     */
    var useComponentSocket: Boolean = true

    companion object {
        @JvmField
        val config = AgentConfig()
    }
}
