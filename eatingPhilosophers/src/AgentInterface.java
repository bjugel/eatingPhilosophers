import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.concurrent.locks.ReentrantLock;

public interface AgentInterface extends Remote {
	
	
	public void initialzePhilos(int philos, int places, int firstPhiloId, int firstPlaceId) throws RemoteException;	
	public void initForks(int numberOfSeats)throws RemoteException;
	public ArrayList<ReentrantLock> getForks()throws RemoteException;
	public void startPhilosophers()throws RemoteException;
	public void giveOtherAgent(AgentInterface agent) throws RemoteException;
	public ArrayList<AgentInterface> getOtherAgents()throws RemoteException;
	public void giveNextAgent(AgentInterface nextAgent)throws RemoteException;
	public int getAgentID()throws RemoteException;

}