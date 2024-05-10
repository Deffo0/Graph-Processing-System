# Graph Processing System (GSP)

## Introduction:
The Graph Processing System (GSP) is a Java-based distributed system designed to perform operations on graphs efficiently. This system consists of a client-server architecture where clients send requests to the server to add or remove edges from a graph, as well as query for the shortest path between two nodes in the graph.

## Components:

+ **Graph Server**: The server component of the system responsible for managing the graph and processing client requests (**TODO**: need some logging).
+ **Graph Client**: The client component that interacts with the server by sending requests to perform operations on the graph (**TODO**: need some logging and reading for the instructions file). 
+ **Configuration**: A configuration class that reads configuration parameters from a properties file, such as server address, ports, and number of nodes. 
+ **Graph**: A class representing the graph structure and containing methods to add edges, remove edges, and find the shortest path between nodes.
+ **Graph Service Interface**: An RMI (Remote Method Invocation) interface defining the methods that can be invoked remotely by clients.

## Files:
**system.properties**: Configuration file containing system properties such as server address, ports, and number of nodes.

## Setup and Usage:

1. **Configuration**: Update the system.properties file to configure the system parameters such as server address, ports, and number of nodes.
2. **Running the Server**: Compile and run GraphServer.java to start the server. It will bind to the specified address and port.
3. **Running the Client**: Compile and run GraphClient.java to start the client. It will read instructions from a file, send requests to the server, and measure performance.
4. **Instructions File**: The client reads instructions from a file named instructions.txt. Each line in the file represents an operation (add, remove, or query) on the graph.
5. **Adding/Removing Edges**: Use the format <Operation> <Source Node> <Destination Node> in the instructions file. Replace <Operation> with 'A' for adding an edge and 'D' for removing an edge.
6. **Querying Shortest Path**: Use the format Q <Source Node> <Destination Node> in the instructions file to query the shortest path between two nodes.
7. **Performance Measurement**: The client measures the time taken to execute each batch of instructions and logs the results.
8. **Sleep Interval**: After completing each batch of instructions, the client sleeps for a random duration between 1 to 10 seconds before starting the next batch.
