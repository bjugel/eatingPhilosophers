package logic;

import java.net.MalformedURLException;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.sql.Time;
import java.util.ArrayList;
import java.util.List;

import dataObjects.Philosopher;
import logic.AgentInterface;
import sun.management.resources.agent;

public class TableSecurityMaster implements Runnable {

	private int numberOfPhilos;
	private int numberOfAgents;
	private int averageTimesOfEating;
	private int totalTimesOfEating;
	private List<AgentInterface> agentList;
	private String catchedMessage;
	private boolean someoneGotCatched;
	private boolean philoIsCatched;
	private boolean philoIsDone;
	private int eatingCounter;
	private int tolerance;
	long startTime;
	private boolean shutDown;
	private boolean useless;

	public TableSecurityMaster(int numberOfPhilos, List<AgentInterface> agentList, int tolerance, long startTime) {
		this.totalTimesOfEating = 0;
		this.averageTimesOfEating = 0;
		this.numberOfPhilos = numberOfPhilos;
		this.agentList = agentList;
		this.numberOfAgents = agentList.size();
		this.catchedMessage = "";
		this.someoneGotCatched = false;
		this.philoIsCatched = false;
		this.philoIsDone = false;
		this.eatingCounter = -1;
		this.tolerance = tolerance;
		this.startTime = startTime;
		this.shutDown = false;
		this.useless = false;
	}

	@Override
	public void run() {
		//initialize and start securityHelper
		for (AgentInterface agent : agentList) {

			try {
				agent.initialzeSecurityHelper(tolerance);
				agent.startSecurityHelper();
			} catch (RemoteException e) {
				e.printStackTrace();
			}

		}
		System.out.println("TableSecurityMaster is running now. \n\n "
				+ "           --------------------ATTENTION!!!-------------------- \n\n  "
				+ "Everyone gets catched, who eats " + (tolerance + 1) + " times more than the average philosopher.\n\n"
				+ "\n\n");
		while (true && !(this.useless)) {

			// The table security collects all eatingCounter from the security helpers to
			// calculate the average times of eating.
			numberOfPhilos=0;
			for (AgentInterface agent : agentList) {

				try {
					totalTimesOfEating += agent.getTotalTimesOfEating();
					numberOfPhilos+=agent.getNumberOfPhilos();
				} catch (RemoteException e) {
					e.printStackTrace();
				}

			}

			// The table security is calculating the average times of eating.
			averageTimesOfEating = totalTimesOfEating / numberOfPhilos;
			System.out.println(("totalEatenTimes " + totalTimesOfEating + "\n"));
			System.out.println(("numberOrPhilos " + numberOfPhilos + "\n"));
			System.out.println(("The average times of eating is " + averageTimesOfEating + "\n"));

			// The table security catches all philos, who were eating more the
			// average philo plus the tolerance.
			for (AgentInterface agent : agentList) {
				try {
					agent.giveHelperAverargeTimesOfEating(averageTimesOfEating);
				} catch (RemoteException e) {
					e.printStackTrace();
					
				}
			}

			boolean allPhilosAreDone = true;

			// This for loop checks if all philos are done.
			// The table security will stop, if all philos are done
			// else he will keep going
			for (AgentInterface agent : agentList) {
				try {
					int numberOfPhilos = agent.getNumberOfPhilos();
					for (int i = 0; i < numberOfPhilos; i++) {
						allPhilosAreDone = allPhilosAreDone && agent.isPhiloDone(i);
					}
				} catch (RemoteException e) {
					e.printStackTrace();
				}
			}

			if (someoneGotCatched) {
				System.out.println(catchedMessage + "\n");
				this.someoneGotCatched = false;
			}

			if (allPhilosAreDone) {
				
				System.out.printf("TableSecurity finished \nStarttime: \t%03d \nEndtime: \t%03d\n", startTime,
						System.currentTimeMillis());
				
				for (AgentInterface agent : agentList) {
					try {
								for (int i = 0; i < agent.getNumberOfPhilos(); i++) {
									System.out.print("P"+agent.getPhiloID(i)+": "+ agent.getPhiloEatingCounter(i) + "   ");

								}
								System.out.println("\n\n");
						}
					 catch (RemoteException e) {
						e.printStackTrace();
						
					}
				}
				break;
			}
			synchronized (this) {
				
				if (this.shutDown) {
					try {
						
						shutDownAllHelpers();
						this.notify();//wakeup master to do his stuf and aknowledged we heard him.
						
						this.wait();// now wait for master to back notify us
						this.shutDown = false;
						wakeUpAllHelpers();
						
						this.notify();
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}
			
			totalTimesOfEating = 0;
		}
	}

	/**
	 * 
	 */
	private void shutDownAllHelpers() {
		for (AgentInterface agent : agentList) {
			try {
				agent.shutDownSecurityHelper();
			} catch (RemoteException e) {
				e.printStackTrace();
				
			}
		}
	}

	/**
	 * 
	 */
	private void wakeUpAllHelpers() {
		for (AgentInterface agent : agentList) {
			try {
				agent.wakeUpSecurityHelper();
			} catch (RemoteException e) {
				e.printStackTrace();
				
			}
		}
	}

	public void shutDown() {
		System.out.println("Master will set the shutdown boolean The boolean is " + this.shutDown);
		this.shutDown = true;
		System.out.println("Master set the shutdown boolean to true. The boolean is " + this.shutDown);
	}

	public void setUseless() throws RemoteException {
		for(AgentInterface agent: agentList){
			agent.killHelper();
		}
		this.useless = true;
		
	}

}
