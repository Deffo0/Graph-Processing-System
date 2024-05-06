import java.rmi.Naming;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.server.UnicastRemoteObject;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class GraphServer extends UnicastRemoteObject implements GraphService {
    private Graph graph;
    private Lock lock;

    public GraphServer() throws RemoteException {
        super();
        this.graph = new Graph();
        this.lock = new ReentrantLock();
    }

    @Override
    public void addEdge(int src, int dest) throws RemoteException {
        lock.lock();
        try {
            graph.addEdge(src, dest);
        } finally {
            lock.unlock();
        }
    }

    @Override
    public void removeEdge(int src, int dest) throws RemoteException {
        lock.lock();
        try {
            graph.removeEdge(src, dest);
        } finally {
            lock.unlock();
        }
    }

    @Override
    public int shortestPath(int src, int dest) throws RemoteException {
        lock.lock();
        try {
            return graph.shortestPath(src, dest);
        } finally {
            lock.unlock();
        }
    }

    public static void main(String[] args) {
        try {
            Configuration config = new Configuration();
            GraphServer server = new GraphServer();

            // Register the server object with RMI Registry
            System.setProperty("java.rmi.server.hostname","127.0.0.1");
            LocateRegistry.createRegistry(config.getRmiregistryPort());
            Naming.rebind("//" + config.getServerAddress() + ":" + config.getRmiregistryPort() + "/GraphService", server);
            System.out.println("Graph server started...");
        } catch (Exception e) {
            System.err.println("Graph server exception: " + e.toString());
            e.printStackTrace();
        }
    }
}
