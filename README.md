# Graph Processing System (GSP)

## Introduction:
The Graph Processing System (GSP) is a Java-based distributed system designed to perform operations on graphs efficiently. This system consists of a client-server architecture where clients send requests to the server to add or remove edges from a graph, as well as query for the shortest path between two nodes in the graph.

## Components:

+ **Graph Server**: The server component of the system responsible for managing the graph and processing client requests.
+ **Graph Client**: The client component that interacts with the server by sending requests to perform operations on the graph.
+ **Configuration**: A configuration class that reads configuration parameters from a properties file, such as server address, ports, and number of nodes. 
+ **Graph**: A class representing the graph structure and containing methods to add edges, remove edges, and find the shortest path between nodes.
+ **Graph Service Interface**: An RMI (Remote Method Invocation) interface defining the methods that can be invoked remotely by clients.

## Files:
**system.properties**: Configuration file containing system properties such as server address, ports, and number of nodes.

**server.Dockerfile**: Contains the instructions to build the server's docker image.
**build.sh**: Packages the server into a jar and build the server's docker image.

## Setup and Usage:

1. **Configuration**: Update the system.properties file to configure the system parameters such as server address, ports, and number of nodes.
2. **Building the Server**: Run build.sh script to produce its corresponding jar and the docker image of the server.
3. **Network Adapter**: The server needs a network adapter to be created in the host machine. Run the following command to create a network adapter named 'gsp-net'. Make sure that the server's IP address is in the same subnet as the network adapter.

3. **Starting the Application**: Run Start.java to start the docker container of the server. It waits for the server to be up and running by waiting for the R signaled from the server. Then it starts the clients to send requests to the server.
5. **Adding/Removing Edges**: Use the format <Operation> <Source Node> <Destination Node> in the instructions file. Replace <Operation> with 'A' for adding an edge and 'D' for removing an edge.
6. **Querying Shortest Path**: Use the format Q <Source Node> <Destination Node> in the instructions file to query the shortest path between two nodes.
7. **Performance Measurement**: The client measures the time taken to execute each batch of instructions and logs the results.
8. **Sleep Interval**: After completing each batch of instructions, the client sleeps for a random duration between 1 to 10 seconds before starting the next batch.
