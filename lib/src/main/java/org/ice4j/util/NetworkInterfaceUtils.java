package org.ice4j.util;

import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Spliterator;
import java.util.Spliterators;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

public class NetworkInterfaceUtils {
    public static Stream<NetworkInterface> networkInterfaces() throws SocketException {
        ArrayList<NetworkInterface> list = new ArrayList<>();
        Enumeration<NetworkInterface> result = NetworkInterface.getNetworkInterfaces();
        while (result.hasMoreElements()) {
            list.add(result.nextElement());
        }

        return StreamSupport.stream(
                Spliterators.spliterator(
                        list.toArray(),
                        Spliterator.DISTINCT | Spliterator.IMMUTABLE | Spliterator.NONNULL),
                false);
    }
}
