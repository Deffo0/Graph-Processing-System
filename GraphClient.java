import java.io.*;
import java.rmi.Naming;
import java.util.Objects;
import java.util.Random;

public class GraphClient {
    public static void main(String[] args) {
        try {
            Configuration config = new Configuration();
            GraphService graphService = (GraphService) Naming.lookup("//" + config.getServerAddress() + ":" + config.getServerPort() + "/GraphService");

            // Read initial graph from file and add edges to the server
            String initialGraphFile = "initial_graph.txt";
            initializeGraphFromFile(graphService, initialGraphFile);

            // Generate random instructions and save them in a file
            String instructionsFile = "instructions.txt";
            generateRandomInstructions(instructionsFile);

            // Read instructions from the file and send them to the server
            // Measure performance and log results
            // Sleep for random amount of time between 1 to 10 seconds
            // Repeat the process
            while (true) {
                long startTime = System.currentTimeMillis();

                // Read instructions from file and send to server
                // Measure elapsed time
                // Log results

                long elapsedTime = System.currentTimeMillis() - startTime;
                System.out.println("Time elapsed: " + elapsedTime + " milliseconds");

                // Sleep for random amount of time between 1 to 10 seconds
                int sleepTime = new Random().nextInt(10) + 1;
                System.out.println("Sleeping for " + sleepTime + " seconds...");
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
