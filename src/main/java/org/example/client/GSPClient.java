package org.example.client;

import org.example.RMIInterface.GraphBatchProcessor;
import org.example.log.JsonFormatter;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.rmi.NotBoundException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.List;
import java.util.Random;
import java.util.logging.*;

public class GSPClient {
    private static final Logger logger = Logger.getLogger(GSPClient.class.getName());

    public GSPClient() {
        super();
    }

    public static void main(String[] args) throws NotBoundException, IOException {
        if (args.length != 5) {
            logger.severe("Usage: java -jar client.jar <clientId> <clientAddress> <serverAddress> <rmiRegistryPort> <serviceName>");
            return;
        }

        String clientId = args[0];
        String serverAddress = args[2];
        int rmiRegistryPort = Integer.parseInt(args[3]);
        String name = args[4];
        initLogger(clientId);
        logger.info("Client started");
        logger.info("Client ID: " + clientId);
        logger.info("Server address: " + serverAddress);
        logger.info("RMI registry port: " + rmiRegistryPort);
        logger.info("Service name: " + name);
        logger.info("Generating batch...");
        String batch = generateBatch(clientId);
        logger.info("Batch generated");
        logger.info("Connecting to server...");
        Registry registry = LocateRegistry.getRegistry(serverAddress, rmiRegistryPort);
        logger.info("Looking up service: " + name);
        GraphBatchProcessor graphBatchProcessor = (GraphBatchProcessor) registry.lookup(name);

        try {
            logger.info("Processing batch:\n" + batch);
            long startTime = System.currentTimeMillis();
            List<Integer> result = graphBatchProcessor.processBatch(batch);
            long endTime = System.currentTimeMillis();
            logger.info("Result: " + result.toString() + ", Time taken: " + (endTime - startTime) + "ms");
        } catch (Exception e) {
            logger.severe("An error occurred: " + e.getMessage());
        }
    }

    private static String generateBatch(String clientId) {
        String fileName = "src/main/resources/instructions_" + clientId + ".txt";
        try {
            return generateRandomInstructions(fileName);
        } catch (Exception e) {
            logger.severe("An error occurred: " + e.getMessage());
        }

        return "null";
    }

    private static String generateRandomInstructions(String fileName) throws Exception {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(fileName))) {
            StringBuilder batchString = new StringBuilder();
            Random random = new Random();
            for (int i = 0; i < 10; i++) {
                char operation = random.nextBoolean() ? 'A' : 'D'; // Randomly choose add or delete operation
                int src = random.nextInt(10); // Random source node
                int dest = random.nextInt(10); // Random destination node
                writer.write(operation + " " + src + " " + dest);
                writer.newLine();
                batchString.append(operation).append(" ").append(src).append(" ").append(dest).append("\n");
            }
            // Add queries for shortest path
            for (int i = 0; i < 5; i++) {
                int src = random.nextInt(10); // Random source node
                int dest = random.nextInt(10); // Random destination node
                writer.write("Q" + " " + src + " " + dest);
                writer.newLine();
                batchString.append("Q").append(" ").append(src).append(" ").append(dest).append("\n");
            }
            writer.write("F");

            return batchString.toString();
        }
    }

    private static void initLogger(String clientId) throws IOException {
        Handler fileHandler = new FileHandler("src/main/resources/GSPClient_" + clientId + ".log");
        fileHandler.setFormatter(new JsonFormatter());
        logger.addHandler(fileHandler);
        logger.setLevel(Level.INFO);
    }
}
