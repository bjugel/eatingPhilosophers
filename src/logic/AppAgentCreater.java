package logic;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

public class AppAgentCreater {
	
	static int startPort = 5500;
	static int numberOfAgents =5;

	public static void main(String[] args) throws RemoteException {
		
		for(int i = 0; i < numberOfAgents; i++){
			//Registry registryOne = LocateRegistry.createRegistry(startPort+i);
			Registry registryOne = LocateRegistry.getRegistry("192.168.56.103", startPort+i);
			registryOne.rebind("agent", new Agent(i));
			//TODO later give the agents new numbers
		}
	}
	
	//TODO dont initialize agents but an agent factory or something of that sort that we give .
}