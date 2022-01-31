package com.thinkit.rpc;


import java.rmi.Remote;
import java.rmi.RemoteException;

/**
 * RMI scheduler interface
 * @author Doaa.farouk
 *
 */
public interface SchedulerInterface extends Remote {
	
	/**
	 * Main scheduler method to be called by the stub
	 * @param start
	 * @param end
	 * @param timezone
	 * @param guests
	 * @return
	 * @throws RemoteException
	 */
	   public String schedule(int start, int end, String timezone, int guests) throws RemoteException;

}
