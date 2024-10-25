package application;

import javafx.fxml.FXML;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class GameController {
    @FXML
    private Pane gamePane;

    private List<Circle> circles = new ArrayList<>();
    private Color[] colors;

    // To store the last position of the mouse
    private double xOffset = 0;
    private double yOffset = 0;

//    public void initialize() {
//        createGame(20, 2); // Tạo game với 20 hình tròn và 2 màu
//    }

    public void createGame(int numCircles, int numColorTypes) {
        Random random = new Random();

        // Tạo màu ngẫu nhiên
        colors = new Color[numColorTypes];
        for (int i = 0; i < numColorTypes; i++) {
            colors[i] = Color.rgb(random.nextInt(256), random.nextInt(256), random.nextInt(256));
        }

        // Tạo các hình tròn
        for (int i = 0; i < numCircles; i++) {
            Color color = colors[random.nextInt(numColorTypes)];
            Circle circle = new Circle(20, color);
            circle.setStroke(Color.BLACK);
            circle.setStrokeWidth(2);
            circle.setCenterX(50 + random.nextInt(300));
            circle.setCenterY(50 + random.nextInt(300));
            circles.add(circle);
            gamePane.getChildren().add(circle);

            // Thêm chức năng kéo cho hình tròn
            addDragFunctionality(circle);
        }

        // Tạo các hình chữ nhật cho màu tương ứng
        for (int i = 0; i < numColorTypes; i++) {
            Rectangle rectangle = new Rectangle(100, 30);
            rectangle.setFill(colors[i]);
            rectangle.setX(50 + i * 150);
            rectangle.setY(350);
            gamePane.getChildren().add(rectangle);
        }
    }

    private void addDragFunctionality(Circle circle) {
        // Handle mouse press event
        circle.setOnMousePressed((MouseEvent event) -> {
            // Bring the clicked circle to the front
            gamePane.getChildren().remove(circle);
            gamePane.getChildren().add(circle);

            // Capture the current mouse position relative to the circle
            xOffset = event.getSceneX() - circle.getCenterX();
            yOffset = event.getSceneY() - circle.getCenterY();
        });

        // Handle mouse drag event
        circle.setOnMouseDragged((MouseEvent event) -> {
            // Update the position of the circle as per the mouse movement
            circle.setCenterX(event.getSceneX() - xOffset);
            circle.setCenterY(event.getSceneY() - yOffset);
        });

        // Handle mouse release event
        circle.setOnMouseReleased((MouseEvent event) -> {
            for (int i = 0; i < colors.length; i++) {
                // Check if the circle is within the bounds of the rectangle
                if (isCircleInRectangle(circle, 50 + i * 150, 350, 100, 30)) {
                    if (circle.getFill().equals(colors[i])) {
                        // Remove the circle from the pane and list
                        gamePane.getChildren().remove(circle);
                        circles.remove(circle);
                        // Check if there are no circles left
                        if (circles.isEmpty()) {
                            System.out.println("You Win!");
                        }
                        return; // Exit the loop if matched
                    }
                }
            }
            // Nếu không khớp, có thể đưa hình tròn trở lại vị trí ban đầu (tuỳ chọn)
        });
    }

    // Check if the circle is within the bounds of the rectangle
    private boolean isCircleInRectangle(Circle circle, double rectX, double rectY, double rectWidth, double rectHeight) {
        double circleX = circle.getCenterX();
        double circleY = circle.getCenterY();
        double circleRadius = circle.getRadius();

        return circleX + circleRadius > rectX && circleX - circleRadius < rectX + rectWidth &&
               circleY + circleRadius > rectY && circleY - circleRadius < rectY + rectHeight;
    }
}
