package logic;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface AgentFactoryInterface extends Remote{
	public AgentInterface giveAgent(int agentID) throws RemoteException;
}
