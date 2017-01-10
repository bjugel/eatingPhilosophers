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

public class Master2 {

	int numberOfPhilo = 50;
	int numberOfSeats = 30;
	int numberOfAgents = 5;
	int startPort = 1099;
	int tolerance = 20;
	int secondsToWait = 60;
	String listOfSeatsToDelete[] = new String[] { "A0S3", "A1S0", "A1S4", "A3S4", "A0S2", "A1S2", "A3S2", "A3S4" };
	// you will insert a new seat after AxSy
	String listOfSeatsToInsert[] = new String[] { "A0S1", "A1S1", "A3S3" };
	String ipList[] = new String[] { "127.0.0.1" };
	int numberOfPhilosToInsert = 4;
	int listOfPhilosToDelete[] = new int[] { 20, 15, 3 };
	int listOfPhilosToSetHungry[] = new int[] { 2, 10, 4 };
	List<AgentInterface> agentList;
	List<AgentFactoryInterface> agentFactoryList;

	TableSecurityMaster tableSec;
	long endTime;
	long startTime;

	public static void main(String[] args) throws Exception {
		System.setProperty("sun.rmi.transport.tcp.responseTimeout", "2000000"); // LOOK
																				// this
																				// is
																				// a
																				// problem
																				// here
		Master2 master = new Master2();
		// You will insert a new philo after x

		master.agentList = new ArrayList<AgentInterface>();
		master.agentFactoryList = new ArrayList<AgentFactoryInterface>();
		master.initializeEnvironment(master.numberOfPhilo, master.numberOfSeats, master.numberOfAgents,
				master.startPort, master.agentList, master.tolerance, master.secondsToWait);

		// Master waits for 100ms then he will start to delete the some seats
		TimeUnit.MILLISECONDS.sleep(100);
		master.deleteSeats(master.agentList, master.listOfSeatsToDelete);

		// Master waits another 100ms then he will insert some new seats
		TimeUnit.MILLISECONDS.sleep(100);
		master.insertSeats(master.agentList, master.listOfSeatsToInsert);

		TimeUnit.MILLISECONDS.sleep(100);
		// TODONEXT adding philos just works one time.
		master.insertPhilos(master.agentList, master.numberOfPhilosToInsert, master.tableSec, master.endTime);
		master.insertPhilos(master.agentList, master.numberOfPhilosToInsert, master.tableSec, master.endTime);
		master.insertPhilos(master.agentList, master.numberOfPhilosToInsert, master.tableSec, master.endTime);
		master.insertPhilos(master.agentList, master.numberOfPhilosToInsert, master.tableSec, master.endTime);

		TimeUnit.MILLISECONDS.sleep(100);
		master.removePhilos(master.agentList, master.listOfPhilosToDelete, master.tableSec);

		/*
		 * for(AgentInterface agent1:agentList){
		 * System.out.println("Agent:"+agent1.toString()+"has other agent" +
		 * agent1.getOtherAgents().get(0).toString()); }
		 */
	}

	public Master2() {
		super();

	}

	public void removePhilos(List<AgentInterface> agentList, int[] listOfPhilosToDelete, TableSecurityMaster tableSecurity)
			throws InterruptedException, RemoteException {
		synchronized (tableSecurity) {

			tableSecurity.shutDown();

			tableSecurity.wait();
		}

		System.out.println("TODO TRHOW PHILOS BUT THIS WOULD BE DONE HERE");
		for (int philoID : listOfPhilosToDelete) {
			for (AgentInterface a : agentList) {
				if (a.deletePhiloByID(philoID)) {
					System.out.println("Deleted Philosopher with ID:" + philoID);
					break;
				}
			}
		}

		synchronized (tableSecurity) {
			tableSecurity.notify();
			tableSecurity.wait(); // totally retardet stuff waiting for the
									// other one to wake us up again since we
									// dont understand shit
		}

	}

	/**
	 * 
	 * @param agentList
	 *            a list of all agents that are currently up and running.
	 * @param numberOfPhilosToInsert
	 *            a list of all positions where philosophers are to be inserted.
	 * @throws InterruptedException
	 * @throws RemoteException
	 */
	public void insertPhilos(List<AgentInterface> agentList, int numberOfPhilosToInsert, TableSecurityMaster tableSecurity,
			long endTime) throws InterruptedException, RemoteException {

		synchronized (tableSecurity) {

			tableSecurity.shutDown();
			System.out.println("waiting for SECUR");
			tableSecurity.wait();
		}

		System.out.println("INSERTING PHILOS AND SEEING IF ALL WORKS");
		// iterate through list
		int lowestPhiloNumber = -1;
		int philoNum;
		int localHighestPhiloID;
		int highestPhiloID = 0;
		// get the smallest philo number and highest philo id
		for (AgentInterface a : agentList) {
			philoNum = a.getNumberOfPhilos();

			if (lowestPhiloNumber == -1) {
				lowestPhiloNumber = philoNum;
			}
			if (philoNum < lowestPhiloNumber) {
				lowestPhiloNumber = philoNum;
			}

			localHighestPhiloID = a.getHighestPhiloId();
			if (localHighestPhiloID > highestPhiloID) {
				highestPhiloID = localHighestPhiloID;
			}
		}
		// insert philos to the first agent with lowestPhiloCount
		for (AgentInterface a : agentList) {
			if (a.getNumberOfPhilos() == lowestPhiloNumber) {

				a.addAndStartPhilos(numberOfPhilosToInsert, highestPhiloID + 1, endTime);
				System.out.printf("Addet %03d Philos to Agent %03d, numberOfPhilosFor this Agent is %03d, \n",
						numberOfPhilosToInsert, a.getAgentID(), a.getNumberOfPhilos());

				break;
			}
		}

		// insert philos so that they are spread equally and start them aswell
		// (give them a time when to wake up)
		// wake up table security;
		synchronized (tableSecurity) {
			tableSecurity.notify();
			tableSecurity.wait(); // totally retardet stuff waiting for the
									// other one to wake us up again since we
									// dont understand shit

		}

	}

	/**
	 * This method calls the method insertSeat() for the specific agent
	 * 
	 * @param agentList
	 *            is the current list of all agents
	 * @param listOfSeatsToInsert
	 *            is the list with the seats to insert
	 * @throws Exception
	 */
	public void insertSeats(List<AgentInterface> agentList, String[] listOfSeatsToInsert) throws Exception {
		for (AgentInterface agent : agentList) {
			agent.shutDownPhilos();
		}
		for (String s : listOfSeatsToInsert) {
			String subString = s.substring(1, 2);
			int agentID = Integer.parseInt(subString);
			agentList.get(agentID).insertSeat(s);
		}
		for (AgentInterface agent : agentList) {
			agent.wakeUpPhilos();
		}
	}

	/**
	 * This method calls the method deleteSeat() for the specific agent
	 * 
	 * @param agentList
	 *            is the current list of all agents
	 * @param listOfSeatsToDelete
	 *            is the list with the seats to delete
	 * @throws Exception
	 */
	public void deleteSeats(List<AgentInterface> agentList, String[] listOfSeatsToDelete) throws Exception {
		for (AgentInterface agent : agentList) {
			agent.shutDownPhilos();
		}
		for (String s : listOfSeatsToDelete) {
			String subString = s.substring(1, 2);
			int agentID = Integer.parseInt(subString);
			agentList.get(agentID).deleteSeat(s);
		}
		for (AgentInterface agent : agentList) {
			agent.wakeUpPhilos();
		}
	}

	/**
	 * This method give every agent a list with all other agents
	 * 
	 * @param agentList
	 *            is the current list of all agents
	 * @throws Exception
	 */
	public void giveEachAgentTheOtherAgentsAndNext() throws Exception {
		for (AgentInterface agent1 : agentList) {
			agent1.clearOtherAgentList();
			// killer line giving each agent its privious and next counterpart
			agent1.givePriviousAndNextAgent(
					agentList.get((agentList.indexOf(agent1) + (agentList.size() - 1)) % agentList.size()),
					agentList.get((agentList.indexOf(agent1) + 1) % agentList.size()));
			for (AgentInterface agent2 : agentList) {
				if (agent1 != agent2) {
					agent1.giveOtherAgent(agent2);
				}
			}
		}
	}

	/**
	 * This method gives every agent the signal to start the philosopher threads
	 * 
	 * @param agentList
	 *            is the current list of all agents
	 */
	public void getPhilosophersUpAndMeditating(List<AgentInterface> agentList, long endTime) {
		try {
			for (AgentInterface a : agentList) {

				a.startPhilosophers(endTime);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * collects all the agents that run on different machines and gives them the
	 * parameters they have to know to initialize CARE after this method it is
	 * necessary to give each agent the respective others to have the programm
	 * functioning correctly
	 * 
	 * @param numberOfPhilo
	 * @param numberOfSeats
	 * @param numberOfAgents
	 * @param startPort
	 * @param agentList
	 */
	public void initializeEnvironment(int numberOfPhilo, int numberOfSeats, int numberOfAgents, int startPort,
			List<AgentInterface> agentList, int tolerance, int secondsToWait) {

		try {
			getAllAgents();

			giveEachAgentTheOtherAgentsAndNext();

			initializeForksForAllAgents();
			initializeSeatsForAllAgents();

			initializePhilosophesForAllAgents();

			setPhiloHungry(agentList);// CB

			System.out.println("Philos will run now.");
			startTime = System.currentTimeMillis();
			getTableSecurityUpAndRunning(numberOfPhilo, agentList, tolerance, startTime);// CB
			this.endTime = startTime + 1000 * secondsToWait;
			getCrashSecurityUpAndRunning();
			getPhilosophersUpAndMeditating(agentList, endTime);

		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	private void getCrashSecurityUpAndRunning() {
		CrashSecurity crashSecurity = new CrashSecurity(agentList, endTime, this);
		new Thread(crashSecurity).start();

	}

	/**
	 * This method set which philosopher should be hungry Here you can decide by
	 * your own which philosopher you want to set hungry
	 * 
	 * @param agentList
	 *            is the current list of all agents
	 * @throws RemoteException
	 */
	public void setPhiloHungry(List<AgentInterface> agentList) throws RemoteException {

		for (Integer philoID : listOfPhilosToSetHungry) {
			for (AgentInterface agent : agentList) {
				agent.setPhiloHungry(philoID);
			}
		}

	}

	/**
	 * This method starts the TableSecurity thread
	 * 
	 * @param numberOfPhilo
	 *            is the current number of philosophers
	 * @param agentList
	 *            is the current list of all agents
	 * @param tolerance
	 *            is how much difference to the average eating counter the
	 *            TableSecurity will accept
	 */
	public void getTableSecurityUpAndRunning(int numberOfPhilo, List<AgentInterface> agentList, int tolerance,
			long startTime) {
		tableSec = new TableSecurityMaster(numberOfPhilo, agentList, tolerance, startTime);
		Thread security = new Thread(tableSec);
		security.start();
	}

	/**
	 * This method initialize all Seats at the beginning
	 * 
	 * @param numberOfSeats
	 *            is the number of seats at the beginning
	 * @param numberOfAgents
	 *            is the number of agents at the beginning
	 * @param agentList
	 *            is the list of all agents at the beginning
	 * @throws RemoteException
	 */
	public void initializeSeatsForAllAgents() throws RemoteException {
		AgentInterface currAgent;
		for (int i = 0; i < numberOfAgents; i++) {
			currAgent = agentList.get(i);
			if (i == 0) {
				currAgent.initSeats(numberOfSeats / numberOfAgents + numberOfSeats % numberOfAgents, 0);
			} else {
				currAgent.initSeats(numberOfSeats / numberOfAgents,
						i * (numberOfSeats / numberOfAgents) + numberOfSeats % numberOfAgents);
			} // CB also changed the term to calculate the first seatID like we
				// did at the philos

		}

	}

	/**
	 * This method initialize all necessary forks at the beginning
	 * 
	 * @param numberOfSeats
	 *            is the number of seats at the beginning
	 * @param numberOfAgents
	 *            is the number of the agents at the beginning
	 * @param agentList
	 *            is the list of all agents at the beginning
	 * @throws RemoteException
	 */
	public void initializeForksForAllAgents() throws RemoteException {

		AgentInterface currAgent;
		for (int i = 0; i < numberOfAgents; i++) {
			currAgent = agentList.get(i);
			if (i == 0) {
				currAgent.initForks(numberOfSeats / numberOfAgents + numberOfSeats % numberOfAgents);
			} else {
				currAgent.initForks(numberOfSeats / numberOfAgents);
			}
		}

	}

	/**
	 * This method initialize all Philosophers at the beginning
	 * 
	 * @param numberOfPhilo
	 *            is the number of Philosopher at the beginning
	 * @param numberOfAgents
	 *            is the number of agents at the beginning
	 * @param agentList
	 *            is the list of all agents at the beginning
	 * @throws RemoteException
	 */
	public void initializePhilosophesForAllAgents() throws RemoteException {
		AgentInterface currAgent;
		for (int i = 0; i < numberOfAgents; i++) {
			currAgent = agentList.get(i);
			if (i == 0) {
				currAgent.initialzePhilos(numberOfPhilo / numberOfAgents + numberOfPhilo % numberOfAgents, 0);
			} else {
				currAgent.initialzePhilos(numberOfPhilo / numberOfAgents,
						i * (numberOfPhilo / numberOfAgents) + numberOfPhilo % numberOfAgents);
			}

		}
	}

	public void getAllWorkingAgentFactories() {
		agentFactoryList.clear();
		for (int i = 0; i < ipList.length; i++) {
			Registry remoteRegistry;
			try {
				remoteRegistry = LocateRegistry.getRegistry(ipList[i], startPort);
				agentFactoryList.add((AgentFactoryInterface) remoteRegistry.lookup("agentFactory"));
			} catch (Exception e) {
				System.out.println("couldnt find factory on ip:" + ipList[i]);
				e.printStackTrace();
			}
		}

	}

	/**
	 * This method is getting connected to all up agents at the beginning
	 * 
	 * @param numberOfAgents
	 *            is the number of agents at the beginning
	 * @param startPort
	 *            is the Port where the method will start to lookup
	 * @param agentList
	 *            is the list where the method will write the agents
	 * @throws NotBoundException
	 * @throws MalformedURLException
	 * @throws RemoteException
	 */
	public void getAllAgents() throws NotBoundException, MalformedURLException, RemoteException {
		agentList.clear();
		getAllWorkingAgentFactories();
		AgentFactoryInterface currFactory;
		int agentCounter = 0;

		for (int i = 0; i < agentFactoryList.size(); i++) {
			currFactory = agentFactoryList.get(i);
			if (i == 0) {
				for (int j = 0; j < numberOfAgents / agentFactoryList.size()
						+ numberOfAgents % agentFactoryList.size(); j++) {
					agentList.add(currFactory.giveAgent(agentCounter));
					agentCounter++;
				}
			} else {
				for (int j = 0; j < numberOfAgents / agentFactoryList.size(); j++) {
					agentList.add(currFactory.giveAgent(agentCounter));
					agentCounter++;
				}
			}
		}
		System.out.println("Created " + agentCounter + " new Agents.");

		// Registry remoteRegistry = LocateRegistry.getRegistry(ipList[i],
		// startPort);
		// agentFactoryList.add((AgentFactoryInterface)
		// remoteRegistry.lookup("agentFactory"));// CB

		// agentList.add((AgentInterface)remoteRegistry.lookup("agent"));
		// agentFactoryList.add((AgentFactoryInterface)
		// Naming.lookup("rmi://"+ ipList[i] +":" + (startPort) +
		// "/agentFactory"));// CB
		// changed
		// to
		// agent
		// agentList.add((AgentInterface)Naming.lookup("rmi://192.168.56.103:"
		// + (startPort + i) + "/agent"));//CB changed to agent
		// Registry remoteRegistry =
		// LocateRegistry.getRegistry("192.168.56.101",startPort+i);
		// agentList.add((AgentInterface)remoteRegistry.lookup("agent"));
	}

	public void reinitializeEnvironment(int numberOfSeatsBackUp, List<Integer> philoIDsBackUp,
			List<Integer> philosEatingCountersBackUp) throws MalformedURLException, RemoteException, NotBoundException {
		try {
			this.numberOfSeats = numberOfSeatsBackUp;
			this.numberOfPhilo = philosEatingCountersBackUp.size();

			getAllAgents();
			giveEachAgentTheOtherAgentsAndNext();
			initializeForksForAllAgents();
			initializeSeatsForAllAgents();

			reinitializePhilosophesForAllAgents(philoIDsBackUp, philosEatingCountersBackUp);
			setPhiloHungry(agentList);// CB

			System.out.println("Philos will run now.");
			getTableSecurityUpAndRunning(numberOfPhilo, agentList, tolerance, startTime);// CB
			getCrashSecurityUpAndRunning();
			getPhilosophersUpAndMeditating(agentList, endTime);

		} catch (Exception ex) {
			ex.printStackTrace();
		}

	}

	private void reinitializePhilosophesForAllAgents(List<Integer> philoIDsBackUp,
			List<Integer> philosEatingCountersBackUp) throws RemoteException {
		this.numberOfPhilo = philosEatingCountersBackUp.size();
		System.out.println("Test1");
		AgentInterface currAgent;
		int nextPhiloIndex = 0;
		int philosToAddToCurrAgent;
		for (int i = 0; i < numberOfAgents; i++) {
			currAgent = agentList.get(i);
			if (i == 0) {
				System.out.println("Test2");
				philosToAddToCurrAgent = (numberOfPhilo / numberOfAgents + numberOfPhilo % numberOfAgents);
				nextPhiloIndex = addPredefinedPhilos(nextPhiloIndex, philosToAddToCurrAgent, philoIDsBackUp,
						philosEatingCountersBackUp, currAgent);
				System.out.println("in first agnet adding philos");
			} else {
				philosToAddToCurrAgent = (numberOfPhilo / numberOfAgents);
				nextPhiloIndex = addPredefinedPhilos(nextPhiloIndex, philosToAddToCurrAgent, philoIDsBackUp,
						philosEatingCountersBackUp, currAgent);
				System.out.println("in "+ i +". agnet adding philos");
			}

		}

	}

	/**
	 * 
	 * @param firstPhiloIndex
	 *            the index of the next philo in the list that we want to add
	 * @param numberOfPhilosToAdd
	 *            how many philos we want to add to the given agent
	 * @param philoIDsBackUp
	 *            the list of philoIDs from which we read which ids to give the
	 *            newly initialized philos
	 * @param philosEatingCountersBackUp
	 *            the list of eatingCounters from which we read which ids to
	 *            give the newly initialized philos
	 * @param agent
	 *            the agent we want to add the philos to
	 * @return the index of the next philo in the list that has not been
	 *         reinitialized and assigned to an Agent yet
	 * @throws RemoteException
	 */
	private int addPredefinedPhilos(int firstPhiloIndex, int numberOfPhilosToAdd, List<Integer> philoIDsBackUp,
			List<Integer> philosEatingCountersBackUp, AgentInterface agent) throws RemoteException {
		// since we add 1 in the returnvalue
		int currPhilIndex = firstPhiloIndex - 1;

		for (int i = 0; i < numberOfPhilosToAdd; i++) {
			currPhilIndex = firstPhiloIndex + i;
			agent.addPhilo(philoIDsBackUp.get(currPhilIndex), philosEatingCountersBackUp.get(currPhilIndex));
		}
		// the next phill index we want to add in the 0 position of the next
		// agent.
		return currPhilIndex + 1;
	}

	public void killTableSecurity() throws RemoteException {
		tableSec.setUseless();

	}
}
