package logic;

import java.rmi.RemoteException;
import java.rmi.Remote;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.locks.ReentrantLock;

import dataObjects.Philosopher;
import dataObjects.Seat;
import dataObjects.TableFork;
import sun.management.resources.agent_zh_CN;

import java.rmi.Naming;

public class Agent extends UnicastRemoteObject implements AgentInterface {

	private ArrayList<AgentInterface> otherAgents = new ArrayList<AgentInterface>();
	private ArrayList<Philosopher> philoList;
	private ArrayList<Seat> seatList;
	private ArrayList<TableFork> forks;// BJ
	private int agentID = 0;
	private int lastSeatID;
	private int firstSeatID;
	private int firstPhiloID = -1;
	private AgentInterface nextAgent;
	private AgentInterface previousAgent;
	private AtomicBoolean philosDown;

	public Agent() throws RemoteException {
		super();
	}

	public Agent(int agentID) throws RemoteException {
		super();
		this.agentID = agentID;
		this.philosDown = new AtomicBoolean(false);

	}

	/**
	 * should be called before startPhilosophers() is called in order to
	 * initiallize all the philosophers that should run.
	 */
	public void initialzePhilos(int numberOfPhilos, int firstPhiloID) {
		philoList = new ArrayList<Philosopher>();
		philoList.clear();
		this.firstPhiloID = firstPhiloID;
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
	public void giveForks(int seatID, int philoID) throws RemoteException {
		boolean isLocalSeat = (seatID <= lastSeatID && seatID >= firstSeatID);
		if (!isLocalSeat) {
			nextAgent.giveForks(seatID, philoID);
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
	 * @param philoID
	 *            TODO
	 * @return if left fork could be taken
	 */
	private boolean checkAndTakeLeftFork(int seatID, int philoID) {
		boolean retVal = false;
		int forkPos = seatID - firstSeatID;
		while (!retVal) {
			retVal = getForks().get(forkPos).checkAndTake();
		}
		// System.out.printf("PhiloPhiloID:\t%03d at Seat%03d\t locked left fork
		// \t%03d at agent \t%03d \n", philoID,seatID, forkPos,this.agentID);
		return retVal;
	}

	/**
	 * blocks with while loop untill right fork is taken
	 * 
	 * @param seatID
	 * @param philoID
	 *            TODO
	 * @return if right fork could be taken
	 * @throws RemoteException
	 */
	private boolean checkAndTakeRightFork(int seatID, int philoID) throws RemoteException {
		boolean retVal = false;
		int forkPos = seatID - firstSeatID;
		boolean isLastSeat = (seatID == lastSeatID);

		if (!isLastSeat) {
			while (!retVal) {
				retVal = getForks().get(forkPos + 1).checkAndTake();
			}
			// System.out.printf("PhiloPhiloID:\t%03d at Seat%03d\t locked right
			// fork \t%03d at agent \t%03d \n", philoID,seatID,
			// forkPos+1,this.agentID);

		} else {
			while (!retVal) {
				retVal = nextAgent.lockFirstFork();
			}
			// System.out.printf("PhiloPhiloID:\t%03d at Seat%03d\t locked right
			// fork \t%03d at agent \t%03d \n", philoID,seatID,
			// 0,this.nextAgent.getAgentID());

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
		seatList = new ArrayList<Seat>();
		seatList.clear();
		this.firstSeatID = firstSeatID;
		this.lastSeatID = firstSeatID + numberOfSeats - 1;
		System.out.printf("First Seat Id \t%03d\n", this.firstSeatID);
		System.out.printf("Last Seat Id \t%03d\n", this.lastSeatID);
		for (int i = 0; i < numberOfSeats; i++) {

			seatList.add(new Seat(agentID, i, firstSeatID + i));

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
		forks = new ArrayList<TableFork>();
		forks.clear();
		System.out.println("Agent" + agentID + " initializing " + numberOfSeats + " forks.");
		for (int i = 0; i < numberOfSeats; i++) {
			TableFork temp = new TableFork();
			forks.add(temp);
			System.out.println(temp);
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
		//if philosophers can still act. 
		//TODO eventually give instead of the down solution the philos a boolean that shuts them down.
		if (!philosDown.get()) {//this method is not 100 percent threadsave. 
			for (Seat s : seatList) {
				if (s.take(false)) {
					returnVal = s.getSeatID();
					break; // #ASK why not work with break
				}
			}
			// no seat found yet and next agent is not yet searched for free
			// seats.
			if (returnVal == -1 && nextAgent.getAgentID() != callingAgentID) {
				returnVal = nextAgent.giveSeat(callingAgentID);
			}
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
		// int firstSeatID = seatList.get(0).getSeatID();
		// int lastSeatID = firstSeatID + seatList.size() - 1;
		// if seat is in our agent
		if (seatID >= firstSeatID && seatID <= lastSeatID) {
			System.out.printf("seat %03d was stood up from \n", seatID);
			seatList.get(seatID - firstSeatID).leave();
			// System.out.printf("seat %03d was stood up from \n", seatID);
		} else {
			nextAgent.standUp(seatID);
		}

	}

	public void addPhilos(int philos, int firstPhiloID) {
		for (int i = 0; i < philos; i++) {

			philoList.add(new Philosopher(firstPhiloID + i, this));
		}
	}
	
	public void addAndStartPhilos(int philos, int firstPhiloID,long endTime) {
		for (int i = 0; i < philos; i++) {
			Philosopher p=new Philosopher(firstPhiloID + i, this);
			p.setEndTime(endTime);
			new Thread(p).start();
			philoList.add(p);
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

	public void givePriviousAndNextAgent(AgentInterface priviousAgent, AgentInterface nextAgent)
			throws RemoteException {
		this.nextAgent = nextAgent;
		this.previousAgent = priviousAgent;
		System.out.println("I'm agent" + agentID + " next agent is agent" + nextAgent.getAgentID()
				+ " and privious agent is agent" + priviousAgent.getAgentID());
	}

	public void startPhilosophers(long endTime) {
		for (Philosopher p : philoList) {
			p.setEndTime(endTime);
			new Thread(p).start();
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
	 * 
	 * @throws Exception
	 */
	public void releaseForks(int seatID, int philoID) throws Exception {
		boolean isLocalSeat = (seatID <= lastSeatID && seatID >= firstSeatID);
		if (!isLocalSeat) {
			nextAgent.releaseForks(seatID, philoID);
		} else {
			int forkPos = seatID - firstSeatID;
			boolean isLastSeat = (seatID == lastSeatID);
			// System.out.printf("PhiloPhiloID:\t%03d at Seat%03d\t releasing
			// left fork \t%03d at agent \t%03d \n", philoID,seatID,
			// forkPos,this.getAgentID());

			getForks().get(forkPos).release();
			if (!isLastSeat) {
				// System.out.printf("PhiloPhiloID:\t%03d at Seat%03d\t
				// releasing right fork \t%03d at agent \t%03d \n",
				// philoID,seatID, forkPos+1,this.getAgentID());

				getForks().get(forkPos + 1).release();
			} else {
				// System.out.printf("PhiloPhiloID:\t%03d at Seat%03d\t
				// releasing right fork \t%03d at agent \t%03d \n",
				// philoID,seatID, 0,this.nextAgent.getAgentID());

				nextAgent.releaseFirstFork();
			}
		}
	}

	/**
	 * Release the first fork for remote agent
	 * 
	 * @throws Exception
	 */
	public void releaseFirstFork() throws Exception {
		getForks().get(0).release();
	}

	/**
	 * CB
	 * 
	 * @return the number of total times of eating for all philosophers at this
	 *         agent.
	 */
	public int getTotalTimesOfEating() {
		int totalTimesOfEating = 0;
		for (Philosopher philo : philoList) {
			totalTimesOfEating += philo.getEatingCounter();
		}
		return totalTimesOfEating;
	}

	// CB
	public int getPhiloEatingCounter(int philoIndex) throws RemoteException {
		return philoList.get(philoIndex).getEatingCounter();
	}

	// CB
	public void catchPhilo(int philoIndex) throws RemoteException {
		philoList.get(philoIndex).getCatched();
	}

	// CB
	public void setPhiloHungry(int philoIndex) throws RemoteException {
		philoList.get(philoIndex).setPhiloHungry();
	}

	/**
	 * returns the current size of the philosopherlist 
	 */
	public int getNumberOfPhilos() {
		return philoList.size();
	}

	// CB
	public boolean isPhiloDone(int philoIndex) {
		return philoList.get(philoIndex).isPhiloDone();
	}

	// CB
	public boolean isPhiloCatched(int philoIndex) {
		return philoList.get(philoIndex).isPhiloCatched();
	}

	// CB
	public int getFirstPhiloID() {
		return this.firstPhiloID;
	}

	// CB
	public void deleteSeat(String seatName) throws Exception {

		int seatIndex = -1;

		for (Seat seat : seatList) {
			if (seat.getSeatName().equals(seatName)) {
				seatIndex = seat.getSeatIndex();
				System.out.println(seatIndex);
				break;
			}
		}

		if (seatIndex != -1) {
			//lockAllNecessarySeatsForDeleteASeat(seatIndex);
			seatList.remove(seatIndex);
			forks.remove(seatIndex);
			System.out.println("Agent " + agentID + " removed seat number" + seatIndex);
			decrementSeatIDAndIndexForAllNecessarySeats(seatIndex);
			//unlockAllNecessarySeatsForDeleteASeat(seatIndex);
		} else {
			System.out.println("the agent could not find the seat " + seatName);
		}

	}

	// CB
	private void unlockAllNecessarySeatsForDeleteASeat(int seatIndex) throws Exception {
		int minusInt = 1;

		if (seatIndex == 0) {
			minusInt = 0;
			if (this.getAgentID() != 0) {
				this.getPriviousAgent().unlockLastSeat();
			}
		}

		for (int i = seatIndex - minusInt; i < seatList.size(); i++) {
			seatList.get(i).leave();
		}
		if (this.agentID < otherAgents.size()) {
			for (int i = this.agentID; i < otherAgents.size(); i++) {
				otherAgents.get(i).leaveAllSeats();
			}
		}

	}

	// CB
	private void decrementSeatIDAndIndexForAllNecessarySeats(int seatIndex) throws RemoteException {

		this.lastSeatID -= 1;

		if (this.agentID < otherAgents.size()) {
			for (int i = this.agentID; i < otherAgents.size(); i++) {
				otherAgents.get(i).decrementFirstSeatID();
				otherAgents.get(i).decrementLastSeatID();
			}
		}

		for (int i = seatIndex; i < seatList.size(); i++) {
			seatList.get(i).decrementSeatID();
			seatList.get(i).decrementSeatIndex();
		}
		if (this.agentID < otherAgents.size()) {
			for (int i = this.agentID; i < otherAgents.size(); i++) {
				otherAgents.get(i).decrementSeatIDForAllSeats();
			}
		}

	}

	// CB
	private void lockAllNecessarySeatsForDeleteASeat(int seatIndex) throws Exception {
		int minusInt = 1;

		if (seatIndex == 0) {
			minusInt = 0;
			if (this.getAgentID() != 0) {
				this.getPriviousAgent().lockLastSeat();
			}
		}

		for (int i = seatIndex - minusInt; i < seatList.size(); i++) {
			seatList.get(i).take(true);
		}
		if (this.agentID < otherAgents.size()) {
			for (int i = this.agentID; i < otherAgents.size(); i++) {
				otherAgents.get(i).lockAllSeats();
			}
		}

	}

	// CB
	public void lockAllSeats() {
		for (Seat seat : seatList) {
			seat.take(true);
		}
	}

	// CB
	public void leaveAllSeats() throws Exception {
		for (Seat seat : seatList) {
			seat.leave();
		}
	}

	// CB
	public void decrementSeatIDForAllSeats() {
		for (Seat seat : seatList) {
			seat.decrementSeatID();
		}
	}

	// CB
	public void decrementFirstSeatID() {
		this.firstSeatID -= 1;
	}

	// CB
	public void decrementLastSeatID() {
		this.lastSeatID -= 1;
	}

	// CB
	public void clearOtherAgentList() throws Exception {
		this.otherAgents.clear();
	}

	// CB
	public AgentInterface getPriviousAgent() throws Exception {
		return this.previousAgent;
	}

	// CB
	public void lockLastSeat() throws Exception {
		this.seatList.get((this.seatList.size() - 1)).take(true);
	}

	// CB
	public void unlockLastSeat() throws Exception {
		this.seatList.get((this.seatList.size() - 1)).leave();
	}

	// CB
	public void insertSeat(String seatName) throws Exception {
		int seatIndex = -1;

		for (Seat seat : seatList) {
			if (seat.getSeatName().equals(seatName)) {
				seatIndex = seat.getSeatIndex();
				// System.out.println(seatIndex);
				break;
			}
		}

		if (seatIndex != -1) {
			//lockAllNecessarySeatsForDeleteASeatForInsertASeat(seatIndex);

			seatList.add(seatIndex + 1, new Seat(this.agentID, seatIndex + 1, seatList.get(seatIndex).getSeatID() + 1));
			System.out.println("Agent " + agentID + " insert a seat at the index " + (seatIndex + 1));

			forks.add(new TableFork());

			incrementSeatIDAndIndexForAllNecessarySeats(seatIndex);

			for (Seat s : seatList) {
				System.out.println(s.getSeatID());
			}
			//unlockAllNecessarySeatsForDeleteASeatForInsertASeat(seatIndex);
		} else {
			System.out.println("the agent could not find the seat " + seatName);
		}

	}

	// CB
	private void unlockAllNecessarySeatsForDeleteASeatForInsertASeat(int seatIndex) throws Exception {
		for (int i = seatIndex; i < seatList.size(); i++) {
			seatList.get(i).leave();
		}
		if (this.agentID < otherAgents.size()) {
			for (int i = this.agentID; i < otherAgents.size(); i++) {
				otherAgents.get(i).leaveAllSeats();
			}
		}
	}

	// CB
	private void incrementSeatIDAndIndexForAllNecessarySeats(int seatIndex) throws RemoteException {
		this.lastSeatID += 1;

		if (this.agentID < otherAgents.size()) {
			for (int i = this.agentID; i < otherAgents.size(); i++) {
				otherAgents.get(i).incrementFirstSeatID();
				otherAgents.get(i).incrementLastSeatID();
			}
		}

		for (int i = seatIndex + 2; i < seatList.size(); i++) {
			seatList.get(i).incrementSeatID();
			seatList.get(i).incrementSeatIndex();
		}
		if (this.agentID < otherAgents.size()) {
			for (int i = this.agentID; i < otherAgents.size(); i++) {
				otherAgents.get(i).incrementSeatIDForAllSeats();
			}
		}

	}
	/**
	 * tries to delete a philo if it is in this master. If not then returns false
	 * @param philoID the id of the philo to delete
	 * @return if the philo could be deleted
	 */
	public boolean deletePhiloByID(int philoID){
		boolean deleted=false; 
		int philoListPos=-1;
		for(Philosopher p:philoList){
			if(philoID==p.getPhiloID()){
				p.goDie();
				philoListPos=philoList.indexOf(p);
				break;
			}
		}
		if (philoListPos>=0){
			
			philoList.remove(philoListPos);
			deleted=true;
		}
		return deleted;
	}

	// CB
	private void lockAllNecessarySeatsForDeleteASeatForInsertASeat(int seatIndex) throws Exception {
		for (int i = seatIndex; i < seatList.size(); i++) {
			seatList.get(i).take(true);
		}
		if (this.agentID < otherAgents.size()) {
			for (int i = this.agentID; i < otherAgents.size(); i++) {
				otherAgents.get(i).lockAllSeats();
			}
		}

	}

	// CB
	public void incrementFirstSeatID() throws RemoteException {
		this.firstSeatID++;
	}

	// CB
	public void incrementLastSeatID() throws RemoteException {
		this.lastSeatID++;
	}

	// CB
	public void incrementSeatIDForAllSeats() throws RemoteException {
		for (Seat seat : seatList) {
			seat.incrementSeatID();
		}
	}

	@Override
	public void shutDownPhilos() throws RemoteException {
		//this.philosDown.set(true);
		//tell all philos they have to wait
		boolean allDownOrDead=false;
		for(Philosopher p :philoList){
			p.setShutDown(true);
		}
		//check if all philos waited
		//while loop only returns if all are down or dead.
		while(!allDownOrDead){
			allDownOrDead=true;
			for(Philosopher p :philoList){
				allDownOrDead= allDownOrDead&&(p.isShutDownAknowledged()||p.isPhiloDone());
			}
		}
		
	}

	@Override
	public void wakeUpPhilos() throws RemoteException {
		this.philosDown.set(false);
		//wakes up philos. philos manage their shutdown and acknowledged fields themselve.
		for(Philosopher p :philoList){
			synchronized (p) {
				p.notify();
			}
		}
	}
	
	public int getHighestPhiloId(){
		int highestPhiloID=0;
		for(Philosopher p: philoList){
			if(p.getPhiloID()>highestPhiloID){
				highestPhiloID=p.getPhiloID();
			}
		}
		return highestPhiloID;
		
	}

}