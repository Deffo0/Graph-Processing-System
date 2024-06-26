package org.example.client;

import org.example.RMIInterface.GraphBatchProcessor;
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
    private static Random rngRequests, rngWrites, rngSleep;
    private static int writePercentage, maxGraphNodes, maxNumberOfRequests;
  
    public GSPClient() {
        super();
    }

    public static void main(String[] args) throws NotBoundException, IOException {
        if (args.length != 8) {
            logger.severe(
                    "Usage: java -jar client.jar <clientId> <clientAddress> <serverAddress> <rmiRegistryPort> <serviceName> <maxGraphNodes> <writePercentage> <maxNumberOfRequests>");
            return;
        }
        String clientId = args[0];
        String clientAddress = args[1];
        String serverAddress = args[2];
        int rmiRegistryPort = Integer.parseInt(args[3]);
        String name = args[4];
        maxNumberOfRequests = Integer.parseInt(args[7]);
        maxGraphNodes = Integer.parseInt(args[5]);
        writePercentage = Integer.parseInt(args[6]);

        rngRequests = new Random(Long.parseLong(clientId));
        rngWrites = new Random(clientAddress.hashCode());
        rngSleep = new Random(Long.parseLong(clientId) * clientAddress.hashCode());

        initLogger(clientId);
        logger.info("Client started");
        logger.info("Client ID: " + clientId);
        logger.info("Server address: " + serverAddress);
        logger.info("RMI registry port: " + rmiRegistryPort);
        logger.info("Service name: " + name);

        logger.info("Connecting to server...");
        Registry registry = LocateRegistry.getRegistry(serverAddress, rmiRegistryPort);
        logger.info("Looking up service: " + name);
        GraphBatchProcessor graphBatchProcessor = (GraphBatchProcessor) registry.lookup(name);

        // set number of requests to -ve value to run indefinitely
        for(int i = 0; i != maxNumberOfRequests; i++) {
            logger.info("Generating batch...");
            String batch = generateBatch(clientId);
            logger.info("Batch generated");
            try {
                logger.info("Processing batch:\n" + batch);
                long startTime = System.currentTimeMillis();
                List<Integer> result = graphBatchProcessor.processBatch(batch);
                long endTime = System.currentTimeMillis();
                logger.info("Result: " + result.toString() + ", Time taken: " + (endTime - startTime) + "ms");

                Thread.sleep(rngSleep.nextInt(10000) + 1000);

            } catch (Exception e) {
                logger.severe("An error occurred: " + e.getLocalizedMessage());
            }
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
        int numberOfRequestsGenerated = Math.max(rngRequests.nextInt(maxNumberOfRequests), 2);
        StringBuilder batchBuilder = new StringBuilder();

        for (int i = 0; i < numberOfRequestsGenerated; i++) {
            char operation = 'Q';
            if (rngWrites.nextInt(100) < writePercentage) {
                operation = rngRequests.nextBoolean() ? 'A' : 'D'; // Randomly choose add or delete operation
            }
            int src = rngRequests.nextInt(maxGraphNodes); // Random source node
            int dest = rngRequests.nextInt(maxGraphNodes); // Random destination node
            batchBuilder.append(operation).append(" ").append(src).append(" ").append(dest).append("\n");
        }
        batchBuilder.append("F");
        String batchString = batchBuilder.toString();
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(fileName))) {
            writer.write(batchString);
        }
        return batchString;
    }

    private static void initLogger(String clientId) throws IOException {
        Handler fileHandler = new FileHandler("src/main/resources/GSPClient_" + clientId + ".log");
        fileHandler.setFormatter(new SimpleFormatter());

        logger.addHandler(fileHandler);
        logger.setLevel(Level.INFO);
    }
}
