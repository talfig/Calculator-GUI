import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Calculator extends Application {
    private static final int CALCULATOR_WIDTH_SIZE = 420;
    private static final int CALCULATOR_HEIGHT_SIZE = 450;

    public void start(Stage stage) throws Exception {
        Parent root = (Parent) FXMLLoader.load(getClass().getResource("calculator.fxml"));
        Scene scene = new Scene(root, CALCULATOR_WIDTH_SIZE, CALCULATOR_HEIGHT_SIZE);
        stage.setTitle("Calculator");
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        // Launch the JavaFX application
        launch(args);
        System.out.println();
    }
}
