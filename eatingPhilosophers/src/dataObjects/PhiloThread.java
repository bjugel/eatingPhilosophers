package dataObjects;
import java.util.ArrayList;

public class PhiloThread implements Runnable{
	
	ArrayList<Philosopher> philoList;
	int number = -1;
	
	public PhiloThread(){
		super();
	}
	
	public PhiloThread(int numberOfPhilo, ArrayList<Philosopher> list){
		super();
		number = numberOfPhilo;
		philoList = list;
	}

	public void run(){
//		try{
//			philoList.add(new Philosopher());
//			System.out.println(this.sayHello());
//		} catch(Exception ex){
//			ex.printStackTrace();
//		}
	}
	
	public String sayHello(){
		return "Hello world!";
	}


}