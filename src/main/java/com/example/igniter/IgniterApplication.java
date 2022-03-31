package com.example.igniter;

import org.apache.ignite.Ignite;
import org.apache.ignite.IgniteCache;
import org.apache.ignite.Ignition;
import org.apache.ignite.client.ClientCache;
import org.apache.ignite.client.ClientException;
import org.apache.ignite.client.IgniteClient;
import org.apache.ignite.configuration.ClientConfiguration;
import org.apache.ignite.configuration.IgniteConfiguration;
import org.apache.ignite.lang.IgniteRunnable;
import org.apache.ignite.resources.IgniteInstanceResource;
import org.apache.ignite.spi.discovery.tcp.TcpDiscoverySpi;
import org.apache.ignite.spi.discovery.tcp.ipfinder.multicast.TcpDiscoveryMulticastIpFinder;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.util.Collections;

@SpringBootApplication
public class IgniterApplication implements CommandLineRunner {

    public static void main(String[] args) {
        SpringApplication.run(IgniterApplication.class, args);
    }

    @Override
    public void run(String... args) throws Exception {
//        System.out.println("good started...");
//        // Preparing IgniteConfiguration using Java APIs
//        IgniteConfiguration cfg = new IgniteConfiguration();
//
//        // The node will be started as a client node.
//        cfg.setClientMode(true);
//
//        // Classes of custom Java logic will be transferred over the wire from this app.
//        cfg.setPeerClassLoadingEnabled(true);
//
//        // Setting up an IP Finder to ensure the client can locate the servers.
//        TcpDiscoveryMulticastIpFinder ipFinder = new TcpDiscoveryMulticastIpFinder();
//        ipFinder.setAddresses(Collections.singletonList("127.0.0.1:47500..47509"));
//        cfg.setDiscoverySpi(new TcpDiscoverySpi().setIpFinder(ipFinder));
//
//        // Starting the node
//        Ignite ignite = Ignition.start(cfg);
//
//        // Create an IgniteCache and put some values in it.
////        IgniteCache<Integer, String> cache = ignite.getOrCreateCache("myCache");
////        cache.put(1, "Hello");
////        cache.put(2, "World!");
////
////        System.out.println(">> Created the cache and add the values.");
////
////        // Executing custom Java compute task on server nodes.
////        ignite.compute(ignite.cluster().forServers()).broadcast(new RemoteTask());
////
////        System.out.println(">> Compute task is executed, check for output on the server nodes.");
//
//        // Disconnect from the cluster.
//        ignite.close();
        ClientConfiguration cfg = new ClientConfiguration()
                .setAddresses("localhost:10800")
                .setPartitionAwarenessEnabled(true);

        try (IgniteClient client = Ignition.startClient(cfg)) {
            //ClientCache<Integer, String> cache = client.cache("myCache");
            // Put, get or remove data from the cache...
            //System.out.println("cahe: "+cache.size());
            // Create an IgniteCache and put some values in it.
            ClientCache<Integer, String> cache = client.getOrCreateCache("myCache");
            cache.put(1, "Hello");
            cache.put(2, "World!");

        System.out.println(">> Created the cache and add the values.");

        // Executing custom Java compute task on server nodes.
        client.compute(client.cluster().forServers()).broadcast(new RemoteTask());

        System.out.println(">> Compute task is executed, check for output on the server nodes.");


        } catch (ClientException e) {
            System.err.println(e.getMessage());
        }
    }
    private static class RemoteTask implements IgniteRunnable {
        @IgniteInstanceResource
        Ignite ignite;

        @Override public void run() {
            System.out.println(">> Executing the compute task");

            System.out.println(
                    "   Node ID: " + ignite.cluster().localNode().id() + "\n" +
                            "   OS: " + System.getProperty("os.name") +
                            "   JRE: " + System.getProperty("java.runtime.name"));

            IgniteCache<Integer, String> cache = ignite.cache("myCache");

            System.out.println(">> " + cache.get(1) + " " + cache.get(2));
        }
    }

}
