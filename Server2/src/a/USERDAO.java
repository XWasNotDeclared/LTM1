package a;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.PreparedStatement;
import java.util.ArrayList;
import java.util.List;

import model.User;

import java.sql.ResultSet;

public class USERDAO {
	
	public User Login(User user) {
		String sql = "SELECT * FROM tamnhatthoc1.users WHERE username = ? and pw = ?";
		try (Connection conn = DBConnect.getConnect();
				
				PreparedStatement stmt = conn.prepareStatement(sql)){
			stmt.setString(1, user.getUsername());
			stmt.setString(2, user.getPassword());
			
			ResultSet rs = stmt.executeQuery();
			if (rs.next()) {
				int uid = rs.getInt("uid");
				String name = rs.getString("username");
				String avatar =  rs.getString("avatar");
				float score = rs.getFloat("score");
				String date_create = rs.getString("date_create");
				return new User(uid, name, avatar, score, date_create);
			}
			else {
				return null;
			}
			
			
		}catch (SQLException e) {
			e.printStackTrace();
		}
		
		
		
		return null;
	}
	
	
	
	public String registerUser (User user) {
		
		String sql = "SELECT * FROM tamnhatthoc1.users WHERE username = ?";
		
		String sql1 = "INSERT INTO tamnhatthoc1.users (username, pw, avatar, score) values (?,?,?,?)";
		
		try (Connection conn = DBConnect.getConnect();
				PreparedStatement stmt = conn.prepareStatement(sql);
				PreparedStatement stmt1 = conn.prepareStatement(sql1)){
			stmt.setString(1, user.getUsername());
			ResultSet rs = stmt.executeQuery();
			if (rs.next()) {
				System.out.println("Flagres");
				return "DUPLICATE_USER_REGISTER_FALSE";
			}
			stmt1.setString(1, user.getUsername());
			stmt1.setString(2, user.getPassword());
			stmt1.setString(3, user.getAvatar());
			stmt1.setFloat(4, user.getScore());
			stmt1.executeUpdate();
			System.out.println("Add new user success!!!");
		return "REGISTER_TRUE";
		}
		catch(SQLException e) {
			e.printStackTrace();
			return "REGISTER_FALSE";
		}
		
	}
	
//	public User getUserInfor (User user) {
//		String sql = "SELECT * FROM tamnhatthoc1.users WHERE ?";
//		try (Connection conn = DBConnect.getConnect();
//				PreparedStatement stmt = conn.prepareStatement(sql)){
//			stmt.st
//			
//			
//		}catch(SQLException e) {
//			e.printStackTrace();
//		}
//		
//		
//		return user;
//	}
	
	
	public List<User> getAllUserInfor(){
		List <User> userList = new ArrayList<>();
		String sql = "SELECT * FROM tamnhatthoc1.users";
		
		try (Connection conn = DBConnect.getConnect();
			PreparedStatement stmt = conn.prepareStatement(sql)){
			ResultSet rs = stmt.executeQuery();
			
			while (rs.next()) {
				int uid = rs.getInt("uid");
				String name = rs.getString("username");
				String avatar =  rs.getString("avatar");
				float score = rs.getFloat("score");
				String date_create = rs.getString("date_create");
				userList.add(new User(uid, name, avatar, score, date_create));
				
			}
			return userList;
			
			
		}
		catch(SQLException e) {
			e.printStackTrace();
		}
		
		
		
		
		return null;
	}
}
