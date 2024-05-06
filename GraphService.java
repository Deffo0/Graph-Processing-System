import java.rmi.Remote;
import java.rmi.RemoteException;

public interface GraphService extends Remote {
    // Method to add an edge to the graph
    void addEdge(int src, int dest) throws RemoteException;

    // Method to remove an edge from the graph
    void removeEdge(int src, int dest) throws RemoteException;

    // Method to query the shortest path between two nodes
    int shortestPath(int src, int dest) throws RemoteException;
}

