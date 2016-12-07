package dataObjects;

import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.locks.ReentrantLock;

import sun.management.Agent;

public class Seat {
	private AtomicBoolean taken;
	int seatID;

	
	public Seat(int pId){
		seatID = pId;
	}
	
	public int getSeatID() {
		return seatID;
	}


	/**
	 * Sit down
	 * @return if you could sit down on the seat. Returns false if the seat was already taken
	 */
	public synchronized boolean  take(){
		return taken.compareAndSet(false, true);
	}
	/**
	 * leave the seat throws exception if seat is already left (that shouldnt happen ever.)
	 * @throws Exception
	 */
	public void leave() throws Exception{
		boolean retval;
		retval =taken.compareAndSet(true, false);
		if (!retval)
			throw new Exception("cant leave a left chair.");
	}
	




	
}
