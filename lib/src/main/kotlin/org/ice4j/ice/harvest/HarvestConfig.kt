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

package org.ice4j.ice.harvest

import java.net.InetAddress

class HarvestConfig {

    var useLinkLocalAddresses: Boolean = true
    fun useLinkLocalAddresses() = useLinkLocalAddresses

    var udpReceiveBufferSize: Int? = null
    fun udpReceiveBufferSize() = udpReceiveBufferSize

    var udpSocketPoolSize: Int = 0

    fun udpSocketPoolSize() = udpSocketPoolSize

    var useIpv6: Boolean = true
    fun useIpv6() = useIpv6

    var useDynamicPorts: Boolean = true
    fun useDynamicPorts() = useDynamicPorts

    var timeout = 90 * 1000
    fun timeout() = timeout

    var stunMappingCandidateHarvesterAddresses: List<String> = arrayListOf()
    fun stunMappingCandidateHarvesterAddresses() = stunMappingCandidateHarvesterAddresses

    var enableAwsHarvester: Boolean = false
    fun enableAwsHarvester() = enableAwsHarvester

    var forceAwsHarvester: Boolean = false
    fun forceAwsHarvester() = enableAwsHarvester

    private val legacyNatHarvesterLocalAddress: String? = null

    private val legacyNatHarvesterPublicAddress: String? = null

    private val staticMappingsFromNewConfig: Set<StaticMapping> = emptySet()
//    config {
//        "ice4j.harvest.mapping.static-mappings".from(configSource)
//            .convertFrom<List<Config>> { cfg ->
//                cfg.map {
//                    val localPort = if (it.hasPath("local-port")) it.getInt("local-port") else null
//                    val publicPort = if (it.hasPath("public-port")) it.getInt("public-port") else null
//                    if ((localPort == null || publicPort == null) && localPort != publicPort) {
//                        throw ConfigException.UnableToRetrieve.ConditionNotMet(
//                            "Inconsistent value for local-port and public-port, both must be present or both missing."
//                        )
//                    }
//                    StaticMapping(
//                        localAddress = it.getString("local-address"),
//                        publicAddress = it.getString("public-address"),
//                        localPort = localPort,
//                        publicPort = publicPort,
//                        name = if (it.hasPath("name")) it.getString("name") else null
//                    )
//                }.toSet()
//            }
//    }

    /**
     * The list of IP addresses that are allowed to be used for host candidate allocations. If no addresses are
     * specified, any address is allowed.
     */
    val allowedAddresses: List<InetAddress> = arrayListOf()

    /**
     * The list of IP addresses that are not allowed to be used for host candidate allocations. If no addresses are
     * specified, any address is allowed.
     */
    val blockedAddresses: List<InetAddress> = arrayListOf()

    /**
     * The allowed interfaces for host candidate allocations. If none are specified all interfaces are allowed unless
     * blocked.
     */
    val allowedInterfaces: List<String> = arrayListOf()

    /**
     * The blocked interfaces for host candidate allocations. Note that this can not be used in conjunction with
     * [allowedInterfaces]. If [allowedInterfaces] are defined then [blockedInterfaces] is not used.
     */
    val blockedInterfaces: List<String> = arrayListOf()

    val staticMappings: Set<StaticMapping> = emptySet()

    private fun String.toInetAddress(): InetAddress? = try {
        InetAddress.getByName(this)
    } catch (e: Exception) {
        //logger.warn("Invalid address, will not use it as allowed/blocked: $this")
        null
    }

    data class StaticMapping(
        val localAddress: String,
        val publicAddress: String,
        val localPort: Int? = null,
        val publicPort: Int? = null,
        val name: String? = null
    )

    companion object {
        @JvmField
        val config = HarvestConfig()
    }
}
