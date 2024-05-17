package org.example.server;

import org.example.RMIInterface.GraphBatchProcessor;

import java.io.FileNotFoundException;
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
            System.out.println("GraphBatchProcessor bound");
        } catch (Exception e) {
            System.err.println("GraphBatchProcessor exception:");
            logger.severe("An error occurred: " + e.getMessage());
        }
    }

    private static void initLogger() throws IOException {
        logger.setLevel(Level.INFO);
        Handler fileHandler = new FileHandler("src/main/resources/GSPServer.log");
        logger.addHandler(fileHandler);
        fileHandler.setFormatter(new SimpleFormatter());
    }

    @Override
    public List<Integer> processBatch(String batch) throws RemoteException {
        if (!batch.endsWith("\n")) {
            batch += "\n";
        }

        String[] lines = batch.split("\n");
        List<Integer> result = new ArrayList<>();

        for (String line : lines) {
            String[] parts = line.split(" ");
            char operation = parts[0].charAt(0);
            int src = Integer.parseInt(parts[1]);
            int dest = Integer.parseInt(parts[2]);

            switch (operation) {
                case 'Q':
                    int distance = graph.shortestPath(src, dest, false);
                    result.add(distance);
                    logger.info("Q " + src + " " + dest + " , Answer=" + distance);
                    break;
                case 'A':
                    graph.addEdge(src, dest);
                    logger.info("A " + src + " " + dest);
                    break;
                case 'D':
                    graph.removeEdge(src, dest);
                    logger.info("D " + src + " " + dest);
                    break;
                case 'F':
                    return result;
            }
        }

        return result;
    }
}
