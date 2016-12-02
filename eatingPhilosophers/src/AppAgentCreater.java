import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

public class AppAgentCreater {
	
	static int startPort = 5200;
	static int numberOfAgents = 4;

	public static void main(String[] args) throws RemoteException {
		
		for(int i = 0; i < numberOfAgents; i++){
			Registry registryOne = LocateRegistry.createRegistry(startPort+i);
			registryOne.rebind("agent", new Agent(i));
		}
	}
	
	//dont initialize agents but an agent factory or something of that sort that we give .
}