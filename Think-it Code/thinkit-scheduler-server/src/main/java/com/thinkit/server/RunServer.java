package com.thinkit.server;

import java.rmi.Naming;

import com.thinkit.rpc.Scheduler;

/**
 * Main server class, to run from command
 * @author Doaa.farouk
 *
 */
public class RunServer {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		try {
				Scheduler schedule=new Scheduler();		   		   
				Naming.rebind("rmi://localhost/schedule", schedule);

			   System.out.println("Scheduler Server is ready.");
			   }catch (Exception e) {
				   System.out.println("Scheduler Server failed: " + e);
				   e.printStackTrace();
				}

	}

}
