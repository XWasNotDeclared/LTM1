package application;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import model.Message;
import model.User;

public class RegisterController {
	
	private Communication commu;
	

    @FXML
    private TextField paswordText;

    @FXML
    private Button registerButton;

    @FXML
    private TextField userNameText;

    @FXML
    void NavToLogin(ActionEvent event) {
		commu.getNavigation().switchTo("Login.fxml");
		
    }

    @FXML
    void Register(ActionEvent event) {
		String username = userNameText.getText();
		String password = paswordText.getText();
		System.out.println("DangNhapClick" + username + password);
		User user = new User(username, password);
		sendRegisterMessage(user);
    }
    
    
	public void sendRegisterMessage(User u) {
		try {
			Message msg = new Message("REGISTER", u);
			commu.sendMessage(msg);
			System.out.println("Gui yeu cau dang ki");
		} catch (Exception e) {
			e.printStackTrace();
			Util.showError("Không thể kết nối đến server");
		}

	}
    
    
    
    
    
    
    
    
    
	public Communication getCommu() {
		return commu;
	}

	public void setCommu(Communication commu) {
		this.commu = commu;
	}

    

}
