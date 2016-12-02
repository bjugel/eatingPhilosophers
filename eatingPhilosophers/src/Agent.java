import java.rmi.RemoteException;
import java.rmi.Remote;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.concurrent.locks.ReentrantLock;

import dataObjects.Philosopher;
import dataObjects.Seat;

import java.rmi.Naming;


public class Agent extends UnicastRemoteObject implements AgentInterface{
	
	private ArrayList<AgentInterface> otherAgents = new ArrayList <AgentInterface>();
	private ArrayList<Philosopher> philoList = new ArrayList<Philosopher>();
	private ArrayList<Seat> seatList = new ArrayList<Seat>();
	private ArrayList<ReentrantLock> forks = new ArrayList<ReentrantLock>();//BJ
	private int agentID=0;
	private AgentInterface nextAgent;
	public Agent() throws RemoteException{
		super();
	}
	
	public Agent(int agentID) throws RemoteException{
		super();
		this.agentID=agentID;
		
	}
	
	
	
	public void initialzePhilos(int numberOfPhilos, int numberOfSeats, int firstPhiloID, int firstSeatID){
		initPhiloList(numberOfPhilos, firstPhiloID);
		System.out.println("Agent"+agentID+ " initializing "+numberOfPhilos+ " Philosophers with starting number " + firstPhiloID); //TODO
		//TODO fill change initialize to different initializations
		//TODO change init methods in master
		//TODO remove some arguments here
	}
	/**
	 * CARE all forks have to be initialized before and next agents have to be given called.
	 * @param numberOfSeats
	 * @param firstSeatID
	 * @throws RemoteException 
	 */
	private void initSeatList(int numberOfSeats, int firstSeatID) throws RemoteException {
		seatList.clear();
		for (int i = 0; i < numberOfSeats; i++) {
			if (i+1<numberOfSeats){
				seatList.add(new Seat(firstSeatID+i, forks.get(i), forks.get(i+1)));
			}else{
				seatList.add(new Seat(firstSeatID+i, forks.get(i), nextAgent.getForks().get(0)));
			}
		}
		//TODO finish create seats and give them forks that are already initialized else we gett nullpointer. evtually create an forks not initialized exception.
	}
	public void initForks(int numberOfSeats) {
		forks.clear();
		for (int i = 0; i < numberOfSeats; i++) {
			forks.add(new ReentrantLock(true));
		}
		//TODO finish create seats and give them forks that are already initialized else we gett nullpointer. evtually create an forks not initialized exception.
	}

	public void addPhilos(int philos, int firstPhiloId){
		for (int i = 0; i < philos; i++) {
			
			philoList.add(new Philosopher(firstPhiloId+i));
		}
	}
	
	public ArrayList<ReentrantLock> getForks() {
		return forks;
	}

	public void giveOtherAgent(AgentInterface agent){
		this.otherAgents.add(agent);
	}
	public void giveNextAgent(AgentInterface nextAgent) throws RemoteException{
		this.nextAgent=nextAgent;
		System.out.println("I'm agent"+ agentID + " next up is Agent" + nextAgent.getAgentID() );
	}
	public void startPhilosophers(){
		for (Philosopher p: philoList){
			new Thread(p).start();	
		}
	}
	
	
	private void initPhiloList(int philos, int firstPhiloId){
		philoList.clear();
		addPhilos(philos, firstPhiloId);
	}
	
	
	
	private void fillSeatList(int philos, int firstPhiloId){
		for (int i = 0; i < philos; i++) {
			//TODO new Seat
		}
	}

	

	public int getAgentID() {
		return agentID;
	}

	@Override
	public String toString() {
		return "Agent"+agentID ;
	}
	
	public ArrayList<AgentInterface> getOtherAgents() {
		return otherAgents;
	}
}