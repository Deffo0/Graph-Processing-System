package org.example.server;

import org.example.RMIInterface.GraphBatchProcessor;
import org.example.log.AnalysisLevel;
import org.example.log.JsonFormatter;

import java.io.IOException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.*;

public class GSPServer implements GraphBatchProcessor {
    private static final Logger logger = Logger.getLogger(GSPServer.class.getName());
    private static Graph graph = null;

    public GSPServer() {
        super();
    }

    public static void main(String[] args) throws IOException {
        if (args.length != 3) {
            System.err.println("Usage: java -jar server.jar <serverAddress> <serverPort> <rmiRegistryPort>");
            return;
        }
        initLogger();

        String serverAddress = args[0];
        int serverPort = Integer.parseInt(args[1]);
        int rmiRegistryPort = Integer.parseInt(args[2]);
        graph = new Graph(logger, "src/main/resources/graph.txt");

        try {
            System.setProperty("java.rmi.server.hostname", serverAddress);
            String name = "GSP";
            GraphBatchProcessor gspServer = new GSPServer();
            GraphBatchProcessor stub = (GraphBatchProcessor) UnicastRemoteObject.exportObject(gspServer, serverPort);

            Registry registry = LocateRegistry.createRegistry(rmiRegistryPort);
            registry.rebind(name, stub);
            logger.info("GraphBatchProcessor bound");
        } catch (Exception e) {
            logger.severe("GraphBatchProcessor exception:" + e.getMessage());
        }finally {
            logger.info("Server INFO : ");
            logger.info("Server address: " + serverAddress);
            logger.info("Server port: " + serverPort);
            logger.info("RMI registry port: " + rmiRegistryPort);
            logger.info("Server started.");
        }
    }

    private static void initLogger() throws IOException {
        Handler fileHandler = new FileHandler("src/main/resources/GSPServer.log");
        fileHandler.setFormatter(new JsonFormatter());
        logger.addHandler(fileHandler);
        logger.setLevel(Level.INFO);
    }

    @Override
    public List<Integer> processBatch(String batch) throws RemoteException {
        if (!batch.endsWith("\n")) {
            batch += "\n";
        }

        String[] lines = batch.split("\n");
        List<Integer> result = new ArrayList<>();

        for (String line : lines) {
            logger.info("Processing line: " + line);
            String[] parts = line.split(" ");
            char operation = parts[0].charAt(0);
            int src = Integer.parseInt(parts[1]);
            int dest = Integer.parseInt(parts[2]);
            long operationStartTime = System.currentTimeMillis();

            switch (operation) {
                case 'Q':
                    logger.info("Querying shortest path from " + src + " to " + dest);
                    int distance = graph.shortestPath(src, dest, false);
                    result.add(distance);
                    logger.info("Shortest path from " + src + " to " + dest + ": " + distance);
                    logger.log(AnalysisLevel.ANALYSIS, "This is an ANALYSIS message");

                    logger.info("Finished in  " + (System.currentTimeMillis()-operationStartTime) + " milliseconds");
                    break;
                case 'A':
                    logger.info("Adding edge: " + src + " -> " + dest);
                    graph.addEdge(src, dest);
                    logger.info("Added edge: " + src + " -> " + dest);
                    logger.info("Finished in  " + (System.currentTimeMillis()-operationStartTime) + " milliseconds");
                    break;
                case 'D':
                    logger.info("Removing edge: " + src + " -> " + dest);
                    graph.removeEdge(src, dest);
                    logger.info("Removed edge: " + src + " -> " + dest);
                    logger.info("Finished in  " + (System.currentTimeMillis()-operationStartTime) + " milliseconds");
                    break;
                case 'F':
                    logger.info("Batch processing finished");
                    return result;
                default:
                    logger.warning("Invalid operation: " + operation);
            }
        }

        return result;
    }
}
