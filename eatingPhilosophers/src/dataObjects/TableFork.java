package dataObjects;

import java.util.concurrent.atomic.AtomicBoolean;

public class TableFork {
	AtomicBoolean taken;
	
	
	public TableFork() {
		this.taken=new AtomicBoolean(false);
	}
	/**
	 * atempts to take the fork if it is taken returns false
	 * using synchronized and atomic boolean to hundertprocently concurrent change the boolean
	 * @return if the boolean could be taken.
	 */
	public synchronized boolean checkAndTake(){
		return taken.compareAndSet(false, true);
	}
	/**
	 * releases the fork that before should have been taken
	 * should only be called by the thread that before set the boolean of the method to false
	 * @throws Exception 
	 */
	public synchronized void release () throws Exception{
		if(!taken.get()){
			throw new Exception("release called on not taken fork");
		}
		taken.compareAndSet(true, false);
		
	}

}
