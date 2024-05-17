package org.example.server;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.*;
import java.util.logging.Logger;

public class Graph {
    private final Map<Integer, List<Integer>> adjacencyList;
    private static Logger logger;

    public Graph(Logger logger, String path) throws FileNotFoundException {
        this.adjacencyList = new HashMap<>();
        Graph.logger = logger;
        initGraph(path);
    }

    private void initGraph(String path) throws FileNotFoundException {
        File file = new File(path);
        Scanner scanner = new Scanner(file);

        while (scanner.hasNextLine()) {
            String line = scanner.nextLine();
            if (line.equals("S")) {
                logger.info("Graph initialization completed");
                logger.info("R");
                break;
            }
            String[] parts = line.split(" ");
            int src = Integer.parseInt(parts[0]);
            int dest = Integer.parseInt(parts[1]);
            addEdge(src, dest);
            logger.info("A " + src + " " + dest);
        }
    }

    public void addEdge(int src, int dest) {
        adjacencyList.putIfAbsent(src, new ArrayList<>());
        adjacencyList.get(src).add(dest);
    }

    public void removeEdge(int src, int dest) {
        List<Integer> neighbors = adjacencyList.get(src);
        if (neighbors != null) {
            neighbors.remove((Integer) dest);
        }
    }

    public int backtrackShortestPath(int src, int dest) {
        List<Integer> path = new ArrayList<>();
        path.add(src);
        int[] minDist = {Integer.MAX_VALUE};
        backtrackShortestPathUtil(src, dest, path, 0, minDist);
        return minDist[0] == Integer.MAX_VALUE ? -1 : minDist[0];
    }

    private void backtrackShortestPathUtil(int current, int dest, List<Integer> path, int distance, int[] minDist) {
        if (current == dest) {
            minDist[0] = Math.min(minDist[0], distance);
            return;
        }

        for (int neighbor : adjacencyList.getOrDefault(current, Collections.emptyList())) {
            if (!path.contains(neighbor)) {
                path.add(neighbor);
                backtrackShortestPathUtil(neighbor, dest, path, distance + 1, minDist);
                path.remove(path.size() - 1);
            }
        }
    }

    public int BFSShortestPath(int src, int dest) {
        if (!adjacencyList.containsKey(src) || !adjacencyList.containsKey(dest)) {
            return -1;
        }

        Queue<Integer> queue = new LinkedList<>();
        Map<Integer, Integer> distance = new HashMap<>();
        queue.offer(src);
        distance.put(src, 0);

        while (!queue.isEmpty()) {
            int current = queue.poll();
            if (current == dest) {
                return distance.get(current);
            }
            for (int neighbor : adjacencyList.get(current)) {
                if (!distance.containsKey(neighbor)) {
                    distance.put(neighbor, distance.get(current) + 1);
                    queue.offer(neighbor);
                }
            }
        }

        return -1;
    }
}
