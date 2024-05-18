package org.example.RMIInterface;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.List;

public interface GraphBatchProcessor extends Remote {
    List<Integer> processBatch(String batch) throws RemoteException;
}
