package logic;

import java.net.MalformedURLException;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.List;

public class CrashSecurity implements Runnable {

	private List<Integer> philosEatingCounters;
	private List<Integer> philosEatingCountersBackUp;
	private List<AgentInterface> agentList;
	private Master2 master;
	private int numberOfSeats;
	private int numberOfSeatsBackUp;
	private long endTime;

	public CrashSecurity(List<AgentInterface> agentList, long endTime, Master2 master) {
		this.master = master;
		this.agentList = agentList;
		this.endTime = endTime;
		this.numberOfSeats = 0;
		this.philosEatingCounters = new ArrayList<Integer>();
		this.philosEatingCountersBackUp = new ArrayList<Integer>();
	}

	@Override
	public void run() {
		System.out.println("CrashSecurity is running now");
		while (System.currentTimeMillis() <= endTime) {

			// backup all data and clear the current data structures to fill
			// them with new data
			philosEatingCountersBackUp.clear();
			for (int eatingCounter : philosEatingCounters) {
				philosEatingCountersBackUp.add(eatingCounter);
			}
			philosEatingCounters.clear();
			numberOfSeatsBackUp = numberOfSeats;
			numberOfSeats = 0;

			for (AgentInterface agent : agentList) {
				try {
					numberOfSeats += agent.getNumberOfSeats();
					for (int i = 0; i < agent.getNumberOfPhilos(); i++) {
						philosEatingCounters.add(agent.getPhiloEatingCounter(i));
					}
				} catch (RemoteException e) {
					e.printStackTrace();
					System.out.println("System Crashed now reinitializing it with:");
					System.out.println("Philos:");
					for (int eatingCounter : philosEatingCountersBackUp) {
						System.out.print(eatingCounter + " ");
					}
					System.out.println("\n number of Current Seats: " + numberOfSeatsBackUp);
					try {
						handleCrash();
					} catch (MalformedURLException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					} catch (RemoteException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					} catch (NotBoundException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}

				}
			}

		}
	}

	private void handleCrash() throws MalformedURLException, RemoteException, NotBoundException {

		killPhilos();
		master.reinitializeEnvironment(this.numberOfSeatsBackUp,this.philosEatingCountersBackUp);
		

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
