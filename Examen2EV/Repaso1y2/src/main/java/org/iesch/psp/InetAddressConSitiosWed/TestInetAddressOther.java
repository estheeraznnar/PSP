package org.iesch.psp.InetAddressConSitiosWed;

import java.net.InetAddress;
import java.net.UnknownHostException;

public class TestInetAddressOther {
    private static final String HEADER = "**********************************************************";

    public static void main(String[] args) {
        try {
            InetAddress local = InetAddress.getLocalHost();
            System.out.println(HEADER);
            System.out.println("getLocalHost : " + local);
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }

        InetAddress ip = null;
        try {
            System.out.println(HEADER);
            ip = InetAddress.getByName("www.amazon.es");
            test(ip);

            System.out.println(HEADER);
            ip = InetAddress.getByName("www.github.com");
            test(ip);

            System.out.println(HEADER);
            ip = InetAddress.getByName("www.stackoverflow.com");
            test(ip);

            System.out.println(HEADER);
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
    }

    private static void test(InetAddress ip) {
        System.out.println("\t getByName: " + ip);
        System.out.println("\t getHostName: " + ip.getHostName());
        System.out.println("\t getHostAddress: " + ip.getHostAddress());
        System.out.println("\t getCanonicalHostName: " + ip.getCanonicalHostName());

        try {
            InetAddress[] ips = InetAddress.getAllByName(ip.getHostName());
            System.out.println("\t direcciones para " + ip.getHostName() + ":");
            for (int i = 0; i < ips.length; i++) {
                System.out.println("\t\t " + ips[i]);
            }
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
    }
}