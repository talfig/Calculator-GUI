import javafx.fxml.FXML;
import javafx.event.ActionEvent;
import javafx.scene.control.Button;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;

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
    private void handleOperator(ActionEvent event) {
        Button button = (Button) event.getSource();
        String operator = button.getText();
        appendOperator(" " + operator + " ");
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
            double result = evaluateExpression(expression.toString());
            String formattedResult;
            if (result == (long) result) { // Check if result is a whole number
                formattedResult = String.valueOf((long) result);
            } else {
                formattedResult = String.valueOf(result);
            }
            updateScreen(expression + " = " + formattedResult);
            expression.setLength(0);
            expression.append(formattedResult);
            startNewCalculation = true; // Mark that user finished a calculation
        } catch (Exception e) {
            updateScreen("Error");
            expression.setLength(0);
            startNewCalculation = true;
        }
    }

    private double evaluateExpression(String expr) {
        // Remove spaces if any
        expr = expr.replaceAll("\\s+", "");

        // Split numbers and operators
        java.util.List<Double> numbers = new java.util.ArrayList<>();
        java.util.List<Character> operators = new java.util.ArrayList<>();

        StringBuilder number = new StringBuilder();

        for (int i = 0; i < expr.length(); i++) {
            char c = expr.charAt(i);
            if (Character.isDigit(c) || c == '.') {
                number.append(c);
            } else if ("+-*/".indexOf(c) >= 0) {
                if (number.length() == 0 && c == '-') {
                    number.append(c); // handle negative number
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

        // First, handle * and / (higher precedence)
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

        // Then handle + and -
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

            if (c == '+' || c == '*' || c == '/') {
                return i;
            } else if (c == '-') {
                // Check if this '-' is a real operator or a negative sign
                if (i == 0 || "+-*/".indexOf(expression.charAt(i - 1)) >= 0) {
                    // It's a negative sign, skip
                    continue;
                } else {
                    return i; // it's a subtraction operator
                }
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
