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
		 * Main interface to run in client
		 * @param start
		 * @param end
		 * @param timezone
		 * @param guests no. of guests
		 * @return common time slots if any, and guests name
		 * @throws RemoteException
		 */
	   public String schedule(int start, int end, String timezone, int guests) throws RemoteException;

}
