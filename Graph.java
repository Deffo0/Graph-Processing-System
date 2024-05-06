import java.util.*;

public class Graph {
    private Map<Integer, List<Integer>> adjacencyList;

    public Graph() {
        this.adjacencyList = new HashMap<>();
    }

    // Method to add an edge to the graph
    public void addEdge(int src, int dest) {
        adjacencyList.putIfAbsent(src, new ArrayList<>());
        adjacencyList.get(src).add(dest);
    }

    // Method to remove an edge from the graph
    public void removeEdge(int src, int dest) {
        List<Integer> neighbors = adjacencyList.get(src);
        if (neighbors != null) {
            neighbors.remove((Integer) dest);
        }
    }

    // Method to perform BFS and find the shortest path from src to dest
    public int shortestPath(int src, int dest) {
        if (!adjacencyList.containsKey(src) || !adjacencyList.containsKey(dest)) {
            return -1; // Either src or dest node does not exist
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

        return -1; // No path found between src and dest
    }
}

