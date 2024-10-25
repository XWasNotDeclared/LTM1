package application;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import model.Message;
import model.User;

public class RoomController {
	private Communication commu;

    public Communication getCommu() {
		return commu;
	}


	public void setCommu(Communication commu) {
		this.commu = commu;
	}

    @FXML
    private Button StartButton;

    @FXML
    private Label UserName1;

    @FXML
    private Label UserName2;

    @FXML
    void startButtonClick(ActionEvent event) {
    	
    	try {
			commu.sendMessage(new Message("START",null));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    	
    	
    }
    @FXML
    void goBackButtunClick(ActionEvent event) {
    	leaveRoom();
    	commu.getNavigation().switchTo("Home.fxml");
    	UserName1.setText("Waiting1");
    	UserName2.setText("Waiting2");
    }

	public void setUserName1(String userName1) {
		UserName1.setText(userName1);
	}


	public void setUserName2(String userName2) {
		UserName2.setText(userName2);
	}
	
	private void leaveRoom() {
		try {
			commu.sendMessage(new Message("LEAVE_ROOM",null));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	public void setUserText(List<User> u) {
		System.out.println(u);
		if (u.size()>=2) {
			UserName1.setText(u.get(0).getUsername());
			UserName2.setText(u.get(1).getUsername());
		}
		else {
			UserName1.setText(u.get(0).getUsername());
			UserName2.setText("Wait");
		}
		
		
	}
	
	


}
