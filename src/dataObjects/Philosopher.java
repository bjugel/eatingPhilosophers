package dataObjects;

import java.rmi.RemoteException;
import java.sql.Time;

import logic.AgentInterface;

public class Philosopher implements Runnable {

	public enum STATE {
		MEDITATING(0), EATING(1), SLEEPING(2), LOOKING_FOR_SEAT(3), PUNISHED(4);//CB changed the states

		private final int value;

		STATE(final int newValue) {
			value = newValue;
		}
	}

	private final int philoID;
	private AgentInterface agent;
	private boolean isHungry;
	private STATE state;
	private int seatID;
	private int eatingCounter;
	private boolean isCatched;//CB
	private boolean isFinish;//CB
	private int seatSearchFails;
	private boolean shutDown;
	private boolean shutDownAknowledged;
	private long endTime;

	public Philosopher(int philoID, AgentInterface yourAgent) {
		super();
		this.philoID = philoID;
		this.seatID = -1;
		this.eatingCounter=0;
		this.state = STATE.LOOKING_FOR_SEAT;// new STATE((int)Math.random() % 4);
		this.agent = yourAgent;
		this.isCatched = false;//CB
		this.isFinish = false;//CB
		this.isHungry = false;//CB
		this.shutDown=false;
		this.shutDownAknowledged=false;
		endTime=-1;
	}

	
	@Override
	public void run() {
		try {
			//System.out.printf("Iam Philosopher%03d with Agent Agent%03d\n", this.philoID, agent.getAgentID());
			while(System.currentTimeMillis()<endTime) {
				
				if(isCatched){
					getPunishment();
				}
				
				//eat one time for 100 percent
				boolean hasEaten;
				seatSearchFails=-1;
				do{
					seatSearchFails++;
					hasEaten=eat();
					if (seatSearchFails > 100){
						Thread.sleep(10);
					}
				}while(!hasEaten);
				
				
				if(!isHungry)
				{
					meditate();
					
					if(eatingCounter%3 == 0){
						goSleep();
					}
				}
				synchronized (this) {
					if(shutDown){
						this.shutDownAknowledged=true;
						this.wait();
						this.shutDownAknowledged=false;
						this.shutDown=false;
				}
				
					
				}
			}
			
			isFinish = true;
			
			System.out.printf("x %03d finished \n",this.philoID);
		} catch (RemoteException e) {
			e.printStackTrace();
		} catch (InterruptedException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}



	/**
	 * @throws RemoteException
	 * @throws InterruptedException
	 * @throws Exception
	 */
	private boolean eat() throws RemoteException, InterruptedException, Exception {
		boolean hasEaten=false;
		this.seatID = agent.giveSeat(agent.getAgentID());
		if (this.seatID == -1) {
			//System.out.printf("Philosopher didnt find a Seat. PhiloID: \t%03d \n", this.philoID);
			//this.eat();
		} else {
			System.out.printf("Philosopher sitting down at Seat%03d\tPhiloID:\t%03d for the \t%03d time \n", this.seatID, this.philoID,this.eatingCounter+1);
			this.agent.giveForks(seatID,philoID);
			
			this.state = STATE.EATING;//CB set the state
			Thread.sleep(1);//CB changed to 1ms
			
			//System.out.printf("Philosopher standing up from Seat%03d\tPhiloID:\t%03d\n", this.seatID, this.philoID);
			this.eatingCounter++;
			hasEaten=true;
			this.agent.releaseForks(seatID,this.philoID);
			this.agent.standUp(seatID);
		}
		return hasEaten;
	}
	
	//CB
	private void goSleep() throws InterruptedException {
		this.state = STATE.SLEEPING;
		Thread.sleep(10);
		this.state = STATE.LOOKING_FOR_SEAT;
	}

	//CB
	private void meditate() throws InterruptedException {
		this.state = STATE.MEDITATING;
		Thread.sleep(5);
		this.state = STATE.LOOKING_FOR_SEAT;
	}
	
	//CB
	public int getEatingCounter(){
		return this.eatingCounter;
	}
	
	//CB
	public void getCatched(){
		this.isCatched = true;
		//System.out.println("Philo " + philoID + " get catched." );
	}
	
	//CB
	private void getPunishment() throws InterruptedException{
		this.state = STATE.PUNISHED;
		//System.out.println("Philo " + philoID + " get punished." );
		Thread.sleep(2);
		this.isCatched = false;
		this.state = STATE.LOOKING_FOR_SEAT;
	}
	
	//CB
	public boolean isPhiloDone(){
		return this.isFinish;
	}
	
	//CB
	public void setPhiloHungry(){
		this.isHungry = true;
		System.out.println("Philo " + philoID + " is hungry." );
	}
	
	//CB
	public boolean isPhiloCatched(){
		return this.isCatched;
	}

	public boolean isShutDownAknowledged() {
		return shutDownAknowledged;
	}

	public void setShutDown(boolean shutDown) {
		this.shutDown = shutDown;
	}


	public void setEndTime(long endTime) {
		this.endTime=endTime;
		
	}


}
