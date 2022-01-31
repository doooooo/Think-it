package com.thinkit.client;

import java.rmi.Naming;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.thinkit.rpc.SchedulerInterface;

/**
 * Main client class, run from command
 * @author Doaa.farouk
 *
 */
public class SchedulerClient {

	/**
	 * 
	 * @param args [start_time end_time timezone guests], example: [2pm 8pm GMT+00 2]
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		SchedulerInterface schedule;
		try {
			schedule = (SchedulerInterface)Naming.lookup("rmi://localhost/schedule");
			
			String start=args[0].toUpperCase();
			String end=args[1].toUpperCase();
			String timezone=args[2].toUpperCase();
			String nguests=args[3];
			
			int start_time=0;
			int end_time=0;
			
			//input validation
			if(!start.contains("AM") && !start.contains("PM")) {
				System.out.println("");
			}
			
			String time_regex="^(0?[1-9]|1[0-2])\\s?((?:A|P)\\.?M\\.?)$";
			String timezone_regex="(?:GMT)[+-][0-9]{1,2}\\b";
			String guests_regex="^[0-9]";
			
			 Pattern p = Pattern.compile("/[0-9]+[AM|PM]+/gm");
			 //Matcher start_m = p.matcher(start);
			 //boolean isStart = start_m.matches();
			
			 //Matcher end_m = p.matcher(end);
			 //boolean isEnd = end_m.matches();
			 
			 if(!Pattern.matches(time_regex,start) && !Pattern.matches(time_regex, end)) {
				 System.out.println("start & end should be in format 1-12am or 1-12pm");
				 return;
			 }
			 else {
				 if(start.contains("PM")) {
					 start=start.replaceAll("PM", "");
					 start_time=Integer.parseInt(start)+12;
				 }
				 if(end.contains("PM")) {
					 end=end.replaceAll("PM", "");
					 end_time=Integer.parseInt(end)+12;
				 }
			 }
			 
			 if(!Pattern.matches(timezone_regex, timezone)) {
				 System.out.println("Timezone should be in the pattern GMT+/-00");
				 return;
			 }
			 
			 if(!Pattern.matches(guests_regex, nguests)) {
				 System.out.println("No. of guests should be numeric");
				 return;
			 }
			
			String result=schedule.schedule(start_time, end_time,timezone , Integer.parseInt(nguests));
			System.out.println("Result is :"+result);
 
			}catch (Exception e) {
				System.out.println("Client exception: " + e);
				}
	}

}
