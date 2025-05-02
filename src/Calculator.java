import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

/**
 * The Calculator class represents a simple JavaFX application
 * that loads a calculator UI from an FXML file and displays it in a window.
 */
public class Calculator extends Application {
    // Constants defining the dimensions of the calculator window
    private static final int CALCULATOR_WIDTH_SIZE = 420;
    private static final int CALCULATOR_HEIGHT_SIZE = 450;

    /**
     * The main entry point for all JavaFX applications.
     * This method is called after the application is launched.
     *
     * @param stage the primary stage for this application.
     * @throws Exception if loading the FXML file fails.
     */
    @Override
    public void start(Stage stage) throws Exception {
        // Load the calculator layout from the FXML file
        Parent root = FXMLLoader.load(getClass().getResource("calculator.fxml"));
        Scene scene = new Scene(root, CALCULATOR_WIDTH_SIZE, CALCULATOR_HEIGHT_SIZE);
        stage.setTitle("Calculator");
        stage.setScene(scene);
        stage.show();
    }

    /**
     * The main method launches the JavaFX application.
     *
     * @param args command-line arguments (not used).
     */
    public static void main(String[] args) {
        launch(args);
        System.out.println();
    }
}
