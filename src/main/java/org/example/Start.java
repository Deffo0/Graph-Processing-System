package org.example;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
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
                "--name",
                "gsp-server",
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

        try (BufferedReader reader = new BufferedReader(new InputStreamReader(serverProcess.getInputStream()))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(" ");
                if(parts.length == 2 && parts[1].toLowerCase().equals("r")){
                    logger.info("Server started.");
                    break;
                }
            }
        }

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
                    configuration.getServerName(),
                    String.valueOf(configuration.getMaxGraphNodes()),
                    String.valueOf(configuration.getWritePercentage()),
                    "10"
            );
            clientProcessBuilder.redirectErrorStream(true);
            Process clientProcess = clientProcessBuilder.start();
            logger.info("Client " + i + " started.");
            clientProcesses.add(clientProcess);
        }
        return clientProcesses;
    }
}
