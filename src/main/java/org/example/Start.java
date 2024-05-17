package org.example;

import org.example.server.GSPServer;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

public class Start {
    public static void main(String[] args) throws IOException, InterruptedException {
        Logger logger = Logger.getLogger(Start.class.getName());
        Configuration configuration = new Configuration();
        String classpath = System.getProperty("java.class.path");
        
        // ProcessBuilder serverProcessBuilder = new ProcessBuilder(
        //         "java",
        //         "-cp",
        //         classpath,
        //         "org.example.server.GSPServer",
        //         configuration.getServerAddress(),
        //         String.valueOf(configuration.getServerPort()),
        //         String.valueOf(configuration.getRmiRegistryPort())
        // );
        // serverProcessBuilder.redirectErrorStream(true);
        // Process serverProcess = serverProcessBuilder.start();

        GSPServer.main(
                new String[] {
                        configuration.getServerAddress(),
                        String.valueOf(configuration.getServerPort()),
                        String.valueOf(configuration.getRmiRegistryPort())
                }
        );

        Thread.sleep(2000);

        try {
            List<Process> clientProcesses = getClientProcesses(configuration, classpath);

            for (Process clientProcess : clientProcesses) {
                clientProcess.waitFor();
            }

            // serverProcess.waitFor();
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
            clientProcesses.add(clientProcess);
        }
        return clientProcesses;
    }
}
