/*
 * Program that simulates a type of call center.  When a call comes in, a responder takes it.  With a given probability, they
 * send the call to a manager.  With another probability, the manager sends the call to a director.  There are 2 respondents, 
 * 1 manager, and 1 director.  Note that I implement a queue using an ArrayList.  Also note that the main
 * point of this exercise is to work with semaphores/ threads, so I don't have a class for a call or anything like that (yet anyways).
 * 
 */

import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Semaphore;

public class CallHandler extends Thread {
	
	static enum Rank {Respondant, Manager, Director}
	
	private static CallHandler instance;
	
	public static CallHandler getInstance(){
		if(instance == null){instance = new CallHandler();}
		return instance;
	}
	
	private static Semaphore responderThread;
	private static Semaphore managerThread;
	private static Semaphore directorThread;
	
	static ArrayList<Employee> responderQ;
	static ArrayList<Employee> managerQ;
	static ArrayList<Employee> directorQ;
	
	public CallHandler(){
		responderQ = new ArrayList<Employee>();
		managerQ = new ArrayList<Employee>();
		directorQ = new ArrayList<Employee>();
		responderQ.add(new Respondant()); responderQ.add(new Respondant());
		managerQ.add(new Manager());
		directorQ.add(new Director());
		
		responderThread = new Semaphore(responderQ.size());
		managerThread = new Semaphore(managerQ.size());
		directorThread = new Semaphore(directorQ.size());
	}
	
	public void takeCall(){
		try {
			responderThread.acquire();
			Employee temp = responderQ.get(0);
			responderQ.remove(0);
			responderQ.add(temp);
			responderQ.get(responderQ.size()-1).takeCall();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		
	}
	
	public void callComplete(Employee e){
		if(e.jobTitle == Rank.Respondant){
			for(int i = 0 ; i < responderQ.size(); i++){
				if(responderQ.get(i).equals(e)){responderQ.remove(i); break;}
			}
			for(int i = 0 ; i < responderQ.size(); i++){
				if(responderQ.get(i).isBusyWithCall()){responderQ.add(i,e); break;}
			}
			if(responderQ.size() == 0){responderQ.add(e);}
			responderThread.release();
		}
		else if(e.jobTitle == Rank.Manager){
			for(int i = 0 ; i < managerQ.size(); i++){
				if(managerQ.get(i).equals(e)){managerQ.remove(i); break;}
			}
			for(int i = 0 ; i < managerQ.size(); i++){
				if(managerQ.get(i).isBusyWithCall()){managerQ.add(i,e); break;}
			}
			if(managerQ.size() == 0){managerQ.add(e);}
			managerThread.release();
		}
		else if(e.jobTitle == Rank.Director){
			for(int i = 0 ; i < directorQ.size(); i++){
				if(directorQ.get(i).equals(e)){directorQ.remove(i); break;}
			}
			for(int i = 0 ; i < directorQ.size(); i++){
				if(directorQ.get(i).isBusyWithCall()){directorQ.add(i,e); break;}
			}
			if(directorQ.size() == 0){directorQ.add(e);}
			directorThread.release();
		}
	}
	
	public void assignEscalatedCall(Employee em){
		if(em.jobTitle == Rank.Respondant){
			try {
				em.endCall();
				managerThread.acquire();
				Employee temp = managerQ.get(0);
				managerQ.remove(0);
				managerQ.add(temp);
				managerQ.get(managerQ.size()-1).takeCall();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		
		else if(em.jobTitle == Rank.Manager){
			try {
				em.endCall();
				directorThread.acquire();
				Employee temp = directorQ.get(0);
				directorQ.remove(0);
				directorQ.add(temp);
				directorQ.get(directorQ.size()-1).takeCall();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}

	
	abstract class Employee extends Thread{
		Rank jobTitle;
		protected boolean busyWithCall;
		protected boolean escalatedCall;

		abstract void escalateCall();
		
		abstract void takeCall();
		abstract void endCall();
		public boolean isBusyWithCall(){return busyWithCall;}
	}
	
	class Respondant extends Employee{
		public Respondant(){this.jobTitle = Rank.Respondant; this.busyWithCall = false; this.escalatedCall = false;}
		
		public void takeCall(){
			this.busyWithCall = true;
			System.out.println("Responder "+ this +" taking call");
			try {
				Thread.sleep(4000);
				if(Math.random() < 0.5){
					 this.escalatedCall = true; this.escalateCall();
				}
				else{
					this.endCall();
				}
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		
		public void endCall(){
			this.busyWithCall = false;
			System.out.println("Responder " + this + " finished call");
			CallHandler.getInstance().callComplete(this);
		}

		public void escalateCall(){
			System.out.println("Responder " + this + "  escalating a call");
			CallHandler.getInstance().assignEscalatedCall(this);
		}
	}
	class Manager extends Employee{
		public Manager(){this.jobTitle = Rank.Manager; this.busyWithCall = false; this.escalatedCall = false;}
		
		public void takeCall(){
			this.busyWithCall = true;
			System.out.println("Manager " + this + "  taking call");
			try {
				Thread.sleep(4000);
				if(Math.random() < 0.5){
					 this.escalatedCall = true; this.escalateCall();
				}
				else{
					this.endCall();
				}
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		
		public void endCall(){
			this.busyWithCall = false;
			System.out.println("Manager " + this + " finished call");
			CallHandler.getInstance().callComplete(this);
		}
		
		public void escalateCall(){
			System.out.println("Manager escalated a call");
			CallHandler.getInstance().assignEscalatedCall(this);
		}
	}
	class Director extends Employee{
		public Director(){this.jobTitle = Rank.Director; this.busyWithCall = false;}
		
		public void takeCall(){
			this.busyWithCall = true;
			System.out.println("Director taking call");
			try {
				Thread.sleep(6000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			finally{this.endCall();}
		}
		
		public void endCall(){
			this.busyWithCall = false;
			System.out.println("Director finished call");
			CallHandler.getInstance().callComplete(this);
		}
		
		public void escalateCall(){}
	}
	
	public static void main(String[] args) {
		
		ExecutorService executor = Executors.newCachedThreadPool();
		
		for(int i = 0 ; i < 4; i++){
			try {
				executor.submit(new Runnable() {
				public void run() {
						CallHandler.getInstance().takeCall();
				}
			});
				Thread.sleep(2000);
			}
			catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		
		executor.shutdown();
		
	}
	
}
