package logic;

import java.rmi.RemoteException;
import java.util.concurrent.atomic.AtomicInteger;

public class SecurityHelper implements Runnable {

	AtomicInteger localTotalPhiloEatCount;
	AtomicInteger averageEatenTimes;
	boolean shutDown;
	boolean finished;
	boolean allPhilosAreDone;
	boolean shutdownAknowledged;
	AgentInterface agent;
	int tolerance;

	public SecurityHelper(AgentInterface agent, int tolerance) {
		this.allPhilosAreDone = false;
		this.localTotalPhiloEatCount = new AtomicInteger(0);
		this.averageEatenTimes = new AtomicInteger(0);
		this.agent = agent;
		this.tolerance = tolerance;
		this.shutdownAknowledged=false;
		this.finished = false;
	}

	@Override
	public void run() {
		System.out.println("security helper is up and running");
		// TODO Auto-generated method stub
		while (!allPhilosAreDone && !finished) {
			this.allPhilosAreDone=true;
			System.out.println("in loop");
			try {
				// add up all philos eating counters
				this.localTotalPhiloEatCount.getAndSet(agent.calculateTotalTimesOfEating());
				System.out.println("this is the local eating time" + this.localTotalPhiloEatCount);
				// punish all philosLocally
				for (int i = 0; i < agent.getNumberOfPhilos(); i++) {
					this.allPhilosAreDone=(allPhilosAreDone&&agent.isPhiloDone(i));
					if (averageEatenTimes.get() + tolerance < agent.getPhiloEatingCounter(i)) {
						agent.catchPhilo(i);
						
					}
				}
			} catch (RemoteException e) {
				e.printStackTrace();
			}
			//TODONOW change the sinchronization method to work similar to the philosinchronization when we add or remove philos 
			// from the idea the new table security should call shutdown and wakeup methods on the agent who busy waits untill the thing is really shut down. 
			synchronized (this) {

				if(shutDown){
					this.shutdownAknowledged=true;
					try {
						this.wait();
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
					this.shutdownAknowledged=false;
					this.shutDown=false;
			
			}}
			System.out.println("philodone: "+allPhilosAreDone +" finished: " +finished);
		}
		this.finished=true;
	}

	public boolean isShutDownAknowledged() {
		return shutdownAknowledged;
	}
	public void setShutDown(boolean shutDown) {
		this.shutDown = shutDown;
	}
	public boolean isFinished(){
		return this.finished;
	}
	public int getLocalTotalPhiloEatCounter(){
		return this.localTotalPhiloEatCount.get();
	}
	public void setAverageEatenTimes(int avarage){
		this.averageEatenTimes.getAndSet(avarage);
	}
}
