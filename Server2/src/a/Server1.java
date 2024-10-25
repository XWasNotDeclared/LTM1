package a;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.swing.JOptionPane;

import model.Message;
import model.User;



public class Server1 {
	
	private static final int PORT = 12345;
	
	private static final List<SocketHandler> clients = Collections.synchronizedList(new ArrayList<>());
	private static final List<User> users = Collections.synchronizedList(new ArrayList<>());
	
	public static void main (String args[]) {
		 
		try (ServerSocket serverSocket = new ServerSocket(PORT)){
			System.out.println("Server open on port" + PORT);
			while (true) {
				
				
				// Thread để lắng nghe và kiểm tra việc tắt server
//	            new Thread(() -> {
//	                while (true) {
//	                    int response = JOptionPane.showConfirmDialog(null,
//	                            "Do you want to stop the server?", 
//	                            "Stop Server", 
//	                            JOptionPane.YES_NO_OPTION);
//	                    if (response == JOptionPane.YES_OPTION) {
//	                        try {
//	                            serverSocket.close();
//	                            System.out.println("Server stopped.");
//	                            break;
//	                        } catch (IOException e) {
//	                            e.printStackTrace();
//	                        }
//	                    }
//	                }
//	            }).start();

			/////////////////////////////	

				
				Socket socket = serverSocket.accept();
				
				SocketHandler loginHandler = new SocketHandler(socket);
				
				clients.add(loginHandler);
				
				new Thread(loginHandler).start();
				
			}
			
			
		}
		catch (IOException e) {
			e.printStackTrace();
		}
				
	}
	
	public static void broadcastMessage(Message msg) {
		synchronized (clients) {
			for(SocketHandler client: clients) {
				try {
					client.sendMessage(msg);
				}catch(IOException e) {
					e.printStackTrace();
				}
			}
		}
	
		
	}
	public static void removeClient (SocketHandler client) {
		clients.remove(client);
	}
	public static void addUser (User user) {
		users.add(user);
	}
	public static void removeUser(User user) {
		users.remove(user);
	}

	public static List<User> getUsers() {
		return users;
	}
	
	
	
	
}