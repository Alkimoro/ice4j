/*
 * ice4j, the OpenSource Java Solution for NAT and Firewall Traversal.
 *
 * Copyright @ 2015 Atlassian Pty Ltd
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
package org.ice4j.ice.harvest;

import org.ice4j.*;

import java.net.*;
import java.net.http.*;
import java.time.*;
import java.util.*;
import java.util.logging.*;

/**
 * Uses the Amazon AWS APIs to retrieve the public and private IPv4 addresses
 * for an EC2 instance.
 *
 * @author Emil Ivov
 */
public class AwsCandidateHarvester
    extends MappingCandidateHarvester
{
    /**
     * The <tt>Logger</tt> used by the <tt>AwsCandidateHarvester</tt>
     * class and its instances for logging output.
     */
    private static final Logger logger
        = Logger.getLogger(AwsCandidateHarvester.class.getName());

    /**
     * The <tt>HttpClient</tt> used by the <tt>AwsCandidateHarvester</tt>
     * class and its instances for making requests to IMDS.
     */
    private static final HttpClient httpClient = HttpClient.newBuilder()
            .connectTimeout(Duration.ofMillis(500))
            .build();

    /**
     * The URL where one obtains AWS public addresses.
     */
    private static final String PUBLIC_IP_URL
        = "http://169.254.169.254/latest/meta-data/public-ipv4";

    /**
     * The URL where one obtains AWS private/local addresses.
     */
    private static final String LOCAL_IP_URL
        = "http://169.254.169.254/latest/meta-data/local-ipv4";

    /**
     * The URL to get IMDSv2 API token for further meta-data requests.
     */
    private static final String IMDS_API_TOKEN_URL
            = "http://169.254.169.254/latest/api/token";

    /**
     * The HTTP header name to provide session token.
     */
    private static final String EC2_METADATA_TOKEN_HEADER
            = "X-aws-ec2-metadata-token";

    /**
     * The HTTP header name to request session token TTL.
     */
    private static final String EC2_METADATA_TOKEN_TTL_HEADER
            = "X-aws-ec2-metadata-token-ttl-seconds";

    /**
     * The default session token TTL value.
     */
    private static final String EC2_METADATA_TOKEN_DEFAULT_TTL
            = "21600";

    /**
     * Whether we are running on Amazon EC2.
     */
    private static Boolean RUNNING_ON_EC2 = null;

    /**
     * The addresses that we will use as a mask
     */
    private static TransportAddress mask;

    /**
     * The addresses that we will be masking
     */
    private static TransportAddress face;

    /**
     * Whether we have already checked and found the mapping addresses.
     */
    private static boolean addressChecked = false;

    /**
     * Creates an AWS harvester. The actual addresses wil be retrieved later,
     * during the first harvest.
     */
    public AwsCandidateHarvester()
    {
        super("aws");
    }

    /**
     * Sends HTTP GET queries to
     * <tt>http://169.254.169.254/latest/meta-data/local-ipv4</tt> and
     * <tt>http://169.254.169.254/latest/meta-data/public-ipv4</tt> to learn the
     * private (face) and public (mask) addresses of this EC2 instance.
     */
    private static synchronized void obtainEC2Addresses()
    {
        if (addressChecked)
            return;
        addressChecked = true;

        String localIPStr = null;
        String publicIPStr = null;

        try
        {
            String metaDataToken = fetch(IMDS_API_TOKEN_URL,
                    Collections.singletonMap(EC2_METADATA_TOKEN_TTL_HEADER, EC2_METADATA_TOKEN_DEFAULT_TTL), "PUT");
            Map<String, String> tokenHeader = Collections.singletonMap(EC2_METADATA_TOKEN_HEADER, metaDataToken);
            localIPStr = fetch(LOCAL_IP_URL, tokenHeader);
            publicIPStr = fetch(PUBLIC_IP_URL, tokenHeader);

            //now let's cross our fingers and hope that what we got above are
            //real IP addresses
            face = new TransportAddress(localIPStr, 9, Transport.UDP);
            mask = new TransportAddress(publicIPStr, 9, Transport.UDP);

            logger.info("Detected AWS local IP: " + face);
            logger.info("Detected AWS public IP: " + mask);


        }
        catch (Exception exc)
        {
            //whatever happens, we just log and fail
            logger.log(Level.INFO, "We failed to obtain EC2 instance addresses "
                + "for the following reason: ", exc);
            logger.info("String for local IP: " + localIPStr);
            logger.info("String for public IP: " + publicIPStr);
        }
    }

    /**
     * Returns the public (mask) address, or null.
     * @return the public (mask) address, or null.
     */
    @Override
    public TransportAddress getMask()
    {
        if (smellsLikeAnEC2())
        {
            obtainEC2Addresses();
            return mask;
        }
        return null;
    }

    /**
     * Returns the local (face) address, or null.
     * @return the local (face) address, or null.
     */
    @Override
    public TransportAddress getFace()
    {
        if (smellsLikeAnEC2())
        {
            obtainEC2Addresses();
            return face;
        }
        return null;
    }

    /**
     * Determines if there is a decent chance for the box executing this
     * application to be an AWS EC2 instance and returns <tt>true</tt> if so.
     *
     * @return <tt>true</tt> if there appear to be decent chances for this
     * machine to be an AWS EC2 and <tt>false</tt> otherwise.
     */
    public synchronized static boolean smellsLikeAnEC2()
    {
        if (RUNNING_ON_EC2 == null)
        {
            RUNNING_ON_EC2 = doTestEc2();
        }
        return RUNNING_ON_EC2;
    }

    /**
     * Tries to connect to an Amazon EC2-specific URL in order to determine
     * whether we are running on EC2.
     *
     * @return <tt>true</tt> if the connection succeeded, <tt>false</tt>
     * otherwise.
     */
    private static boolean doTestEc2()
    {
        try
        {
            String metaDataToken = fetch(IMDS_API_TOKEN_URL,
                    Collections.singletonMap(EC2_METADATA_TOKEN_TTL_HEADER, EC2_METADATA_TOKEN_DEFAULT_TTL), "PUT");

            return metaDataToken != null;
        }
        catch(Exception exc)
        {
            //ah! I knew you weren't one of those ...
            return false;
        }
    }

    /**
     * Retrieves the content at the specified <tt>url</tt>. No more, no less.
     *
     * @param url the URL we'd like to open and query.
     *
     * @param headers the HTTP headers to put into the request.
     *
     * @throws Exception if anything goes wrong.
     */
    private static String fetch(String url, Map<String, String> headers)
            throws Exception
    {
        return fetch(url, headers, "GET");
    }

    /**
     * Retrieves the content at the specified <tt>url</tt>. No more, no less.
     *
     * @param url the URL we'd like to open and query.
     *
     * @param headers the HTTP headers to put into the request.
     *
     * @param method the HTTP method we'd like to use.
     *
     * @return the String we retrieved from the URL.
     *
     * @throws Exception if anything goes wrong.
     */
    private static String fetch(String url, Map<String, String> headers, String method)
        throws Exception
    {
        HttpRequest.Builder builder = HttpRequest.newBuilder()
                .uri(new URI(url))
                .method(method, HttpRequest.BodyPublishers.noBody());
        for (Map.Entry<String, String> header : headers.entrySet())
        {
            builder.setHeader(header.getKey(), header.getValue());
        }
        HttpResponse<String> response = httpClient.send(builder.build(), HttpResponse.BodyHandlers.ofString());
        if (response.statusCode() != 200)
        {
            throw new Exception("Failed to fetch " + url + ". Response code: " + response.statusCode());
        }
        return response.body();
    }
}
