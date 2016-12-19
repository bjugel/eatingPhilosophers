package logic;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;

public class AppAgentCreater {
	
	static int startPort = 1099;
	static int numberOfAgents =4;

	public static void main(String[] args) throws RemoteException {
		
		for(int i = 0; i < numberOfAgents; i++){
			//System.setProperty("java.rmi.server.hostname", "192.168.56.101");
			//System.setProperty("java.rmi.server.codebase", "/root/git/eatingPhilosophers/bin/");
			Registry registryOne = LocateRegistry.createRegistry(startPort+i);
			//Registry registryOne = LocateRegistry.createRegistry(startPort+i);
			/*Agent agentObject =  new Agent(i);
			AgentInterface agent = (AgentInterface) UnicastRemoteObject.exportObject(agentObject, 0);
			registryOne.rebind("agent",agent);*/
			registryOne.rebind("agent",new Agent(i));
			//TODO later give the agents new numbers
			System.out.println("bound the agents");
		}
	}
	
	//TODO dont initialize agents but an agent factory or something of that sort that we give .
}