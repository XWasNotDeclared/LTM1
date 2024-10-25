package model;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;


public class RoomClient implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String name;
	private int ID;
	private int numPeople;
	private int numSeed;
	private int numType;
	private List<User> users;
	

	public RoomClient(String name, int iD, int numPeople, int numSeed, int numType) {
		super();
		this.name = name;
		ID = iD;
		this.numPeople = numPeople;
		this.numSeed = numSeed;
		this.numType = numType;
		this.users = new ArrayList<>();
	}
	public RoomClient(String name, int iD, int numPeople, int numSeed, int numType, List<User> users) {
		super();
		this.name = name;
		ID = iD;
		this.numPeople = numPeople;
		this.numSeed = numSeed;
		this.numType = numType;
		this.users = users;
	}
	
	public void addUser(User user) {
		users.add(user);
	}
	
	public void removeUser(User user) {
		users.remove(user);
	}
	
	
	public List<User> getUsers() {
		return users;
	}

	public void setUsers(List<User> users) {
		this.users = users;
	}

	public int getID() {
		return ID;
	}






	public String getName() {
		return name;
	}



	public void setName(String name) {
		this.name = name;
	}



	public int getNumPeople() {
		return numPeople;
	}



	public void setNumPeople(int numPeople) {
		this.numPeople = numPeople;
	}



	public int getNumSeed() {
		return numSeed;
	}



	public void setNumSeed(int numSeed) {
		this.numSeed = numSeed;
	}



	public int getNumType() {
		return numType;
	}



	public void setNumType(int numType) {
		this.numType = numType;
	}

	@Override
	public String toString() {
		return "RoomClient [name=" + name + ", ID=" + ID + ", numPeople=" + numPeople + ", numSeed=" + numSeed
				+ ", numType=" + numType + ", users=" + users + "]";
	}





	

}
