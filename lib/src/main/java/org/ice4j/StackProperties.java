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
package org.ice4j;

import org.ice4j.ice.*;

import java.util.*;
import java.util.logging.*;

/**
 * The class contains a number of property names and their default values that
 * we use to configure the behavior of the ice4j stack.
 *
 * @author Emil Ivov
 */
public class StackProperties
{
    /**
     * Our class logger.
     */
    private static final Logger logger
        = Logger.getLogger(StackProperties.class.getName());

    /**
     * The name of the property containing the number of binds that we should
     * should execute in case a port is already bound to (each retry would be on
     * a new random port).
     */
    public static final String BIND_RETRIES = "org.ice4j.BIND_RETRIES";

    /**
     * The default number of binds that we would try
     * implementation should execute in case a port is already bound to (each
     * retry would be on a different port).
     */
    public static final int BIND_RETRIES_DEFAULT_VALUE = 50;

    /**
     * The name of the property that tells if we should bind to the
     * wildcard address instead of the (usually more specific) harvest candidate
     * addresses.
     *
     * The wildcard is a special local IP address. It usually means "any".
     */
    public static final String BIND_WILDCARD = "org.ice4j.BIND_WILDCARD";

    /**
     * The number of milliseconds a client transaction should wait before
     * retransmitting, after it has sent a request for the first time.
     */
    public static final String FIRST_CTRAN_RETRANS_AFTER
                                        = "org.ice4j.FIRST_CTRAN_RETRANS_AFTER";

    /**
     * The maximum number of milliseconds that an exponential client
     * retransmission timer can reach.
     */
    public static final String MAX_CTRAN_RETRANS_TIMER
                                    = "org.ice4j.MAX_CTRAN_RETRANS_TIMER";

    /**
     * Indicates whether a client transaction should be kept after a response
     * is received rather than destroying it which is the default.
     */
    public static final String KEEP_CRANS_AFTER_A_RESPONSE
                                = "org.ice4j.KEEP_CRANS_AFTER_A_RESPONSE";

    /**
     * The maximum number of retransmissions a client transaction should send.
     */
    public static final String MAX_CTRAN_RETRANSMISSIONS
                                = "org.ice4j.MAX_RETRANSMISSIONS";

    /**
     * The name of the property that tells the stack whether or not it should
     * let the application see retransmissions of incoming requests.
     */
    public static final String PROPAGATE_RECEIVED_RETRANSMISSIONS
                        = "org.ice4j.PROPAGATE_RECEIVED_RETRANSMISSIONS";

    /**
     * A property that allows us to specify whether we would expect link local
     * IPv6 addresses to be able to reach globally routable ones.
     */
    public static final String ALLOW_LINK_TO_GLOBAL_REACHABILITY
                                = "true";

    /**
     * The name of the property that allows us to tell the stack to always
     * sign STUN messages with a FINGERPRINT attribute.
     */
    public static final String ALWAYS_SIGN
                                = "org.ice4j.ALWAYS_SIGN";

    /**
     * Tells the stack whether to reject all incoming requests that do not
     * carry a MESSAGE-INTEGRITY header.
     */
    public static final String REQUIRE_MESSAGE_INTEGRITY
                                    = "org.ice4j.REQUIRE_MESSAGE_INTEGRITY";

    /**
     * The name of the property that can be used to disable STUN keep alives.
     * Set to <tt>true</tt> to disable.
     */
    public static final String NO_KEEP_ALIVES = "org.ice4j.NO_KEEP_ALIVES";

    /**
     * Returns the String value of the specified property (minus all
     * encompassing whitespaces)and null in case no property value was mapped
     * against the specified propertyName, or in case the returned property
     * string had zero length or contained whitespaces only.
     *
     * @param propertyName the name of the property that is being queried.
     *
     * @return the result of calling the property's toString method and null in
     * case there was no value mapped against the specified
     * <tt>propertyName</tt>, or the returned string had zero length or
     * contained whitespaces only.
     */
    public static String getString(String propertyName)
    {
        Object obj = System.getProperty(propertyName);
        String str;

        if (obj == null)
        {
            str = null;
        }
        else
        {
            str = obj.toString().trim();
            if (str.length() == 0)
                str = null;
        }
        return str;
    }

    /**
     * Returns the String array of the specified property, or null in case
     * the returned property string array had zero length.
     *
     * @param propertyName the name of the property that is being queried.
     * @param  regex the delimiting regular expression
     *
     * @return  the array of strings computed by splitting the specified
     * property value around matches of the given regular expression
     */
    public static String[] getStringArray(String propertyName, String regex)
    {
        String str = getString(propertyName);
        if (str == null)
            return null;

        String[] parts = str.split(regex);

        // Remove mal-formatted entries.
        List<String> res = new ArrayList<>();
        for (String s : parts)
            if (s != null && s.trim().length() != 0)
                res.add(s);

        if (res.size() == 0)
            return null;

        return res.toArray(new String[res.size()]);
    }

    /**
     * Returns the value of a specific property as a signed decimal integer. If
     * a property with the specified property name exists, its string
     * representation is parsed into a signed decimal integer according to the
     * rules of {@link Integer#parseInt(String)}. If parsing the value as a
     * signed decimal integer fails or there is no value associated with the
     * specified property name, <tt>defaultValue</tt> is returned.
     *
     * @param propertyName the name of the property to get the value of as a
     * signed decimal integer
     * @param defaultValue the value to be returned if parsing the value of the
     * specified property name as a signed decimal integer fails or there is no
     * value associated with the specified property name in the System
     * properties.
     * @return the value of the property with the specified name in the System
     * properties as a signed decimal integer;
     * <tt>defaultValue</tt> if parsing the value of the specified property name
     * fails or no value is associated among the System properties.
     */
    public static int getInt(String propertyName, int defaultValue)
    {
        String stringValue = getString(propertyName);
        int intValue = defaultValue;

        if ((stringValue != null) && (stringValue.length() > 0))
        {
            try
            {
                intValue = Integer.parseInt(stringValue);
            }
            catch (NumberFormatException ex)
            {
                logger.log(
                        Level.FINE,
                        propertyName + " does not appear to be an integer. "
                            + "Defaulting to " + defaultValue + ".",
                        ex);
            }
        }
        return intValue;
    }

    /**
     * Gets the value of a specific property as a <tt>boolean</tt>. If the
     * specified property name is associated with a value, the string
     * representation of the value is parsed into a <tt>boolean</tt> according
     * to the rules of {@link Boolean#parseBoolean(String)} . Otherwise,
     * <tt>defaultValue</tt> is returned.
     *
     * @param propertyName the name of the property to get the value of as a
     * <tt>boolean</tt>
     * @param defaultValue the value to be returned if the specified property
     * name is not associated with a value
     * @return the value of the property with the specified name as a
     * <tt>boolean</tt>; <tt>defaultValue</tt> if the property with the
     * specified name is not associated with a value
     */
    public static boolean getBoolean(String propertyName, boolean defaultValue)
    {
        String str = getString(propertyName);

        return (str == null) ? defaultValue : Boolean.parseBoolean(str);
    }
}
