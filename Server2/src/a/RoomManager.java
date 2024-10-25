package a;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import model.Room;
import model.RoomClient;
import model.User;

public class RoomManager {
	private static RoomManager instance;
	private List<Room> rooms;
	
	
	
	
	
	public RoomManager() {
		super();
		this.rooms = new ArrayList<>();
	}

	public static synchronized  RoomManager getInstance() {
		if (instance == null) {
			instance = new RoomManager();
		}
		return instance;
	}
	
	public synchronized Room createRoom (String name, int ID, int numPeople, int numSeed, int numType) {
		Room room = new Room(name,ID, numPeople, numSeed, numType);
		rooms.add(room);
		return room;
	}
	
	public synchronized void removeRoom (Room room) {
		rooms.remove(room);
	}
	public synchronized List<Room> getActiveRooms(){
		return new ArrayList<>(rooms);// tra ve ban sao danh sach phong
	}
	
    // Phương thức chuyển đổi Set<Room> sang List<RoomClient>
    public List<RoomClient> getActiveRoomClients() {
        return rooms.stream()
            .map(room -> new RoomClient(
                room.getName(),
                room.getID(),
                room.getNumPeople(),
                room.getNumSeed(),
                room.getNumType(),
                room.getUsers()))
        
            .collect(Collectors.toList());
    }
    
    public synchronized Room getRoomByRoomClient(RoomClient roomClient) {
    	 return rooms.stream()
    		        .filter(room -> room.getID() == roomClient.getID())
    		        .findFirst()
    		        .orElse(null); // Trả về null nếu không tìm thấy
    }
	
	
	

}
