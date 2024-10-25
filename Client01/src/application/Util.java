package application;

import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.stage.Stage;

public class Util {
	public static void showError (String errorMessage) {
		Alert alert = new Alert(AlertType.INFORMATION);
//		alert.setTitle("Lỗi");
//		alert.setHeaderText("Xảy ra lỗi");
		alert.setContentText(errorMessage);
		alert.showAndWait();
	}
	
    // Phương thức để lấy Stage hiện tại
    public static Stage getCurrentStage() {
        return (Stage) javafx.stage.Window.getWindows().stream()
            .filter(window -> window.isShowing())
            .findFirst()
            .orElse(null);
    }
	

}
