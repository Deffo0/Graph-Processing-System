import java.rmi.Naming;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.server.UnicastRemoteObject;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.logging.*;

public class GraphServer extends UnicastRemoteObject implements GraphService {
    private Graph graph;
    private Lock lock;
    private static final Logger LOGGER = Logger.getLogger(GraphServer.class.getName());

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
            LOGGER.info("Edge added: " + src + " -> " + dest);
        } catch (Exception e) {
            LOGGER.severe("Error adding edge: " + e.getMessage());
        } finally {
            lock.unlock();
        }
    }

    @Override
    public void removeEdge(int src, int dest) throws RemoteException {
        lock.lock();
        try {
            graph.removeEdge(src, dest);
            LOGGER.info("Edge removed: " + src + " -> " + dest);
        } catch (Exception e) {
            LOGGER.severe("Error removing edge: " + e.getMessage());
        } finally {
            lock.unlock();
        }
    }

    @Override
    public int shortestPath(int src, int dest) throws RemoteException {
        lock.lock();
        try {
            int shortestPath = graph.shortestPath(src, dest);
            LOGGER.info("Shortest path from " + src + " to " + dest + ": " + shortestPath);
            return shortestPath;
        } catch (Exception e) {
            LOGGER.severe("Error finding shortest path: " + e.getMessage());
            return -1; // or throw RemoteException
        } finally {
            lock.unlock();
        }
    }

    public static void main(String[] args) {
        try {

            Configuration config = new Configuration();
            GraphServer server = new GraphServer();
            LOGGER.setLevel(Level.INFO);

            Handler fileHandler = new FileHandler("Server.log");
            fileHandler.setFormatter(new SimpleFormatter());
            LOGGER.addHandler(fileHandler);

            // Register the server object with RMI Registry
            System.setProperty("java.rmi.server.hostname","127.0.0.1");
            LocateRegistry.createRegistry(config.getRmiregistryPort());
            Naming.rebind("//" + config.getServerAddress() + ":" + config.getRmiregistryPort() + "/GraphService", server);
            LOGGER.info("Graph server started...");
        } catch (Exception e) {
            System.err.println("Graph server exception: " + e.toString());
            e.printStackTrace();
        }
    }
}
