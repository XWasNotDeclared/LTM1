package application;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import javafx.application.Platform;
import javafx.stage.Stage;
import model.Message;
import model.RoomClient;
import model.User;

public class Communication {
	private ObjectInputStream ois;
	private ObjectOutputStream oos;
	private Socket socket;
	private volatile boolean running = true;
	private Navigation navigation;
	private User currentUser;

	private final BlockingQueue<Message> messageQueue = new LinkedBlockingQueue<Message>();

	public Communication(String ip, int port) throws IOException, ClassNotFoundException {
		this.socket = connectToServer(ip, port);
		this.oos = new ObjectOutputStream(socket.getOutputStream());
		sendMessage(new Message("PING", "a"));
		this.ois = new ObjectInputStream(socket.getInputStream());
		ois.readObject();
	}

	public void startReceiving() {
		new Thread(() -> {
			try {
				while (running) {
					System.out.println("flag1");
					Message message = receiveMessage();
					messageQueue.offer(message);
				}
			} catch (ClassNotFoundException | IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}).start();
	}

	public void startProcessing() {
		new Thread(() -> {
			while (running) {
				try {
					Message message = messageQueue.take();
					processMessage(message);
				} catch (InterruptedException e) {
					e.printStackTrace();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}).start();
	}

	private  void processMessage(Message message) throws IOException {
		System.out.println(message.getLabel());
		switch (message.getLabel()) {
		case "LOGIN_TRUE":
			currentUser = (User) message.getData();
			System.out.println(currentUser);
			Platform.runLater(() -> {
				HomeController homeController = navigation.switchTo("Home.fxml");
				homeController.updateUserInfor(currentUser.getUsername(), currentUser.getScore());
				homeController.setCommu(this);
			});
			break;
		case "LOGIN_FALSE":
			Platform.runLater(() -> {
				Util.showError("Đăng nhập thất bại, sai tên hoặc mật khẩu!");
			});
			break;
		case "ALREADY_LOGIN":
			Platform.runLater(() -> {
				Util.showError("Đăng nhập thất bại, Tai khoan nay da dăng nhap");
			});
			break;
		case "REGISTER_FALSE":
			Platform.runLater(() -> {
				Util.showError("Đăng ký thất bại");
			});
			break;
		case "REGISTER_TRUE":
			Platform.runLater(() -> {
				Util.showError("Đăng ký thành công, hãy quay lại đăng nhập");
			});
			break;
		case "DUPLICATE_USER_REGISTER_FALSE":
			Platform.runLater(() -> {
				Util.showError("Đăng ký thất bại, username đã tồn tại");
			});
			break;
		case "CREATE_ROOM_OK":
			Platform.runLater(() -> {
				Util.showError("tạo thành công, id room: " + String.valueOf(message.getData()));
				RoomController roomController = navigation.switchTo("Room.fxml");
				roomController.setUserName1(currentUser.getUsername());
				roomController.setCommu(this);
			});
			break;

		case "LIST_ID_ROOM":
			List<RoomClient> listRoom = (ArrayList<RoomClient>) message.getData();
			System.out.println(listRoom);
			Platform.runLater(() -> {
				FindRoomController findRoomController = this.getNavigation().getController("FindRoom.fxml");
				findRoomController.setRoomList(listRoom);
			});
			break;

		case "JOIN_ROOM_ACCEPT":
			List<User> users = (ArrayList<User>) message.getData();
			System.out.println("setuserFlag" + users);
			Platform.runLater(() -> {
				RoomController roomController = navigation.switchTo("Room.fxml");
				roomController.setCommu(this);
				roomController.setUserText(users);
			});
			break;
		case "UPDATE_LIST_ROOM":
			List<RoomClient> listRoom2 = (ArrayList<RoomClient>) message.getData();
			Platform.runLater(() -> {
				if (this.getNavigation().getController("FindRoom.fxml") != null) {
					FindRoomController findRoomController = this.getNavigation().getController("FindRoom.fxml");
					findRoomController.setRoomList(listRoom2);
				}
			});
		break;
		case "UPDATE_ROOM":
			List<User> users2 = (List<User>) message.getData();
			Platform.runLater(() -> {
				if (this.getNavigation().getController("Room.fxml") != null) {
					RoomController roomController = this.getNavigation().getController("Room.fxml");
					roomController.setUserText(users2);
				}
			});
		break;
		case "LOG_OUT_OK":
			System.out.println("dang xuat thanh cong     "+currentUser);
			break;
		case "START_GAME_OK":
			RoomClient room = (RoomClient) message.getData();
			Platform.runLater(() -> {
				GameController gameController =  this.getNavigation().switchTo("Game.fxml");
				gameController.createGame(room.getNumSeed(),room.getNumType());
			});
		break;
			

		default:
			Platform.runLater(() -> {
				Util.showError("Lối bất định");
			});

			break;

		}

	}

	public void checkConnection() throws IOException, ClassNotFoundException {
		if (socket.isClosed() || !socket.isConnected()) {
			System.out.println("Socket không còn kết nối!");
		} else {
			long startTime = System.currentTimeMillis();
			try {
				sendMessage(new Message("PING", "a"));
				Message respoMessage = receiveMessage();
				if (respoMessage != null && respoMessage.getLabel().equals("PONG")) {
					long endTime = System.currentTimeMillis();
					System.out.println("Co ket noi");
					System.out.println(endTime - startTime);
				}

			} catch (IOException e) {
				System.out.println("Socket không còn kết nối!(K ping dc)");
				close();
			}
		}
	}

//	private void checkConnectionLoop() throws IOException, ClassNotFoundException {
//
//		while (running) {
//			checkConnection();
//			try {
//				Thread.sleep(1000);
//			} catch (InterruptedException e) {
//				Thread.currentThread().interrupt();
//				break;
//			}
//		}
//	}

	public synchronized void sendMessage(Message message) throws IOException {
		oos.writeObject(message);
		oos.flush();
	}

	public Message receiveMessage() throws IOException, ClassNotFoundException {
		return (Message) ois.readObject();
	}

	public void close() throws IOException {
		running = false;
		ois.close();
		oos.close();
		socket.close();
	}

	public Socket connectToServer(String host, int port) {

		try {
			Socket socket = new Socket(host, port);
			System.out.println("Ket noi thanh cong!!!");
			return socket;

		} catch (Exception e) {
			e.printStackTrace();
		}
		System.out.println("k kn sv");

		return null;
	}

	public Navigation getNavigation() {
		return navigation;
	}

	public void setNavigation(Navigation navigation) {
		this.navigation = navigation;
	}

	public User getCurrentUser() {
		return currentUser;
	}
	

}
