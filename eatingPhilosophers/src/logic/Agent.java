package logic;

import java.rmi.RemoteException;
import java.rmi.Remote;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.concurrent.locks.ReentrantLock;

import dataObjects.Philosopher;
import dataObjects.Seat;
import dataObjects.TableFork;
import java.rmi.Naming;

public class Agent extends UnicastRemoteObject implements AgentInterface {

	private ArrayList<AgentInterface> otherAgents = new ArrayList<AgentInterface>();
	private ArrayList<Philosopher> philoList = new ArrayList<Philosopher>();
	private ArrayList<Seat> seatList = new ArrayList<Seat>();
	private ArrayList<TableFork> forks = new ArrayList<TableFork>();// BJ
	private int agentID = 0;
	private int lastSeatID;
	private int firstSeatID;
	private AgentInterface nextAgent;

	public Agent() throws RemoteException {
		super();
	}

	public Agent(int agentID) throws RemoteException {
		super();
		this.agentID = agentID;

	}

	/**
	 * should be called before startPhilosophers() is called in order to
	 * initiallize all the philosophers that should run.
	 */
	public void initialzePhilos(int numberOfPhilos, int firstPhiloID) {
		philoList.clear();
		addPhilos(numberOfPhilos, firstPhiloID);

		System.out.println("Agent" + agentID + " initializing " + numberOfPhilos + " Philosophers with starting number "
				+ firstPhiloID); // TODO
		// TODO fill change initialize to different initializations
		// TODO change init methods in master
		// TODO remove some arguments here
		// testings
	}

	/**
	 * killer method locking first fork for a specific philosopher
	 * 
	 * @param seatID
	 * @throws RemoteException
	 */
	public void giveForks(int seatID,int philoID) throws RemoteException {
		boolean isLocalSeat = (seatID <= lastSeatID && seatID >= firstSeatID);
		if (!isLocalSeat) {
			nextAgent.giveForks(seatID,philoID);
		} else {
			boolean leftForkTaken = false;
			boolean rightForkTaken = false;
				if (seatID % 2 == 0) {
					
						leftForkTaken = checkAndTakeLeftFork(seatID, philoID);
					
						rightForkTaken = checkAndTakeRightFork(seatID, philoID);
				} else {				
						rightForkTaken = checkAndTakeRightFork(seatID, philoID);			
						leftForkTaken = checkAndTakeLeftFork(seatID, philoID);		
				}
			
		}
	}

	/**
	 * blocks untill left fork is taken
	 * 
	 * @param seatID
	 * @param philoID TODO
	 * @return if left fork could be taken
	 */
	private boolean checkAndTakeLeftFork(int seatID, int philoID) {
		boolean retVal=false;
		int forkPos = seatID - firstSeatID;
		while(!retVal){
			retVal= getForks().get(forkPos).checkAndTake();
		}
		System.out.printf("PhiloPhiloID:\t%03d at Seat%03d\t locked left fork \t%03d at agent \t%03d \n", philoID,seatID, forkPos,this.agentID);
		return retVal;
	}

	/**
	 *blocks with while loop untill right fork is taken 
	 * 
	 * @param seatID
	 * @param philoID TODO
	 * @return if right fork could be taken
	 * @throws RemoteException
	 */
	private boolean checkAndTakeRightFork(int seatID, int philoID) throws RemoteException {
		boolean retVal = false;
		int forkPos = seatID - firstSeatID;
		boolean isLastSeat = (seatID == lastSeatID);
		
			if (!isLastSeat) {
				while (!retVal){
				retVal = getForks().get(forkPos + 1).checkAndTake();
				}
				System.out.printf("PhiloPhiloID:\t%03d at Seat%03d\t locked right fork \t%03d at agent \t%03d \n", philoID,seatID, forkPos+1,this.agentID);
				
			} else {
				while (!retVal){
				retVal = nextAgent.lockFirstFork();
				}
				System.out.printf("PhiloPhiloID:\t%03d at Seat%03d\t locked right fork \t%03d at agent \t%03d \n", philoID,seatID, 0,this.nextAgent.getAgentID());
				
			}
		
		return retVal;
	}

	/**
	 * using a pulling while loop lock the first fork for the last seat in
	 * previous agent. shouldnt be used differently .
	 */
	public boolean lockFirstFork() {
		boolean forkLocked = false;
		while (!forkLocked) { // TODO no pulling but something else
			forkLocked = getForks().get(0).checkAndTake();
		}
		return forkLocked;
	}

	/**
	 * CARE all forks have to be initialized before and next agents have to be
	 * given called.
	 * 
	 * @param numberOfSeats
	 * @param firstSeatID
	 * @throws RemoteException
	 */
	public void initSeats(int numberOfSeats, int firstSeatID) throws RemoteException {
		seatList.clear();
		this.firstSeatID = firstSeatID;
		this.lastSeatID = firstSeatID + numberOfSeats - 1;
		System.out.printf("First Seat Id \t%03d\n", this.firstSeatID);
		System.out.printf("Last Seat Id \t%03d\n", this.lastSeatID);
		for (int i = 0; i < numberOfSeats; i++) {

			seatList.add(new Seat(firstSeatID + i));

		}
		System.out.println("Agent" + agentID + " initializing " + numberOfSeats + " Seats.");
		// System.out.println("Verifying that last
		// Seats["+(firstSeatID+numberOfSeats-1)+ "] fork equals next agents
		// firstfork: "+
		// (seatList.get(numberOfSeats-1).getRightFork().equals(nextAgent.getForks().get(0))));
		// TODO finish create seats and give them forks that are already
		// initialized else we gett nullpointer. evtually create an forks not
		// initialized exception.
	}

	public void initForks(int numberOfSeats) {
		forks.clear();
		System.out.println("Agent" + agentID + " initializing " + numberOfSeats + " forks.");
		for (int i = 0; i < numberOfSeats; i++) {
			TableFork temp = new TableFork();
			forks.add(temp);
			System.out.println(temp);
			// ReentrantLock a;
			// a.
		}

		// TODO finish create seats and give them forks that are already
		// initialized else we gett nullpointer. evtually create an forks not
		// initialized exception.
	}

	/**
	 * 
	 * @return the seat id if could sit down else -1
	 * @throws RemoteException
	 */
	public int giveSeat(int callingAgentID) throws RemoteException {
		int returnVal = -1;
		for (Seat s : seatList) {
			if (s.take()) {
				returnVal = s.getSeatID();
				break; // #ASK why not work with break
			}
		}
		// no seat found yet and next agent is not yet searched for free seats.
		if (returnVal == -1 && nextAgent.getAgentID() != callingAgentID) {
			returnVal = nextAgent.giveSeat(callingAgentID);
		}

		return returnVal;
		// TODO check that this method works 100 percent always again.
	}

	/**
	 * lets philosoph stand up from a given seat.
	 * 
	 * @param seatID
	 *            the seat to stand up from
	 */
	public void standUp(int seatID) throws Exception {
		int firstSeatID = seatList.get(0).getSeatID();
		int lastSeatID = firstSeatID + seatList.size() - 1;
		// if seat is in our agent
		if (seatID >= firstSeatID && seatID <= lastSeatID) {
			seatList.get(seatID - firstSeatID).leave();
			System.out.printf("seat %03d was stood up from \n", seatID);
		} else {
			nextAgent.standUp(seatID);
		}

	}

	public void addPhilos(int philos, int firstPhiloID) {
		for (int i = 0; i < philos; i++) {

			philoList.add(new Philosopher(firstPhiloID + i, this));
		}
	}

	public ArrayList<TableFork> getForks() {
		return forks;
	}

	public ArrayList<Seat> getSeats() {
		return seatList;
	}

	public void giveOtherAgent(AgentInterface agent) {
		this.otherAgents.add(agent);
	}

	public void giveNextAgent(AgentInterface nextAgent) throws RemoteException {
		this.nextAgent = nextAgent;
		System.out.println("I'm agent" + agentID + " next up is Agent" + nextAgent.getAgentID());
	}

	public void startPhilosophers() {
		for (Philosopher p : philoList) {
			new Thread(p).start();
		}
	}

	private void fillSeatList(int philos, int firstPhiloId) {
		for (int i = 0; i < philos; i++) {
			// TODO new Seat
		}
	}

	public int getAgentID() {
		return agentID;
	}

	@Override
	public String toString() {
		return "Agent" + agentID;
	}

	public ArrayList<AgentInterface> getOtherAgents() {
		return otherAgents;
	}

	/**
	 * Release left and right fork
	 * @throws Exception 
	 */
	public void releaseForks(int seatID,int philoID) throws Exception {
		boolean isLocalSeat = (seatID <= lastSeatID && seatID >= firstSeatID);
		if (!isLocalSeat) {
			nextAgent.releaseForks(seatID,philoID);
		} else {
			int forkPos = seatID - firstSeatID;
			boolean isLastSeat = (seatID == lastSeatID);
			System.out.printf("PhiloPhiloID:\t%03d at Seat%03d\t releasing left fork \t%03d at agent \t%03d \n", philoID,seatID, forkPos,this.getAgentID());

			getForks().get(forkPos).release();
			if (!isLastSeat) {
				System.out.printf("PhiloPhiloID:\t%03d at Seat%03d\t releasing right fork \t%03d at agent \t%03d \n", philoID,seatID, forkPos+1,this.getAgentID());

				getForks().get(forkPos + 1).release();
			} else {
				System.out.printf("PhiloPhiloID:\t%03d at Seat%03d\t releasing right fork \t%03d at agent \t%03d \n", philoID,seatID, 0,this.nextAgent.getAgentID());

				nextAgent.releaseFirstFork();
			}
		}
	}

	/**
	 * Release the first fork for remote agent
	 * @throws Exception 
	 */
	public void releaseFirstFork() throws Exception {
		getForks().get(0).release();

	}

}