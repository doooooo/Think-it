package com.thinkit.rpc;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.stream.Stream;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.Response;

import com.thinkit.model.Guest;
import com.thinkit.model.GuestList;

/**
 * RMI scheduler implementation class
 * @author Doaa.farouk
 *
 */
public class Scheduler extends UnicastRemoteObject implements SchedulerInterface{
	
	GuestList allGuests;
	//String global_gmt_operator;
	int global_gmt_offset=0;// +/-n
	int guest_offset=0;
	

	public Scheduler() throws RemoteException {
		super();
	}
	
	/**
	 * Scheduler implementation method
	 */
	public String schedule(int start, int end, String timezone, int guests) throws RemoteException {
		
		ArrayList<int[]> common_slots=new ArrayList<int[]>();
		
		StringBuilder sb=new StringBuilder();
		StringBuilder names=new StringBuilder();
		//1. calc - if end-start < 1, return "no time slots" 
		
		//2. execute rest api for n guests
		//GuestList myGuests=getAPIGuests(guests);
		
		GuestList allGuests=new GuestList();
		
		Guest[] guestList=generateGuestList(start,end,guests, timezone);
		allGuests.setGuestList(guestList);
		
		Hashtable<Integer, Integer> pt=new Hashtable<Integer,Integer>();
		
		//3.a. pre-check if any guests.availability.length=0 or null, return "no time slots"
		
		//3.b. convert all guests.availability to timezone
		String userTimezone = timezone;
		userTimezone=userTimezone.toUpperCase().replaceAll("GMT", "");
		
		if(userTimezone.length()>0)
			global_gmt_offset=Integer.parseInt(userTimezone);//+/-n
		
		int[] intime=new int[guestList.length];
		int[] outtime=new int[guestList.length];
		
		for(int i=0;i<guestList.length;i++) {
			
			//init index pointer
			pt.put(i, 1);
			
			Guest guest=guestList[i];
			
			
			if(i<guestList.length-1) {
				names.append(guest.getName()+" - ");
			}
			
			//pre-check, if any guest don't have available time-slots
			if(guest.getAvailability()==null || guest.getAvailability().length==0) {
				return "No common time-slots";
			}
			
			String guestTimezone = guest.getOffset();
			
			//convert all to input timezone
			if(!guestTimezone.equalsIgnoreCase(timezone)) {
				guestTimezone=guestTimezone.toUpperCase().replaceAll("GMT", "");
				
				if(guestTimezone.length()>0)
					guest_offset=Integer.parseInt(guestTimezone);//+/-n
				
				//convert all to input timezone
				Stream<int[]> arr=Arrays.stream(guest.getAvailability());
				//int[][] newAvailability=(int[][]) arr.map(elem -> Arrays.stream(elem).map(j -> (j+global_gmt_offset-guest_offset)>24 ? (j+24-global_gmt_offset+guest_offset):(j+global_gmt_offset-guest_offset)).toArray()).toArray(int[][]::new);
				int[][] newAvailability=(int[][]) arr.map(elem -> Arrays.stream(elem).map(j -> (j+global_gmt_offset-guest_offset)).toArray()).toArray(int[][]::new);
				
				//sort availability based on start times
				Arrays.sort(newAvailability,new Comparator<int[]>() {
				    public int compare(int[] o1, int[] o2) {
				        // Intentional: Reverse order for this demo
				        return o1[0]-o2[0];
				    }
				});
				
				guestList[i].setAvailability(newAvailability);
			}
			
			
			
			//initial time slots
			intime[i]=guestList[i].getAvailability()[0][0];
			outtime[i]=guestList[i].getAvailability()[0][1];
			
			
		}
		
		//4. add start,end to guests.availability arrays
		
		//5. run algorithm to calculate common slots
		Integer[] init_intime = null;
		Integer[] init_outtime = null;
		
		int loop=0;
		
		while(loop !=-1) {
		init_intime = Arrays.stream( intime ).boxed().toArray( Integer[]::new );
		init_outtime = Arrays.stream( outtime ).boxed().toArray( Integer[]::new );
			
		Integer maxStart=Collections.max(Arrays.asList(init_intime));
		Integer minEnd=Collections.min(Arrays.asList(init_outtime));
			
		System.out.println(">>maxStart "+maxStart);
		System.out.println(">>minEnd "+minEnd);
		
		if(minEnd>=maxStart) { //|| (24-maxStart)<=minEnd) {
			//sb.append("Available slots:\n");
			//sb.append(maxStart+" - "+minEnd);
			
			if(maxStart+1<=minEnd)
				common_slots.add(new int[] {maxStart,minEnd});
			
			
			for(int i=0;i<intime.length;i++) {
				intime[i]=minEnd;
				//replace the item with minEnd
				if(outtime[i]==minEnd) {
					int index=pt.get(i);
					if(index<guestList[i].getAvailability().length) {
						outtime[i]=guestList[i].getAvailability()[index][1];
						intime[i]=guestList[i].getAvailability()[index][0];
						pt.put(i, index+1);
					}
					else {
						//return sb.toString();
						//break;
						loop=-1;
					}
				}
			}
		}
		
		else if(maxStart>=minEnd) {
			
			for(int i=0;i<intime.length;i++) {
				intime[i]=maxStart;
				//replace the item with minEnd
				if(outtime[i]<maxStart) {
					int index=pt.get(i);
					if(index<guestList[i].getAvailability().length) {
						outtime[i]=guestList[i].getAvailability()[index][1];
						intime[i]=guestList[i].getAvailability()[index][0];
						pt.put(i, index+1);
					}
					else {
						//return sb.toString();
						//break;
						loop=-1;
					}
				}
			}
		}
		}
		
		if(common_slots.size()>0) {
			sb.append("Available slots for ").append(guests).append("  guests:");
			sb.append("\n").append("guest names: ").append(names.toString());
		}
		
		for(int[] slot:common_slots) {
			int start_time=slot[0];
			while (start_time <= slot[1]-1) //Run loop
		    {
		        sb.append("\n").append( (start_time>24)?24-start_time:start_time ).append(" to ").append((start_time+1)>24 ? (24-start_time-1):(start_time+1)).append(" ").append(timezone);
		        start_time += 1; //Endtime check
		    }
		 
		}
		
		
		return sb.toString();
	}

	/**
	 * REST API helper method (commented in code)
	 * @param guests
	 * @return
	 */
	private GuestList getAPIGuests(int guests) {
		String url="https://bbmk31v2s7.execute-api.eu-central-1.amazonaws.com/dev/schedule";
		//String url="https://bbmk31v2s7.execute-api.eu-central-1.amazonaws.com/dev/generate";
		
		Client client = ClientBuilder.newBuilder().build();
		
		WebTarget target = client.target(url);
		
		Response response = target.queryParam("guests", guests).request().get();
		
		String responseString = response.readEntity(String.class);
		//GuestList allGuests=response.readEntity(GuestList.class);
		response.close();
		
		System.out.println(responseString);
		
		return null;
	}

	/**
	 * Helper method to generate guests list, without executing APIs
	 * @param start user preference start-time
	 * @param end user preference end-time
	 * @param guests no. of guests (not used)
	 * @param timezone user preference timezone
	 * @return
	 */
	private Guest[] generateGuestList(int start, int end, int guests, String timezone) {
		Guest[] guestList=new Guest[6];
		guestList[0]=new Guest("Doaa",new int[][] {{4,16}, {18, 25}}, "GMT");
		guestList[1]=new Guest("Anas",new int[][] {{2,14}, {17, 24}}, "GMT");
		guestList[2]=new Guest("Maha",new int[][] {{6, 8}, {12, 20}}, "GMT");
		guestList[3]=new Guest("Amin",new int[][] {{10, 22}}, "GMT");
		guestList[4]=new Guest("Mona",new int[][] {{12, 20},{23, 12}}, "GMT");
		guestList[5]=new Guest("me",new int[][] {{start,end}}, timezone);
		
		
//		guestList[0]=new Guest("Doaa",new int[][] {{2,14}, {16, 23}}, "GMT-2");
//		//guestList[1]=new Guest("Anas",new int[][] {{8,20}, {23, 5}}, "GMT+6");
//		guestList[1]=new Guest("Anas",new int[][] {{8,20}, {22, 24}}, "GMT+6");
//		guestList[2]=new Guest("Maha",new int[][] {{8, 10}, {14, 22}}, "GMT+2");
//		guestList[3]=new Guest("Amin",new int[][] {{13, 24}}, "GMT+3");
//		guestList[4]=new Guest("me",new int[][] {{start,end}}, timezone);
		return guestList;
	}

}
