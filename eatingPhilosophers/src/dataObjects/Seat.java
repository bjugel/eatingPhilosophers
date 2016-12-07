package dataObjects;

import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.locks.ReentrantLock;

import sun.management.Agent;

public class Seat {
	private AtomicBoolean taken;
	private TableFork leftFork;
	private TableFork rightFork;
	int seatID;
	Agent agent;
	
	public Seat(int pId,TableFork leftFork,TableFork rightFork){
		this.leftFork=leftFork;
		this.rightFork=rightFork;
		seatID = pId;
	}
	
	/**
	 * tryes to take the forks and blocks untill both are taken 
	 * Deadlocks cant occur since every second place switches the fork first taken 
	 * therefore assuring that its impossible for all places to have one fork while waiting
	 * for the other
	 */
	public void takeForks(){
//		if(seatID%2==0){
//			leftFork.lock();
//			rightFork.lock();
//		}else {
//			rightFork.lock();
//			leftFork.lock();
//		}
		//TODO implement new
	}
	
	
	public int getSeatID() {
		return seatID;
	}

	public void releaseForks(){
//		leftFork.unlock();
//		rightFork.unlock();
		//TODO reimplement
	}
	/**
	 * just sit down.
	 */
	public void take(){
		//this.taken.lock();
		//TODO reimplement
	}
	//lays down the forks and leaves the table
	public void leave(){
		releaseForks();
//		this.taken.unlock();
		//TODO reimplement
	}
	/**
	 * 
	 * @return weather or not the seat is currently occupied
	 */
	public synchronized boolean isTaken(){
		return taken.get();
	}

	public TableFork getRightFork() {
		return this.rightFork;

	}


	
}
