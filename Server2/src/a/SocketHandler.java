package a;

import java.net.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import model.Message;
import model.User;
import java.io.*;
import model.Room;
import model.RoomClient;

public class SocketHandler implements Runnable {
	private Socket socket;
	private ObjectOutputStream oos;
	private ObjectInputStream ois;
	private volatile boolean running = true;
	private User currentUser;
	private Room room;
	
	
	public User getCurrentUser() {
		int uid  = currentUser.getUid()   ;
		String username = currentUser.getUsername()  ;
		String avatar= currentUser.getAvatar() ;
		float score = currentUser.getScore() ;
		String date_create = currentUser.getDate_create() ;
		return new User(uid,username,avatar,score,date_create);
	}

	public SocketHandler(Socket socket) {
		this.socket = socket;
	}

	@Override
	public void run() {

		// TODO Auto-generated method stub

		try{
			oos = new ObjectOutputStream(socket.getOutputStream());
			ois = new ObjectInputStream(socket.getInputStream());
			
			while (running) {
				Object rcvObj = ois.readObject();
//				Thread.sleep(100);
				System.out.println(rcvObj.getClass().getName());
				// kiem tra doi tuong rcvObj instanceof User
				if (rcvObj instanceof Message) {
					Message rcvMsg = (Message) rcvObj; // cast ve Message
					String label = rcvMsg.getLabel();
					System.out.println("lbl:"+label);
					
					///////////////////////////////////
					switch (label) {
					case "LOGIN":
						currentUser = loginHandle(rcvMsg);
						
						break;
					case "REGISTER":
						registerHandle(rcvMsg);
						break;
					case "CREATE_ROOM":
						createRoom(rcvMsg);
						updateRoom();
						Server1.broadcastMessage(updateListRoom());
						break;
					case "GET_ROOM_LIST":
						sendRoomList(rcvMsg);
						break;
					case "JOIN_ROOM":
						joinRoom(rcvMsg);
						updateRoom();
						break;
					case "LEAVE_ROOM":
						leaveRoom(rcvMsg);
						updateRoom();
						Server1.broadcastMessage(updateListRoom());
						break;
					case "LOG_OUT":
						logOut(rcvMsg);
						break;
					case "START":
						start(rcvMsg);
						break;

					case "PING":
						sendPong();
						break;
					}
			////////////////////////////////////////
				System.out.println("now onl"+Server1.getUsers());
					
			/////////////////////////////////////////	
				
				
				
				} else {
					System.out.println("Doi tuong nhan duoc khac voi msg");
				}
			}

		} catch (Exception e) {
			System.out.println("Ket noi bi ngat dot ngot");
			e.printStackTrace();
		} finally {
			// Đảm bảo socket được đóng
			try {
				
				if (room != null) {
					leaveRoom(new Message("holder message",null));
				}
				Server1.removeClient(this);
				if (this.currentUser != null) {
					Server1.removeUser(currentUser);
					currentUser = null;
				}
				Server1.broadcastMessage(updateListRoom());
				oos.close();
				ois.close();
				if (socket != null && !socket.isClosed()) {
					socket.close();
				}
				
				
			} catch (IOException e) {
				System.err.println("Lỗi khi đóng socket: " + e.getMessage());
			}
		}

	}
	private void registerHandle (Message msg) throws IOException {
		User rcvUser = (User) msg.getData();
		USERDAO userDao = new USERDAO();
		String status = userDao.registerUser(rcvUser);
		if (status == "REGISTER_TRUE") {
			System.out.println("dang ky thanh cong  " + rcvUser.getUsername());
			sendMessage(new Message("REGISTER_TRUE","OK"));
		} if (status == "DUPLICATE_USER_REGISTER_FALSE") {
			System.out.println("dang ky that bai,user da ton tai!!!  " + rcvUser.getUsername());
			sendMessage(new Message("DUPLICATE_USER_REGISTER_FALSE","NOT_OK"));
		}
		else {
			System.out.println("dang ky that bai,khong ro nguyen nhan  " + rcvUser.getUsername());
			sendMessage(new Message("REGISTER_FALSE","NOT_OK"));
		}
		System.out.println("Gui phan hoi");
	}

	private User loginHandle (Message msg) throws IOException {
		User rcvUser = (User) msg.getData();
		
		if(!Server1.getUsers().contains(rcvUser)) {
			USERDAO userDao = new USERDAO();
			User user = userDao.Login(rcvUser);
			if (user != null) {
				System.out.println("Login thanh cong  " + rcvUser.getUsername());
				
				Server1.addUser(user);//them vao danh sach user dang onl cua server
				
				sendMessage(new Message("LOGIN_TRUE",user));
				System.out.println("Gui phan hoi");
				return user;
			} else {
				System.out.println("Login that bai  " + rcvUser.getUsername());
				sendMessage(new Message("LOGIN_FALSE",user));
				System.out.println("Gui phan hoi");
				return null;
			}
		}else {
			System.out.println("Tai khoan da dang nhap  " + rcvUser.getUsername());
			sendMessage(new Message("ALREADY_LOGIN",null));
			System.out.println("Gui phan hoi");
			return null;
		}
		
		


	}
	
	private void createRoom (Message msg) throws IOException {
		Map <String, Integer> newRoomInfor = (HashMap<String, Integer>)msg.getData();
    	int numPeople = newRoomInfor.get("numPeople");
    	int numSeed = newRoomInfor.get("numSeed");
    	int numType = newRoomInfor.get("numTypeSeed");
		System.out.println(numPeople+" "+numSeed+" "+numType);
		this.room = RoomManager.getInstance() .createRoom("test",(int)System.currentTimeMillis()%1000,numPeople, numSeed, numType);
		room.addParticiant(this);
		System.out.println("OK"+ room.getName());
		System.out.println(RoomManager.getInstance().getActiveRooms());
		Message resMsg = new Message("CREATE_ROOM_OK",Integer.valueOf(room.getID()));
		sendMessage(resMsg);
	}
	
	
	public void sendRoomList(Message rcvMsg) {
		System.out.println("get room list request!!!");
		List<RoomClient> listRoom = RoomManager.getInstance().getActiveRoomClients();
//		List<String> listIDRoom = new ArrayList<>();
//		for (Room r : listRoom) {
//			listIDRoom.add(String.valueOf(r.getID()));
//		}
		try {
			sendMessage(new Message("LIST_ID_ROOM",listRoom));
			System.out.println("gui list room xong!!!");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	private void joinRoom(Message msg) {
		RoomClient roomClient = (RoomClient)msg.getData();
		this.room = RoomManager.getInstance().getRoomByRoomClient(roomClient);
		room.addParticiant(this);
		List<User> users = new ArrayList<>(room.getUsers());
//		List<User> users = new ArrayList<>();
		try {
			for (User u : users)
			System.out.println("send"+u);
//			for(SocketHandler sh : room.getParticipants()) {
//			sh.sendMessage(new Message("JOIN_ROOM_ACCEPT",users));
//			}
			this.sendMessage(new Message("JOIN_ROOM_ACCEPT",users));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	private void leaveRoom(Message msg) {
		if (room!=null) {
		this.room.removeParticiant(this);
		}
	}
	private Message updateListRoom() {
		System.out.println("update room list because comeone leave or create");
		List<RoomClient> listRoom = RoomManager.getInstance().getActiveRoomClients();
//		List<String> listIDRoom = new ArrayList<>();
//		for (Room r : listRoom) {
//			listIDRoom.add(String.valueOf(r.getID()));
//		}
			return new Message("UPDATE_LIST_ROOM",listRoom);
	}
	private void updateRoom() {// co nguoi thoa ra
	
		try {
			List<User> users = new ArrayList<User>(room.getUsers());
			System.out.println("send updateRoom to"+users);
			for(SocketHandler sh : room.getParticipants()) {
			sh.sendMessage(new Message("UPDATE_ROOM",users));
			}

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	private void logOut(Message msg) {
		if (currentUser != null) {
			Server1.removeUser(currentUser);
			currentUser = null;
		}
		try {
			sendMessage(new Message("LOG_OUT_OK", null));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	private void start(Message msg) {
		if(room.gameStart(this)) {
			System.out.println("send start msg to all handler in room");
			for (SocketHandler sh: room.getParticipants()) {
				try {
					sh.sendMessage(new Message("START_GAME_OK",new RoomClient(room.getName(),room.getID(),room.getNumPeople(),room.getNumSeed(),room.getNumType())));
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		};
	}
	
	
	
	
	
	public synchronized void sendMessage(Message message) throws IOException {
		oos.writeObject(message);
		oos.flush();
	}
	
	
	private void sendPong() throws IOException {
			System.out.println("get PING!!!");
			sendMessage(new Message("PONG","B"));
			System.out.println("send Pong!!!");
	}
	private void stop() {
		running = false;
		leaveRoom(null);
	}
}
