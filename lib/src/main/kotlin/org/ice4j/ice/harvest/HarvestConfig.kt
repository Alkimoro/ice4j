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

import com.typesafe.config.Config
import org.jitsi.metaconfig.ConfigException
import org.jitsi.metaconfig.config
import org.jitsi.metaconfig.optionalconfig
import org.jitsi.utils.logging2.createLogger
import java.net.InetAddress
import org.jitsi.config.JitsiConfig.Companion.newConfig as configSource

class HarvestConfig {
    val logger = createLogger()

    val useLinkLocalAddresses: Boolean by config {
        "org.ice4j.ice.harvest.DISABLE_LINK_LOCAL_ADDRESSES".from(configSource)
            .transformedBy { !it }
        "ice4j.harvest.use-link-local-addresses".from(configSource)
    }
    fun useLinkLocalAddresses() = useLinkLocalAddresses

    val udpReceiveBufferSize: Int? by optionalconfig {
        "org.ice4j.ice.harvest.AbstractUdpListener.SO_RCVBUF".from(configSource)
        "ice4j.harvest.udp.receive-buffer-size".from(configSource)
    }
    fun udpReceiveBufferSize() = udpReceiveBufferSize

    val udpSocketPoolSize: Int by config {
        "ice4j.harvest.udp.socket-pool-size".from(configSource)
    }

    fun udpSocketPoolSize() = udpSocketPoolSize

    val useIpv6: Boolean by config {
        "org.ice4j.ipv6.DISABLED".from(configSource)
            .transformedBy { !it }
        "ice4j.harvest.use-ipv6".from(configSource)
    }
    fun useIpv6() = useIpv6

    val useDynamicPorts: Boolean by config {
        "org.ice4j.ice.harvest.USE_DYNAMIC_HOST_HARVESTER".from(configSource)
        "ice4j.harvest.udp.use-dynamic-ports".from(configSource)
    }
    fun useDynamicPorts() = useDynamicPorts

    var timeout = 90 * 1000
    fun timeout() = timeout

    val stunMappingCandidateHarvesterAddresses: List<String> by config {
        "org.ice4j.ice.harvest.STUN_MAPPING_HARVESTER_ADDRESSES".from(configSource)
            .convertFrom<String> { it.split(",") }
        "ice4j.harvest.mapping.stun.addresses".from(configSource)
    }
    fun stunMappingCandidateHarvesterAddresses() = stunMappingCandidateHarvesterAddresses

    val enableAwsHarvester: Boolean by config {
        "org.ice4j.ice.harvest.DISABLE_AWS_HARVESTER".from(configSource).transformedBy { !it }
        "ice4j.harvest.mapping.aws.enabled".from(configSource)
    }
    fun enableAwsHarvester() = enableAwsHarvester

    val forceAwsHarvester: Boolean by config {
        "org.ice4j.ice.harvest.FORCE_AWS_HARVESTER".from(configSource).transformedBy { !it }
        "ice4j.harvest.mapping.aws.force".from(configSource)
    }
    fun forceAwsHarvester() = enableAwsHarvester

    private val legacyNatHarvesterLocalAddress: String? by optionalconfig {
        "org.ice4j.ice.harvest.NAT_HARVESTER_LOCAL_ADDRESS".from(configSource)
    }

    private val legacyNatHarvesterPublicAddress: String? by optionalconfig {
        "org.ice4j.ice.harvest.NAT_HARVESTER_PUBLIC_ADDRESS".from(configSource)
    }

    private val staticMappingsFromNewConfig: Set<StaticMapping> by config {
        "ice4j.harvest.mapping.static-mappings".from(configSource)
            .convertFrom<List<Config>> { cfg ->
                cfg.map {
                    val localPort = if (it.hasPath("local-port")) it.getInt("local-port") else null
                    val publicPort = if (it.hasPath("public-port")) it.getInt("public-port") else null
                    if ((localPort == null || publicPort == null) && localPort != publicPort) {
                        throw ConfigException.UnableToRetrieve.ConditionNotMet(
                            "Inconsistent value for local-port and public-port, both must be present or both missing."
                        )
                    }
                    StaticMapping(
                        localAddress = it.getString("local-address"),
                        publicAddress = it.getString("public-address"),
                        localPort = localPort,
                        publicPort = publicPort,
                        name = if (it.hasPath("name")) it.getString("name") else null
                    )
                }.toSet()
            }
    }

    /**
     * The list of IP addresses that are allowed to be used for host candidate allocations. If no addresses are
     * specified, any address is allowed.
     */
    val allowedAddresses: List<InetAddress> by config {
        "org.ice4j.ice.harvest.ALLOWED_ADDRESSES".from(configSource).convertFrom<String> { l ->
            l.split(";").filter { it.isNotEmpty() }.mapNotNull { it.toInetAddress() }
        }
        "ice4j.harvest.allowed-addresses".from(configSource).convertFrom<List<String>> { l ->
            l.mapNotNull { it.toInetAddress() }
        }
    }

    /**
     * The list of IP addresses that are not allowed to be used for host candidate allocations. If no addresses are
     * specified, any address is allowed.
     */
    val blockedAddresses: List<InetAddress> by config {
        "org.ice4j.ice.harvest.BLOCKED_ADDRESSES".from(configSource).convertFrom<String> { l ->
            l.split(";").filter { it.isNotEmpty() }.mapNotNull { it.toInetAddress() }
        }
        "ice4j.harvest.blocked-addresses".from(configSource).convertFrom<List<String>> { l ->
            l.mapNotNull { it.toInetAddress() }
        }
    }

    /**
     * The allowed interfaces for host candidate allocations. If none are specified all interfaces are allowed unless
     * blocked.
     */
    val allowedInterfaces: List<String> by config {
        "org.ice4j.ice.harvest.ALLOWED_INTERFACES".from(configSource).convertFrom<String> { l ->
            l.split(";").filter { it.isNotEmpty() }
        }
        "ice4j.harvest.allowed-interfaces".from(configSource)
    }

    /**
     * The blocked interfaces for host candidate allocations. Note that this can not be used in conjunction with
     * [allowedInterfaces]. If [allowedInterfaces] are defined then [blockedInterfaces] is not used.
     */
    val blockedInterfaces: List<String> by config {
        "org.ice4j.ice.harvest.BLOCKED_INTERFACES".from(configSource).convertFrom<String> { l ->
            l.split(";").filter { it.isNotEmpty() }
        }
        "ice4j.harvest.blocked-interfaces".from(configSource)
    }

    val staticMappings: Set<StaticMapping> = let {
        if (legacyNatHarvesterLocalAddress != null && legacyNatHarvesterPublicAddress != null) {
            setOf(
                StaticMapping(legacyNatHarvesterLocalAddress!!, legacyNatHarvesterPublicAddress!!),
                *staticMappingsFromNewConfig.toTypedArray()
            )
        } else {
            staticMappingsFromNewConfig
        }
    }

    private fun String.toInetAddress(): InetAddress? = try {
        InetAddress.getByName(this)
    } catch (e: Exception) {
        logger.warn("Invalid address, will not use it as allowed/blocked: $this")
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
