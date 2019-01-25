package com.trashsoftware.studio.xiangqi.connection;

import java.io.IOException;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.URL;
import java.net.URLConnection;
import java.util.Enumeration;
import java.util.Scanner;

public class Host {

    private static InetAddress getExternalIp() throws IOException {
        URL url = new URL("http://automation.whatismyip.com/n09230945.asp");
        URLConnection connection = url.openConnection();
        connection.addRequestProperty("Protocol", "Http/1.1");
        connection.addRequestProperty("Connection", "keep-alive");
        connection.addRequestProperty("Keep-Alive", "1000");
        connection.addRequestProperty("User-Agent", "Web-Agent");

        try (Scanner s = new Scanner(connection.getInputStream())) {
            return InetAddress.getByName(s.nextLine());
        }
    }

    private static boolean isIpBoundToNetworkInterface(InetAddress ip) {

        try {
            Enumeration<NetworkInterface> nets =
                    NetworkInterface.getNetworkInterfaces();

            while (nets.hasMoreElements()) {
                NetworkInterface intf = nets.nextElement();
                Enumeration<InetAddress> ips = intf.getInetAddresses();
                while (ips.hasMoreElements())
                    if (ip.equals(ips.nextElement()))
                        return true;
            }
        } catch (SocketException e) {
            // ignore
        }
        return false;
    }

    public static InetAddress getHost() throws IOException {
        InetAddress ip = getExternalIp();

        if (!isIpBoundToNetworkInterface(ip))
            throw new IOException("Could not find external ip");

        return ip;
    }
}
