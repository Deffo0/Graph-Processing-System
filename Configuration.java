import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

public class Configuration {
    private static final String CONFIG_FILE = "system.properties";

    private String serverAddress;
    private int serverPort;
    private int numberOfNodes;
    private String[] nodeAddresses;
    private int rmiregistryPort;

    public Configuration() {
        loadConfiguration();
    }

    private void loadConfiguration() {
        Properties properties = new Properties();
        try (FileInputStream fis = new FileInputStream(CONFIG_FILE)) {
            properties.load(fis);
            serverAddress = properties.getProperty("GSP.server");
            serverPort = Integer.parseInt(properties.getProperty("GSP.server.port"));
            numberOfNodes = Integer.parseInt(properties.getProperty("GSP.numberOfnodes"));
            nodeAddresses = new String[numberOfNodes];
            for (int i = 0; i < numberOfNodes; i++) {
                nodeAddresses[i] = properties.getProperty("GSP.node" + i);
            }
            rmiregistryPort = Integer.parseInt(properties.getProperty("GSP.rmiregistry.port"));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public String getServerAddress() {
        return serverAddress;
    }

    public int getServerPort() {
        return serverPort;
    }

    public int getNumberOfNodes() {
        return numberOfNodes;
    }

    public String[] getNodeAddresses() {
        return nodeAddresses;
    }

    public int getRmiregistryPort() {
        return rmiregistryPort;
    }
}
