package logic;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;

public class AgentFactory extends UnicastRemoteObject implements  AgentFactoryInterface {
	
	
	protected AgentFactory() throws RemoteException {
		super();
	}
	
	public AgentInterface giveAgent(int agentID) throws RemoteException{
			return new Agent(agentID);
	}

}
