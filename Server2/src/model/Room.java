package model;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import a.RoomManager;
import a.SocketHandler;

public class Room extends RoomClient {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private List<SocketHandler> participants;
	private boolean gameStarted = false;

	public Room(String name, int iD, int numPeople, int numSeed, int numType) {
		super(name,iD,numPeople,numSeed,numType);
		this.participants = new ArrayList<SocketHandler>();
	}
	
	public synchronized void addParticiant (SocketHandler handler) {
		participants.add(handler);
		addUser(handler.getCurrentUser());
//		System.out.println("flagadd"+handler.getCurrentUser());
	}
	public synchronized void removeParticiant (SocketHandler handler) {
		System.out.println("FLAGTEST"+this.getUsers());
		System.out.println(handler.getCurrentUser());
		System.out.println(users.remove(handler.getCurrentUser()));
		System.out.println("FLAGTEST"+this.getUsers());
		participants.remove(handler);
		if(participants.isEmpty()) {
			RoomManager.getInstance().removeRoom(this);
		}
	}
	public synchronized List<SocketHandler> getParticipants(){
		return participants;
	}
	public synchronized boolean gameStart(SocketHandler handler) {
		if (!gameStarted) {
			gameStarted = true;
			System.out.println(handler.getCurrentUser() + " start the game!!!");
			return true;
		}else {
			System.out.println("Tu choi, game da bat dau roi.");
			return false;
		}
	}
	

}
