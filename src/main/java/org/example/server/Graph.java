package org.example.server;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.*;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.logging.Logger;

public class Graph {
    private final Map<Integer, Set<Integer>> adjacencyList;
    private static Logger logger;
    private final ReentrantReadWriteLock lock;

    public Graph(Logger logger, String path) throws FileNotFoundException {
        this.adjacencyList = new HashMap<>();
        Graph.logger = logger;
        this.lock = new ReentrantReadWriteLock();
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
            logger.info("Adding edge: " + src + " -> " + dest);
            addEdge(src, dest);
            logger.info("Added edge: " + src + " -> " + dest);
        }
    }

    public void addEdge(int src, int dest) {
        lock.writeLock().lock();
        try {
            adjacencyList.putIfAbsent(src, new HashSet<>());
            adjacencyList.get(src).add(dest);
        } finally {
            lock.writeLock().unlock();
        }
    }

    public void removeEdge(int src, int dest) {
        lock.writeLock().lock();
        try {
            Set<Integer> neighbors = adjacencyList.get(src);
            if (neighbors != null) {
                neighbors.remove(dest);
            }
        } finally {
            lock.writeLock().unlock();
        }
    }

    public int shortestPath(int src, int dest, boolean fast) {
        lock.readLock().lock();
        try {
            if (fast)
                return BFSShortestPath(src, dest);
            else
                return bellmanFordShortestPath(src, dest);
        } finally {
            lock.readLock().unlock();
        }
    }

    private int backtrackShortestPath(int src, int dest) {
        lock.readLock().lock();
        try {
            List<Integer> path = new ArrayList<>();
            path.add(src);
            int[] minDist = {Integer.MAX_VALUE};
            backtrackShortestPathUtil(src, dest, path, 0, minDist);
            return minDist[0] == Integer.MAX_VALUE ? -1 : minDist[0];
        } finally {
            lock.readLock().unlock();
        }
    }

    private int bellmanFordShortestPath(int src, int dest) {
        lock.readLock().lock();

        try {
            Map<Integer, Integer> distance = new HashMap<>();
            distance.put(src, 0);
            for (int i = 0; i < adjacencyList.size() - 1; i++) {
                for (int u : adjacencyList.keySet()) {
                    if(!distance.containsKey(u)) continue;
                    for (int v : adjacencyList.getOrDefault(u, Collections.emptySet())) {
                        if (!distance.containsKey(v) || distance.get(v) > distance.get(u) + 1) {
                            distance.put(v, distance.get(u) + 1);
                        }
                    }
                }
            }
            return distance.getOrDefault(dest, -1);

        } finally {
            lock.readLock().unlock();
        }
        
    }

    private void backtrackShortestPathUtil(int current, int dest, List<Integer> path, int distance, int[] minDist) {
        if (current == dest) {
            minDist[0] = Math.min(minDist[0], distance);
            return;
        }

        for (int neighbor : adjacencyList.getOrDefault(current, Collections.emptySet())) {
            if (!path.contains(neighbor)) {
                path.add(neighbor);
                backtrackShortestPathUtil(neighbor, dest, path, distance + 1, minDist);
                path.remove(path.size() - 1);
            }
        }
    }

    private int BFSShortestPath(int src, int dest) {
        lock.readLock().lock();
        try {
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
                for (int neighbor : adjacencyList.getOrDefault(current, Collections.emptySet())) {
                    if (!distance.containsKey(neighbor)) {
                        distance.put(neighbor, distance.get(current) + 1);
                        queue.offer(neighbor);
                    }
                }
            }

            return -1;
        } finally {
            lock.readLock().unlock();
        }
    }
}
