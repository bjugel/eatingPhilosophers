package dataObjects;

import java.rmi.RemoteException;
import java.sql.Time;

import logic.AgentInterface;

public class Philosopher implements Runnable {

	public enum STATE {
		MEDITATING(0), EATING(1), SLEEPING(2), WAITING_PLACE(3), WAITING_FORK(4);

		private final int value;

		STATE(final int newValue) {
			value = newValue;
		}
	}

	private final int philoID;
	STATE state;
	int eatingCounter;
	boolean hungry = false;
	int seatID;
	AgentInterface agent;
	/*
	 * Place place Fork leftFork; Fork rightFork;
	 */

	public Philosopher(int philoID, AgentInterface yourAgent) {
		super();
		this.philoID = philoID;
		this.seatID = -1;
		this.eatingCounter=0;
		state = STATE.SLEEPING;// new STATE((int)Math.random() % 5);
		this.agent = yourAgent;
	}

	@Override
	public void run() {
		try {
			System.out.printf("Iam Philosopher%03d with Agent Agent%03d\n", this.philoID, agent.getAgentID());
			for (int i = 0; i < 10; i++) {
				eat();
				
			}
			System.out.printf("x %03d finished \n",this.philoID);
			

		} catch (RemoteException e) {
			e.printStackTrace();
		} catch (InterruptedException e) {
			e.printStackTrace();
		} catch (Exception e) {

			e.printStackTrace();
		}

		// TODO for loop

	}

	/**
	 * @throws RemoteException
	 * @throws InterruptedException
	 * @throws Exception
	 */
	private void eat() throws RemoteException, InterruptedException, Exception {
		this.seatID = agent.giveSeat(agent.getAgentID());
		if (this.seatID == -1) {
			//System.out.printf("Philosopher didnt find a Seat. PhiloID: \t%03d \n", this.philoID);
			this.eat();
		} else {
			System.out.printf("Philosopher sitting down at Seat%03d\tPhiloID:\t%03d for the \t%03d time \n", this.seatID, this.philoID,this.eatingCounter+1);
			this.agent.giveForks(seatID,philoID);

			Thread.sleep(10);
			System.out.printf("Philosopher standing up from Seat%03d\tPhiloID:\t%03d\n", this.seatID, this.philoID);
			this.eatingCounter+=1;
			this.agent.releaseForks(seatID,this.philoID);
			this.agent.standUp(seatID);
		}
	}

	@Override
	public String toString() {
		return "Philosopher[" + philoID + "]";
	}

}
