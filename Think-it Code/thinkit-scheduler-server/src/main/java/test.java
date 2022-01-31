import java.rmi.RemoteException;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import com.thinkit.rpc.Scheduler;

public class test {

	public static void main(String[] args) {
		
		try {
			Scheduler sch=new Scheduler();
			//sch.schedule(11, 12, "GMT+1", 5);
			System.out.println(sch.schedule(14, 20, "GMT", 5));
			
		} catch (RemoteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
		
		
	}

}
