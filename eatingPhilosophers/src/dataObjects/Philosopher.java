package dataObjects;
public class Philosopher implements Runnable {
	
	public enum STATE{MEDITATING(0), EATING(1), SLEEPING(2), WAITING_PLACE(3), WAITING_FORK(4);

        private final int value;

        STATE(final int newValue) {
            value = newValue;
        }
	}
	private final int philoID;
	STATE state;
	int eatingCounter = 0;
	boolean hungry = false;
	int seatID;
	/*Place place
	Fork leftFork;
	Fork rightFork;*/
	
	public Philosopher(int philoID){
		super();
		this.philoID=philoID;
		this.seatID= -1;
		state = STATE.SLEEPING;//new STATE((int)Math.random() % 5);
	}

	@Override
	public void run() {
		System.out.println("Iam Philosopher"+this.philoID);
		
		//TODO for loop
		
	}

	@Override
	public String toString() {
		return "Philosopher[" + philoID + "]";
	}
	
}
