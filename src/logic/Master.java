package logic;
import java.util.*;
import java.util.concurrent.TimeUnit;

import sun.management.resources.agent;

import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
//import TimeUnit;


public class Master {
	
	

	public static void main(String[] args) throws Exception {
		
		int numberOfPhilo = 40;
		int numberOfSeats = 20;
		int numberOfAgents = 4;
		int startPort = 5500;
		int tolerance =20;
		int secondsToWait=60;
		
		String listOfSeatsToDelete[] = new String[]{"A0S3","A1S0","A1S4","A3S4","A0S2","A1S2","A3S2","A3S4"}; //Ax = id of agent || Sy = index of seat !!!Make sure that you are initialize this seats 
		String listOfSeatsToInsert[] = new String[]{"A0S1","A1S1","A3S3"};//You will insert a new seat after AxSy
		
		List<AgentInterface> agentList;
		
		agentList= new ArrayList<AgentInterface>();
		
		initializeEnvironment(numberOfPhilo, numberOfSeats, numberOfAgents, startPort, agentList, tolerance,secondsToWait);				
		
		//Master waits for 100ms then he will start to delete the some seats
		TimeUnit.MILLISECONDS.sleep(100);
		deleteSeats(agentList, listOfSeatsToDelete);
		
		//Master waits another 100ms then he will insert some new seats
		TimeUnit.MILLISECONDS.sleep(100);
		insertSeats(agentList, listOfSeatsToInsert);
		
		
		/*for(AgentInterface agent1:agentList){
			System.out.println("Agent:"+agent1.toString()+"has other agent" + agent1.getOtherAgents().get(0).toString());
		}*/
	}


	/**
	 *This method calls the method insertSeat() for the specific agent
	 * @param agentList is the current list of all agents
	 * @param listOfSeatsToInsert is the list with the seats to insert
	 * @throws Exception
	 */
	private static void insertSeats(List<AgentInterface> agentList, String[] listOfSeatsToInsert) throws Exception {
		for(AgentInterface agent: agentList){
			agent.shutDownPhilos();
		}
		for(String s: listOfSeatsToInsert){
			String subString = s.substring(1, 2);
			int agentID = Integer.parseInt(subString);
			agentList.get(agentID).insertSeat(s);
		}
		for(AgentInterface agent: agentList){
			agent.wakeUpPhilos();
		}
	}


	/**
	 * This method calls the method deleteSeat() for the specific agent
	 * @param agentList is the current list of all agents
	 * @param listOfSeatsToDelete is the list with the seats to delete
	 * @throws Exception
	 */
	private static void deleteSeats(List<AgentInterface> agentList, String[] listOfSeatsToDelete) throws Exception {
		for(AgentInterface agent: agentList){
			agent.shutDownPhilos();
		}
		for(String s: listOfSeatsToDelete){
			String subString = s.substring(1, 2);
			int agentID = Integer.parseInt(subString);
			agentList.get(agentID).deleteSeat(s);
		}
		for(AgentInterface agent: agentList){
			agent.wakeUpPhilos();
		}
	}


	/**
	 * This method give every agent a list with all other agents
	 * @param agentList is the current list of all agents
	 * @throws Exception
	 */
	private static void giveEachAgentTheOtherAgentsAndNext(List<AgentInterface> agentList) throws Exception {
		for(AgentInterface agent1:agentList){
			agent1.clearOtherAgentList();
			//killer line giving each agent its privious and next  counterpart
			agent1.givePriviousAndNextAgent(agentList.get((agentList.indexOf(agent1)+(agentList.size()-1))%agentList.size()),agentList.get((agentList.indexOf(agent1)+1)%agentList.size()));
			for(AgentInterface agent2:agentList){
				if (agent1!=agent2){
					agent1.giveOtherAgent(agent2);
				}
			}		 
		}
	}

	
	/**
	 * This method gives every agent the signal to start the philosopher threads
	 * @param agentList is the current list of all agents
	 */
	private static void getPhilosophersUpAndMeditating(List<AgentInterface> agentList,long endTime) {
		try {
			for(AgentInterface a:agentList){
				
				a.startPhilosophers(endTime);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	
	/**
	 * collects all the agents that run on different machines and gives them the parameters they have to know to initialize
	 * CARE after this method it is necessary to give each agent the respective others to have the programm functioning correctly 
	 * @param numberOfPhilo
	 * @param numberOfSeats
	 * @param numberOfAgents
	 * @param startPort
	 * @param agentList
	 */
	private static void initializeEnvironment(int numberOfPhilo, int numberOfSeats, int numberOfAgents, int startPort,
			List<AgentInterface> agentList, int tolerance,int secondsToWait) {
		
		long endTime; 
		long startTime;
		try{
			getAllAgents(numberOfAgents, startPort, agentList);
			
			giveEachAgentTheOtherAgentsAndNext(agentList);
			
			initializeForksForAllAgents(  numberOfSeats, numberOfAgents,agentList);
			initializeSeatsForAllAgents(  numberOfSeats, numberOfAgents,agentList);
			
			initializePhilosophesForAllAgents(numberOfPhilo, numberOfAgents, agentList);
			
			setPhiloHungry(agentList);//CB
			
			System.out.println("Philos will run now.");
			startTime=System.currentTimeMillis();
			getTableSecurityUpAndRunning(numberOfPhilo, agentList, tolerance,startTime);//CB
			
			endTime=startTime+1000*secondsToWait;
			getPhilosophersUpAndMeditating(agentList,endTime);
			
			
		} catch(Exception ex){
			ex.printStackTrace();
		}
	}


	/**
	 * This method set which philosopher should be hungry
	 * Here you can decide by your own which philosopher you want to set hungry
	 * @param agentList is the current list of all agents
	 * @throws RemoteException
	 */
	private static void setPhiloHungry(List<AgentInterface> agentList) throws RemoteException {
		agentList.get(2).setPhiloHungry(1);
		agentList.get(0).setPhiloHungry(4);
		/*for(AgentInterface agent: agentList){
			agent.setPhiloHungry(0);
		}*/
	}


	/**
	 * This method starts the TableSecurity thread
	 * @param numberOfPhilo is the current number of philosophers
	 * @param agentList is the current list of all agents
	 * @param tolerance is how much difference to the average eating counter the TableSecurity will accept 
	 */
	private static void getTableSecurityUpAndRunning(int numberOfPhilo, List<AgentInterface> agentList, int tolerance,long startTime) {
		Thread security = new Thread(new TableSecurity(numberOfPhilo, agentList, tolerance,startTime));
		security.start();
	}


	/**
	 *This method initialize all Seats at the beginning
	 * @param numberOfSeats is the number of seats at the beginning
	 * @param numberOfAgents is the number of agents at the beginning
	 * @param agentList is the list of all agents at the beginning
	 * @throws RemoteException
	 */
	private static void initializeSeatsForAllAgents(int numberOfSeats, int numberOfAgents,
			List<AgentInterface> agentList) throws RemoteException {
		AgentInterface currAgent;
		for(int i = 0; i < numberOfAgents; i++){
			currAgent=agentList.get(i);
			if(i==0){
				currAgent.initSeats(numberOfSeats/numberOfAgents+numberOfSeats%numberOfAgents,0); 
			}else{
				currAgent.initSeats(numberOfSeats/numberOfAgents,	i*(numberOfSeats/numberOfAgents)+numberOfSeats%numberOfAgents); 
			}//CB also changed the term to calculate the first seatID like we did at the philos
			
		}
		
	}


	/**
	 * This method initialize all necessary forks at the beginning  
	 * @param numberOfSeats is the number of seats at the beginning
	 * @param numberOfAgents is the number of the agents at the beginning
	 * @param agentList is the list of all agents at the beginning
	 * @throws RemoteException
	 */
	private static void initializeForksForAllAgents(int numberOfSeats, int numberOfAgents,
			List<AgentInterface> agentList) throws RemoteException {
		
		AgentInterface currAgent;
		for(int i = 0; i < numberOfAgents; i++){
			currAgent=agentList.get(i);
			if(i==0){
				currAgent.initForks(numberOfSeats/numberOfAgents+numberOfSeats%numberOfAgents);
			}else{
				currAgent.initForks(numberOfSeats/numberOfAgents); 
			}		
		}
		
	}


	/**
	 * This method initialize all Philosophers at the beginning
	 * @param numberOfPhilo is the number of Philosopher at the beginning 
	 * @param numberOfAgents is the number of agents at the beginning
	 * @param agentList is the list of all agents at the beginning
	 * @throws RemoteException
	 */
	private static void initializePhilosophesForAllAgents(int numberOfPhilo,  int numberOfAgents,
			List<AgentInterface> agentList) throws RemoteException {
		AgentInterface currAgent;
		for(int i = 0; i < numberOfAgents; i++){
			currAgent=agentList.get(i);
			if(i==0){
				currAgent.initialzePhilos(numberOfPhilo/numberOfAgents+numberOfPhilo%numberOfAgents,0); 
			}else{
				currAgent.initialzePhilos(numberOfPhilo/numberOfAgents,	i*(numberOfPhilo/numberOfAgents)+numberOfPhilo%numberOfAgents); 
			}
			
		}
	}


	/**
	 *This method is getting connected to all up agents at the beginning
	 * @param numberOfAgents is the number of agents at the beginning 
	 * @param startPort is the Port where the method will start to lookup
	 * @param agentList is the list where the method will write the agents
	 * @throws NotBoundException
	 * @throws MalformedURLException
	 * @throws RemoteException
	 */
	private static void getAllAgents(int numberOfAgents, int startPort, List<AgentInterface> agentList)
			throws NotBoundException, MalformedURLException, RemoteException {
		for(int i = 0; i < numberOfAgents; i++){

			agentList.add((AgentInterface)Naming.lookup("rmi://127.0.0.1:" + (startPort + i) + "/agent"));//CB changed to agent
			
		}
	}
	
	
	

}
