import java.io.*;
import java.rmi.Naming;
import java.util.Objects;
import java.util.Random;
import java.util.logging.*;
public class GraphClient {
    private static final Logger logger = Logger.getLogger(GraphClient.class.getName());

    public static void main(String[] args) {
        try {
            Handler fileHandler = new FileHandler("GraphClient.log");
            fileHandler.setFormatter(new SimpleFormatter());
            logger.addHandler(fileHandler);
            logger.setLevel(Level.INFO);

            Configuration config = new Configuration();
            GraphService graphService = (GraphService) Naming.lookup("//" + config.getServerAddress() + ":" + config.getRmiregistryPort() + "/GraphService");

            // Read initial graph from file and add edges to the server
            String initialGraphFile = "initial_graph.txt";
            initializeGraphFromFile(graphService, initialGraphFile);

            // Generate random instructions and save them in a file
            String instructionsFile = "instructions.txt";
//            generateRandomInstructions(instructionsFile);

            // Read instructions from the file and send them to the server
            // Measure performance and log results
            // Sleep for random amount of time between 1 to 10 seconds
            // Repeat the process
            while (true) {
                long startTime = System.currentTimeMillis();
                //Read instructions from file and send to server and measure performance and log results in GrpahClient.log
                try (BufferedReader reader = new BufferedReader(new FileReader(instructionsFile)) ) {
                    String line;
                    while (!Objects.equals(line = reader.readLine(), "F")) {
                        String[] tokens = line.split(" ");
                        char operation = tokens[0].charAt(0);
                        int src = Integer.parseInt(tokens[1]);
                        int dest = Integer.parseInt(tokens[2]);
                        long operationStartTime = System.currentTimeMillis();

                        switch (operation) {
                            case 'A':
                                logger.info("Adding edge: " + src + " -> " + dest);
                                graphService.addEdge(src, dest);
                                logger.info("Added edge: " + src + " -> " + dest);
                                logger.info("Finished in  " + (System.currentTimeMillis()-operationStartTime) + " milliseconds");

                                break;
                            case 'D':
                                logger.info("Removing edge: " + src + " -> " + dest);
                                graphService.removeEdge(src, dest);
                                logger.info("Removed edge: " + src + " -> " + dest);
                                logger.info("Finished in  " + (System.currentTimeMillis()-operationStartTime) + " milliseconds");
                                break;
                            case 'Q':
                                logger.info("Querying shortest path from " + src + " to " + dest);
                                int shortestPath = graphService.shortestPath(src, dest);
                                logger.info("Shortest path from " + src + " to " + dest + ": " + shortestPath);
                                logger.info("Finished in  " + (System.currentTimeMillis()-operationStartTime) + " milliseconds");
                                break;
                            default:
                                logger.warning("Invalid operation: " + operation);
                        }
                    }
                }

                long elapsedTime = System.currentTimeMillis() - startTime;
                logger.info("Time elapsed: " + elapsedTime + " milliseconds");

                int sleepTime = new Random().nextInt(10) + 1;
                logger.info("Sleeping for " + sleepTime + " seconds...");
                Thread.sleep(sleepTime * 1000);
            }
        } catch (Exception e) {
            System.err.println("Graph client exception: " + e.toString());
            e.printStackTrace();
        }
    }

    private static void initializeGraphFromFile(GraphService graphService, String fileName) throws IOException {
        try (BufferedReader reader = new BufferedReader(new FileReader(fileName))) {
            String line;
            while (!Objects.equals(line = reader.readLine(), "S")) {
                String[] tokens = line.split(" ");
                int src = Integer.parseInt(tokens[0]);
                int dest = Integer.parseInt(tokens[1]);
                graphService.addEdge(src, dest);
            }
        }
    }

    private static void generateRandomInstructions(String fileName) throws Exception {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(fileName))) {
            Random random = new Random();
            for (int i = 0; i < 10; i++) {
                char operation = random.nextBoolean() ? 'A' : 'D'; // Randomly choose add or delete operation
                int src = random.nextInt(10); // Random source node
                int dest = random.nextInt(10); // Random destination node
                writer.write(operation + " " + src + " " + dest);
                writer.newLine();
            }
            // Add queries for shortest path
            for (int i = 0; i < 5; i++) {
                int src = random.nextInt(10); // Random source node
                int dest = random.nextInt(10); // Random destination node
                writer.write("Q" + " " + src + " " + dest);
                writer.newLine();
            }
            writer.write("F");


        }
    }
}
