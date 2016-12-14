package logic;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.concurrent.locks.ReentrantLock;

import dataObjects.Seat;
import dataObjects.TableFork;

public interface AgentInterface extends Remote {
	
	
	public void initialzePhilos(int philos, int firstPhiloID) throws RemoteException;	
	public void initForks(int numberOfSeats)throws RemoteException;
	public void initSeats(int numberOfSeats, int firstSeatID)throws RemoteException;
	public ArrayList<TableFork> getForks()throws RemoteException;
	public ArrayList<Seat> getSeats()throws RemoteException;
	public void startPhilosophers(long endTimes)throws RemoteException;
	public void giveOtherAgent(AgentInterface agent) throws RemoteException;
	public ArrayList<AgentInterface> getOtherAgents()throws RemoteException;
	public void givePriviousAndNextAgent(AgentInterface priviousAgent,AgentInterface nextAgent)throws RemoteException;
	public int getAgentID()throws RemoteException;
	public int giveSeat(int callingAgentID)throws RemoteException;
	public void standUp(int seatID)throws Exception;
	public boolean lockFirstFork() throws RemoteException;
	public void giveForks(int seatID,int philoID) throws RemoteException;
	public void releaseForks(int seatID,int philoID) throws RemoteException, Exception;
	public void releaseFirstFork() throws RemoteException, Exception;
	public int getTotalTimesOfEating()throws RemoteException;
	public int getPhiloEatingCounter(int philoIndex)throws RemoteException;
	public void catchPhilo(int philoIndex)throws RemoteException;
	public void setPhiloHungry(int philoIndex)throws RemoteException;
	public int getNumberOfPhilos()throws RemoteException;
	public boolean isPhiloDone(int philoIndex)throws RemoteException;
	public boolean isPhiloCatched(int philoIndex)throws RemoteException;
	public int getFirstPhiloID()throws RemoteException;
	public void deleteSeat(String seatName)throws Exception;
	public void lockAllSeats()throws RemoteException;
	public void decrementSeatIDForAllSeats()throws RemoteException;
	public void decrementFirstSeatID()throws RemoteException;
	public void decrementLastSeatID()throws RemoteException;
	public void leaveAllSeats() throws Exception;
	public void clearOtherAgentList()throws Exception;
	public AgentInterface getPriviousAgent() throws Exception;
	public void lockLastSeat()throws Exception;
	public void unlockLastSeat()throws Exception;
	public void insertSeat(String seatName)throws Exception;
	public void incrementFirstSeatID()throws RemoteException;
	public void incrementLastSeatID()throws RemoteException;
	public void incrementSeatIDForAllSeats()throws RemoteException;
	public void shutDownPhilos() throws RemoteException;
	public void wakeUpPhilos() throws RemoteException;
}