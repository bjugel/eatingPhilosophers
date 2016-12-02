import java.util.*;
import java.rmi.Naming;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

public class Master {
	
	

	public static void main(String[] args) throws RemoteException {
		
		int numberOfPhilo = 41;
		int numberOfSeats = 5;
		int numberOfAgents = 4;
		int startPort = 5200;

		List<AgentInterface> agentList;
		
		agentList= new ArrayList<AgentInterface>();
		
		initializeEnvironment(numberOfPhilo, numberOfSeats, numberOfAgents, startPort, agentList);				
		giveEachAgentTheOtherAgentsAndNext(agentList);
		getPhilosophersUpAndMeditating(agentList);
		
		for(AgentInterface agent1:agentList){
			System.out.println("Agent:"+agent1.toString()+"has other agent" + agent1.getOtherAgents().get(0).toString());
			
		}
		
	}



	private static void giveEachAgentTheOtherAgentsAndNext(List<AgentInterface> agentList) throws RemoteException {
		for(AgentInterface agent1:agentList){
			//killer line giving each agent its next  counterpart
			agent1.giveNextAgent(agentList.get((agentList.indexOf(agent1)+1)%agentList.size()));
			for(AgentInterface agent2:agentList){
				if (agent1!=agent2){
					agent1.giveOtherAgent(agent2);
				}
			}		
		}
	}

	
	
	private static void getPhilosophersUpAndMeditating(List<AgentInterface> agentList) {
		try {
			for(AgentInterface a:agentList){
				a.startPhilosophers();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	/**
	 * collects all the agents that run on different machines and gives them the parameters they have to know to initialize
	 * CARE after this method it is neccesary to give each agent the respective others to have the programm functioning correctly 
	 * @param numberOfPhilo
	 * @param numberOfSeats
	 * @param numberOfAgents
	 * @param startPort
	 * @param agentList
	 */
	private static void initializeEnvironment(int numberOfPhilo, int numberOfSeats, int numberOfAgents, int startPort,
			List<AgentInterface> agentList) {
		AgentInterface currAgent;
		try{
			for(int i = 0; i < numberOfAgents; i++){

				agentList.add((AgentInterface)Naming.lookup("rmi://127.0.0.1:" + (startPort + i) + "/agent"));//CB changed to agent
				currAgent=agentList.get(i);
				if(i==0){
					currAgent.initialzePhilos(numberOfPhilo/numberOfAgents+numberOfPhilo%numberOfAgents, 
							numberOfSeats/numberOfAgents+numberOfSeats%numberOfAgents,0, 0); 
				}else{
					currAgent.initialzePhilos(numberOfPhilo/numberOfAgents, numberOfSeats/numberOfAgents,
										i*numberOfPhilo/numberOfAgents+numberOfPhilo%numberOfAgents, 
										i*numberOfSeats/numberOfAgents+numberOfSeats%numberOfAgents); 
				}
				
			}
			
		} catch(Exception ex){
			ex.printStackTrace();
		}
	}
	
	
	

}
