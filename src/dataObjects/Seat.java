package dataObjects;

import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.locks.ReentrantLock;

import sun.management.Agent;

public class Seat {
	private AtomicBoolean taken;
	private int seatID;
	private String seatName;
	private int seatIndex;

	
	public Seat(int agentID, int seatIndex, int seatID){
		this.seatID = seatID;
		this.seatIndex = seatIndex;
		this.seatName = "A" + agentID + "S" + seatIndex;
		this.taken=new AtomicBoolean(false);
		System.out.println("Seat " + seatName + " is inzialized.");
	}
	
	public int getSeatID() {
		return seatID;
	}


	/**
	 * Sit down or lock seat for agent
	 * @param isAgent
	 * @returnif you could sit down on the seat. Returns false if the seat was already taken. If an agent try to lock the seat the return should be always true
	 */
	public synchronized boolean  take(boolean isAgent){
		boolean returnValue = false;
		//TODO maybe we can get rid of polling?!?!
		if(!isAgent){
			returnValue = taken.compareAndSet(false, true);
		}else{
			boolean isTaken = false;
			do{
				isTaken = taken.compareAndSet(false, true);
			}while(!isTaken);
			returnValue = isTaken;
			System.out.println(seatName + "got lock from the agent.");
		}
		return returnValue;
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
	
	//CB
	public String getSeatName(){
		return this.seatName;
	}
	
	//CB
	public int getSeatIndex(){
		return this.seatIndex;
	}

	//CB
	public void decrementSeatID(){
		seatID--;
		System.out.println(seatName + "the new seatID is " + seatID);
	}
	
	//CB
	public void incrementSeatID(){
		seatID++;
		System.out.println(seatName + "the new seatID is " + seatID);
	}
	
	//CB
	public void decrementSeatIndex(){
		seatIndex--;
		System.out.println(seatName + "the new seatIndex is " + seatIndex);
	}
	
	//CB
	public void incrementSeatIndex(){
		seatIndex++;
		System.out.println(seatName + "the new seatIndex is " + seatIndex);
	}

	
}
