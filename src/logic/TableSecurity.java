package logic;
import java.rmi.RemoteException;
import java.sql.Time;
import java.util.ArrayList;
import java.util.List;

import logic.AgentInterface;
import sun.management.resources.agent;


public class TableSecurity implements Runnable {


	private int numberOfPhilos;
	private int numberOfAgents;
	private double averageTimesOfEating;
	private int totalTimesOfEating;
	private List<AgentInterface> agentList;
	private String catchedMessage;
	private boolean someoneGotCatched;
	private boolean philoIsCatched;
	private boolean philoIsDone;
	private int eatingCounter;
	private int tolerance;
	long startTime;

	
	public TableSecurity(int numberOfPhilos, List<AgentInterface> agentList, int tolerance,long startTime) {
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
		this.startTime=startTime;
	}

	@Override
	public void run() {
		System.out.println("TableSecurity is running now. \n\n "
				+ "           --------------------ATTENTION!!!-------------------- \n\n  "
				+ "Everyone get catched, who eats "+ (tolerance+1) +" times more than the average philosopher.\n\n"
						+ "Legend:\n"
						+ "! = philosopher got catched \n"
						+ "# = philosopher is already catched \n"
						+ "+ = philosopher is done \n\n");
		while(true){
			
			//The table security collects all eatingCounter from the philos to calculate the average times of eating.
			for(AgentInterface agent: agentList){
				
				try {
					totalTimesOfEating += agent.getTotalTimesOfEating();
				} catch (RemoteException e) {
					e.printStackTrace();
				}
				
			}
			
			//The table security is calculating the average times of eating.
			averageTimesOfEating = totalTimesOfEating/numberOfPhilos;
			catchedMessage = ("The average times of eating is " + averageTimesOfEating + "\n");
			
			//The table security catches all philos, who were eating more the average philo plus the tolerance.
			for(AgentInterface agent: agentList){
				try {
					int numberOfPhilos = agent.getNumberOfPhilos();
					for(int i = 0; i < numberOfPhilos; i++){
						this.philoIsDone = agent.isPhiloDone(i);
						this.eatingCounter = agent.getPhiloEatingCounter(i);
						this.philoIsCatched = agent.isPhiloCatched(i);
						catchedMessage = catchedMessage + " " + eatingCounter;
						if(eatingCounter > averageTimesOfEating + tolerance && !philoIsCatched && !philoIsDone){
							catchedMessage = catchedMessage + "!";
							this.someoneGotCatched = true;
							agent.catchPhilo(i);
							System.out.println("Table Security catched Philo " + (agent.getFirstPhiloID() + i));
						}else{
							if(!philoIsDone){
								if(philoIsCatched){
								catchedMessage = catchedMessage + "#";
								this.philoIsCatched = false;
							}
							}else{
								catchedMessage = catchedMessage + "+";
								this.philoIsDone = false;
							}
						}
					}
				} catch (RemoteException e) {
					e.printStackTrace();
				}
			}
			
			boolean allPhilosAreDone = true;
			
			//This for loop checks if all philos are done. 
			//The table security will stop, if all philos are done
			//else he will keep going
			for(AgentInterface agent: agentList){
				try {
					int numberOfPhilos = agent.getNumberOfPhilos();
					for(int i = 0; i < numberOfPhilos; i++){
						allPhilosAreDone = allPhilosAreDone && agent.isPhiloDone(i) ;
					}
				} catch (RemoteException e) {
					e.printStackTrace();
				}
			}
			
			if(someoneGotCatched){
				System.out.println(catchedMessage  + "\n");
				this.someoneGotCatched = false;
			}
			
			if(allPhilosAreDone){
				System.out.printf("TableSecurity finished \nStarttime: \t%03d \nEndtime: \t%03d\n",startTime, System.currentTimeMillis());
				break;
			}
			
			
			totalTimesOfEating = 0;
		}
	}







}

