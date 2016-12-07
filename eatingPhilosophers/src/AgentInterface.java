import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.concurrent.locks.ReentrantLock;

import dataObjects.Seat;

public interface AgentInterface extends Remote {
	
	
	public void initialzePhilos(int philos, int firstPhiloID) throws RemoteException;	
	public void initForks(int numberOfSeats)throws RemoteException;
	public void initSeats(int numberOfSeats, int firstSeatID)throws RemoteException;
	public ArrayList<ReentrantLock> getForks()throws RemoteException;
	public ArrayList<Seat> getSeats()throws RemoteException;
	public void lockFirstFork() throws RemoteException;
	public void startPhilosophers()throws RemoteException;
	public void giveOtherAgent(AgentInterface agent) throws RemoteException;
	public ArrayList<AgentInterface> getOtherAgents()throws RemoteException;
	public void giveNextAgent(AgentInterface nextAgent)throws RemoteException;
	public int getAgentID()throws RemoteException;

}