package dataObjects;

import java.util.concurrent.locks.ReentrantLock;

import sun.management.Agent;

public class Seat {
	private ReentrantLock taken;
	private ReentrantLock leftFork;
	private ReentrantLock rightFork;
	int seatID;
	Agent agent;
	
	public Seat(int pId,ReentrantLock leftFork,ReentrantLock rightFork){
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
		if(seatID%2==0){
			leftFork.lock();
			rightFork.lock();
		}else {
			rightFork.lock();
			leftFork.lock();
		}
	}
	
	
	public int getSeatID() {
		return seatID;
	}

	public void releaseForks(){
		leftFork.unlock();
		rightFork.unlock();
	}
	/**
	 * just sit down.
	 */
	public void take(){
		this.taken.lock();
	}
	//lays down the forks and leaves the table
	public void leave(){
		releaseForks();
		this.taken.unlock();
	}
	/**
	 * 
	 * @return weather or not the seat is currently occupied
	 */
	public boolean isTaken(){
		return taken.isLocked();
	}

	public ReentrantLock getLeftFork() {
		return leftFork;
	}

	public ReentrantLock getRightFork() {
		return rightFork;
	}
	
}
