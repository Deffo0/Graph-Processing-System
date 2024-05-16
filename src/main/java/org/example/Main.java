package org.example;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

public class Main {
    public static void main(String[] args) throws IOException, InterruptedException {
        Logger logger = Logger.getLogger(Main.class.getName());
        Configuration configuration = new Configuration();
        
        ProcessBuilder serverProcessBuilder = new ProcessBuilder(
                "java",
                "-cp",
                "D:\\Library\\Faculty of Engineering\\Year 4\\2nd Semester\\Solutions\\Distributed Systems\\RMI\\target\\classes",
                "org.example.server.GSPServer",
                configuration.getServerAddress(),
                String.valueOf(configuration.getServerPort()),
                String.valueOf(configuration.getRmiRegistryPort())
        );
        serverProcessBuilder.redirectErrorStream(true);
        Process serverProcess = serverProcessBuilder.start();

        // Wait for the server process to start (optional)
        Thread.sleep(2000); // Adjust this delay as needed

        try {
            String[] nodeAddresses = configuration.getNodeAddresses();
            List<Process> clientProcesses = new ArrayList<>();

            for (int i = 0; i < nodeAddresses.length; i++) {
                String nodeAddress = nodeAddresses[i];

                // Start the GSPClient process
                ProcessBuilder clientProcessBuilder = new ProcessBuilder(
                        "java",
                        "-cp",
                        "D:\\Library\\Faculty of Engineering\\Year 4\\2nd Semester\\Solutions\\Distributed Systems\\RMI\\target\\classes",
                        "org.example.client.GSPClient",
                        String.valueOf(i),
                        nodeAddress,
                        configuration.getServerAddress(),
                        String.valueOf(configuration.getRmiRegistryPort()),
                        configuration.getServerName()
                );
                clientProcessBuilder.redirectErrorStream(true);
                Process clientProcess = clientProcessBuilder.start();
                clientProcesses.add(clientProcess);
            }

            // Wait for all client processes to finish
            for (Process clientProcess : clientProcesses) {
                clientProcess.waitFor();
            }

            // Optional: Wait for the server process to finish
            serverProcess.waitFor();

        } catch (IOException | InterruptedException e) {
            logger.severe("An error occurred: " + e.getMessage());
        } finally {
            // Optional: Destroy the server process
            serverProcess.destroy();
        }
    }
}
