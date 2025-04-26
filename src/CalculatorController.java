import javafx.fxml.FXML;
import javafx.event.ActionEvent;
import javafx.scene.control.Button;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;

public class CalculatorController {
    @FXML
    private VBox screen;

    private final StringBuilder expression = new StringBuilder();
    private boolean startNewCalculation = false; // True after pressing "="

    @FXML
    public void initialize() {
        clearScreen();
    }

    @FXML
    private void handleNumber(ActionEvent event) {
        Button button = (Button) event.getSource();
        String value = button.getText();

        if (startNewCalculation) {
            // New number after '=': start new expression
            expression.setLength(0);
            startNewCalculation = false;
        }

        expression.append(value);
        updateScreen(expression.toString());
    }

    @FXML
    private void add(ActionEvent event) {
        appendOperator("+");
    }

    @FXML
    private void subtract(ActionEvent event) {
        appendOperator("-");
    }

    @FXML
    private void multiply(ActionEvent event) {
        appendOperator("*");
    }

    @FXML
    private void divide(ActionEvent event) {
        appendOperator("/");
    }

    @FXML
    private void negate(ActionEvent event) {
        if (expression.length() == 0) return;

        int lastOperatorIndex = findLastOperatorIndex();
        int startIndex = lastOperatorIndex + 1;

        if (startIndex >= expression.length()) return;

        // Check if number is already negative
        if (expression.charAt(startIndex) == '-') {
            // Already negative -> remove the minus
            expression.deleteCharAt(startIndex);
        } else {
            // Not negative -> insert minus
            expression.insert(startIndex, '-');
        }

        updateScreen(expression.toString());
    }

    @FXML
    private void equal(ActionEvent event) {
        if (expression.length() == 0) return;

        try {
            ScriptEngine engine = new ScriptEngineManager().getEngineByName("JavaScript");
            Object result = engine.eval(expression.toString());

            updateScreen(expression + " = " + result.toString());
            expression.setLength(0);
            expression.append(result.toString());
            startNewCalculation = true; // Mark that user finished a calculation
        } catch (ScriptException e) {
            updateScreen("Error");
            expression.setLength(0);
            startNewCalculation = true;
        }
    }

    private void appendOperator(String op) {
        if (expression.length() == 0) return;

        if (startNewCalculation) {
            // Continue calculation with the result
            startNewCalculation = false;
        }

        char lastChar = expression.charAt(expression.length() - 1);
        if ("+-*/".indexOf(lastChar) >= 0) {
            // Replace last operator
            expression.setCharAt(expression.length() - 1, op.charAt(0));
        } else {
            expression.append(op);
        }
        updateScreen(expression.toString());
    }

    private int findLastOperatorIndex() {
        for (int i = expression.length() - 1; i >= 0; i--) {
            char c = expression.charAt(i);

            // Only consider +, *, / as real operators
            if ((c == '+' || c == '*' || c == '/')) {
                return i;
            }
        }
        return -1;
    }

    private void updateScreen(String text) {
        screen.getChildren().clear();
        Text displayText = new Text(text);
        displayText.setStyle("-fx-font-size: 30px;");
        screen.getChildren().add(displayText);
    }

    private void clearScreen() {
        updateScreen("0");
    }
}
