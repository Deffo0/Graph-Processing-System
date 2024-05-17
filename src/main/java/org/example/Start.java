package org.example;

import org.example.server.GSPServer;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Logger;

public class Start {
    private final static Logger logger = Logger.getLogger(Start.class.getName());
    public static void main(String[] args) throws IOException, InterruptedException {

        Configuration configuration = new Configuration();
        String classpath = System.getProperty("java.class.path");

        logger.info("Starting server...");

        
        ProcessBuilder serverProcessBuilder = new ProcessBuilder(
                "docker",
                "run",
                // "-d",
                // "org.example.server.GSPServer",
                "--network",
                "gsp",
                "--ip",
                configuration.getServerAddress(),
                "--expose",
                String.valueOf(configuration.getServerPort()),
                "--expose",
                String.valueOf(configuration.getRmiRegistryPort()),
                "gsp-server",
                configuration.getServerAddress(),
                String.valueOf(configuration.getServerPort()),
                String.valueOf(configuration.getRmiRegistryPort())
        );
        System.out.println(serverProcessBuilder.command());
        serverProcessBuilder.redirectErrorStream(true);
        Process serverProcess = serverProcessBuilder.start();
        
        // System.out.println();
        // GSPServer.main(
        //         new String[] {
        //                 configuration.getServerAddress(),
        //                 String.valueOf(configuration.getServerPort()),
        //                 String.valueOf(configuration.getRmiRegistryPort())
        //         }
        // );
        logger.info("Server started.");
        Thread.sleep(2000);

        try {
            logger.info("Starting clients...");
            List<Process> clientProcesses = getClientProcesses(configuration, classpath);
            logger.info("Clients started.");
            for (Process clientProcess : clientProcesses) {
                clientProcess.waitFor();
            }
            logger.info("All clients finished.");

            serverProcess.waitFor();
        } catch (IOException | InterruptedException e) {
            logger.severe("An error occurred: " + e.getMessage());
        } finally {
            // serverProcess.destroy();
        }
    }

    private static List<Process> getClientProcesses(Configuration configuration, String classpath) throws IOException {
        String[] nodeAddresses = configuration.getNodeAddresses();
        List<Process> clientProcesses = new ArrayList<>();

        for (int i = 0; i < nodeAddresses.length; i++) {
            String nodeAddress = nodeAddresses[i];
            logger.info("Starting client " + i + "...");
            ProcessBuilder clientProcessBuilder = new ProcessBuilder(
                    "java",
                    "-cp",
                    classpath,
                    "org.example.client.GSPClient",
                    String.valueOf(i),
                    nodeAddress,
                    configuration.getServerAddress(),
                    String.valueOf(configuration.getRmiRegistryPort()),
                    configuration.getServerName()
            );
            clientProcessBuilder.redirectErrorStream(true);
            Process clientProcess = clientProcessBuilder.start();
            logger.info("Client " + i + " started.");
            clientProcesses.add(clientProcess);
        }
        return clientProcesses;
    }
}
