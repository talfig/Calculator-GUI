import javafx.fxml.FXML;
import javafx.event.ActionEvent;
import javafx.scene.control.Button;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;

import java.util.*;

/**
 * Controller class for a basic calculator implemented with JavaFX.
 * Handles number input, operator input, negation, evaluation of expressions, and UI updates.
 */
public class CalculatorController {
    @FXML
    private VBox screen;

    private final StringBuilder expression = new StringBuilder(); // Holds the current expression entered by the user
    private boolean startNewCalculation = false; // Indicates whether a new calculation should begin (set after pressing "=")

    /**
     * Initializes the controller after the FXML has been loaded.
     */
    @FXML
    public void initialize() {
        clearScreen();
    }

    /**
     * Handles input for number buttons.
     * @param event the event triggered by clicking a number button
     */
    @FXML
    private void handleNumber(ActionEvent event) {
        Button button = (Button) event.getSource();
        String value = button.getText();

        if (startNewCalculation) {
            expression.setLength(0);
            startNewCalculation = false;
        }

        expression.append(value);
        updateScreen(expression.toString());
    }

    /**
     * Handles input for operator buttons (+, -, *, /).
     * Replaces standard minus with an en-dash for subtraction display.
     * @param event the event triggered by clicking an operator button
     */
    @FXML
    private void handleOperator(ActionEvent event) {
        Button button = (Button) event.getSource();
        String operator = button.getText();
        if (operator.equals("-")) {
            operator = "–"; // replace minus with en-dash for subtraction
        }
        appendOperator(" " + operator + " ");
    }

    /**
     * Negates the last number in the expression.
     * @param event the event triggered by clicking the "+/-" (negate) button
     */
    @FXML
    private void negate(ActionEvent event) {
        if (expression.length() == 0) return;

        int lastOperatorIndex = findLastOperatorIndex();
        int startIndex = lastOperatorIndex + 1;

        // Skip spaces
        while (startIndex < expression.length() && expression.charAt(startIndex) == ' ') {
            startIndex++;
        }
        if (startIndex >= expression.length()) return;

        // If it's the first number (no operator before it)
        if (lastOperatorIndex == -1) {
            if (expression.charAt(0) == '-') {
                expression.deleteCharAt(0);
            } else {
                expression.insert(0, '-');
            }
        } else {
            // Check if already negated
            if (expression.charAt(startIndex - 1) == '-') {
                expression.deleteCharAt(startIndex - 1);
            } else {
                expression.insert(startIndex, '-');
            }
        }

        updateScreen(expression.toString());
    }

    /**
     * Evaluates the current mathematical expression and displays the result.
     * @param event the event triggered by clicking the "=" button
     */
    @FXML
    private void equal(ActionEvent event) {
        if (expression.length() == 0) return;

        try {
            double result = evaluateExpression(expression.toString());
            String formattedResult;
            if (result == (long) result) {
                formattedResult = String.valueOf((long) result);
            } else {
                formattedResult = String.valueOf(result);
            }
            updateScreen(expression + " = " + formattedResult);
            expression.setLength(0);
            expression.append(formattedResult);
            startNewCalculation = true;
        } catch (Exception e) {
            updateScreen("Error");
            expression.setLength(0);
            startNewCalculation = true;
        }
    }

    /**
     * Parses and evaluates a mathematical expression string.
     * Supports +, –, *, and / with standard precedence.
     * @param expr the expression string to evaluate
     * @return the computed result
     */
    private double evaluateExpression(String expr) {
        expr = expr.replaceAll("\\s+", "");
        expr = expr.replace('–', '-'); // Replace en-dash with minus

        List<Double> numbers = new ArrayList<>();
        List<Character> operators = new ArrayList<>();
        StringBuilder number = new StringBuilder();

        for (int i = 0; i < expr.length(); i++) {
            char c = expr.charAt(i);
            if (Character.isDigit(c) || c == '.') {
                number.append(c);
            } else if ("+-*/".indexOf(c) >= 0) {
                if (number.length() == 0 && c == '-') {
                    number.append(c); // negative number
                } else {
                    numbers.add(Double.parseDouble(number.toString()));
                    number.setLength(0);
                    operators.add(c);
                }
            }
        }

        if (number.length() > 0) {
            numbers.add(Double.parseDouble(number.toString()));
        }

        // Handle * and / first
        for (int i = 0; i < operators.size(); ) {
            char op = operators.get(i);
            if (op == '*' || op == '/') {
                double a = numbers.get(i);
                double b = numbers.get(i + 1);

                if (op == '/' && b == 0) {
                    throw new ArithmeticException("Division by zero is not allowed!");
                }

                double res = (op == '*') ? a * b : a / b;
                numbers.set(i, res);
                numbers.remove(i + 1);
                operators.remove(i);
            } else {
                i++;
            }
        }

        // Handle + and -
        double result = numbers.get(0);
        for (int i = 0; i < operators.size(); i++) {
            char op = operators.get(i);
            double b = numbers.get(i + 1);
            if (op == '+') {
                result += b;
            } else {
                result -= b;
            }
        }

        return result;
    }

    /**
     * Appends an operator to the current expression, replacing the last operator if one already exists.
     * @param op the operator string to append
     */
    private void appendOperator(String op) {
        if (expression.length() == 0) return;

        if (startNewCalculation) {
            startNewCalculation = false;
        }

        char lastChar = expression.charAt(expression.length() - 1);
        if ("+–*/".indexOf(lastChar) >= 0) {
            expression.setLength(expression.length() - 1); // Remove last operator
            expression.append(op.trim());
        } else {
            expression.append(op);
        }

        updateScreen(expression.toString());
    }

    /**
     * Finds the index of the last operator in the expression.
     * Used to determine where the last number starts.
     * @return index of the last operator, or -1 if none
     */
    private int findLastOperatorIndex() {
        for (int i = expression.length() - 1; i >= 0; i--) {
            char c = expression.charAt(i);
            if (c == '+' || c == '*' || c == '/' || c == '–') {
                return i;
            } else if (c == '-') {
                if (i != 0 && "+–*/".indexOf(expression.charAt(i - 1)) < 0) {
                    return i;
                }
            }
        }
        return -1;
    }

    /**
     * Updates the calculator screen with the provided text.
     * @param text the text to display on the screen
     */
    private void updateScreen(String text) {
        screen.getChildren().clear();
        Text displayText = new Text(text);
        displayText.setStyle("-fx-font-size: 30px;");
        screen.getChildren().add(displayText);
    }

    /**
     * Clears the calculator screen and resets the display to "0".
     */
    private void clearScreen() {
        updateScreen("0");
    }
}
