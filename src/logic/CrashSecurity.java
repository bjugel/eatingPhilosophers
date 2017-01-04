package logic;

import java.net.MalformedURLException;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.List;

public class CrashSecurity implements Runnable {

	private List<Integer> philosEatingCounters;
	private List<Integer> philosEatingCountersBackUp;
	private List<Integer> philoIDs;
	private List<Integer> philoIDsBackUp;
	private List<AgentInterface> agentList;
	private Master2 master;
	private int numberOfSeats;
	private int numberOfSeatsBackUp;
	private long endTime;
	boolean useless;

	public CrashSecurity(List<AgentInterface> agentList, long endTime, Master2 master) {
		this.master = master;
		this.agentList = agentList;
		this.endTime = endTime;
		this.numberOfSeats = 0;
		this.philosEatingCounters = new ArrayList<Integer>();
		this.philosEatingCountersBackUp = new ArrayList<Integer>();
		this.philoIDs = new ArrayList<Integer>();
		this.philoIDsBackUp = new ArrayList<Integer>();
		this.useless=false;
	}

	@Override
	public void run() {
		System.out.println("CrashSecurity is running now");
		outerloop:
		while (System.currentTimeMillis() <= endTime && !this.useless) {

			// backup all data and clear the current data structures to fill
			// them with new data
			philosEatingCountersBackUp.clear();
			
			for (int eatingCounter : philosEatingCounters) {
				philosEatingCountersBackUp.add(eatingCounter);
			}
			philoIDsBackUp.clear();
			for (int philoID : philoIDs) {
				philoIDsBackUp.add(philoID);
			}
			
			philosEatingCounters.clear();
			philoIDs.clear();
			
			numberOfSeatsBackUp = numberOfSeats;
			numberOfSeats = 0;

			for (AgentInterface agent : agentList) {
				try {
					numberOfSeats += agent.getNumberOfSeats();
					for (int i = 0; i < agent.getNumberOfPhilos(); i++) {
						philosEatingCounters.add(agent.getPhiloEatingCounter(i));
						philoIDs.add(agent.getPhiloID(i));
					}
				} catch (RemoteException e) {
					e.printStackTrace();
					System.out.println("System Crashed now reinitializing it with:");
					System.out.println("Philos:");
					for (int philoID : philoIDsBackUp) {
						System.out.print(philoID + " ");
					}
					System.out.println();
					for (int eatingCounter : philosEatingCountersBackUp) {
						System.out.print(eatingCounter + " ");
					}
					System.out.println("\n number of Current Seats: " + numberOfSeatsBackUp);
					try {
						handleCrash();
						break;
					} catch (MalformedURLException e1) {
						
						e1.printStackTrace();
					} catch (RemoteException e1) {
						
						e1.printStackTrace();
					} catch (NotBoundException e1) {
						
						e1.printStackTrace();
					}

				}
			}

		}
		System.out.println("Crash Security terminated.");
	}

	private void handleCrash() throws MalformedURLException, RemoteException, NotBoundException {

		master.killTableSecurity();
		killPhilos();
		//kill table security
		master.reinitializeEnvironment(this.numberOfSeatsBackUp,this.philoIDsBackUp,this.philosEatingCountersBackUp);
		//last statement set useless to true so this instance of chrash security ends itself. 
		this.useless=true;
		System.out.println("The useless boolean is now "+this.useless);
		
		
		//set boolean so that crash security ends itselfe.
	}
	/**
	 * kills all philos in the remaining agents so they dont run anymore.
	 */
	private void killPhilos() {
		for (AgentInterface a : agentList) {
			try {
				a.killAllPhilos();
			} catch (RemoteException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

}
