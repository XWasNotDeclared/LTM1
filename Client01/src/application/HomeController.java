package application;



import java.io.IOException;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ListView;
import javafx.scene.text.Text;
import model.Message;

public class HomeController {
	private Communication commu;

    public Communication getCommu() {
		return commu;
	}


	public void setCommu(Communication commu) {
		this.commu = commu;
	}



	@FXML
    private ComboBox<?> comboBox;

    @FXML
    private ListView<?> listView1;

    @FXML
    private ListView<?> listView2;

    @FXML
    private Button newRoom;

    @FXML
    private Text scoreText;

    @FXML
    private Text userNameText;
    @FXML
    private Button logOutButton;

    @FXML
    void newRoomClick(ActionEvent event) {
    	CreateNewRoomController createNewRoomController = commu.getNavigation().switchTo("CreateNewRoom.fxml");
    	createNewRoomController.setCommu(commu);
    }
    @FXML
    void logOutButtonClick(ActionEvent event) {
    	commu.getNavigation().switchTo("Login.fxml");
    	try {
			commu.sendMessage(new Message("LOG_OUT",commu.getCurrentUser()));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    }   
    @FXML
    void findRoomButtonClick(ActionEvent event) {
    	
    	try {
			commu.sendMessage(new Message("GET_ROOM_LIST", null));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    	
    	FindRoomController findRoomController =  commu.getNavigation().switchTo("FindRoom.fxml");
    	findRoomController.setCommu(commu);
    }
    

	
	public void updateUserInfor (String userName, float score) {
		userNameText.setText(userName);
		scoreText.setText(String.valueOf(score));
	}
	

}
