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
        if (operator.equals("-")) {
            operator = "–"; // replace minus with en-dash for subtraction
        }
        appendOperator(" " + operator + " ");
    }

    @FXML
    private void negate(ActionEvent event) {
        if (expression.length() == 0) return;

        int lastOperatorIndex = findLastOperatorIndex();
        int startIndex = lastOperatorIndex + 1;

        if (startIndex >= expression.length()) return;

        // Find the actual start of the number (skip spaces)
        while (startIndex < expression.length() && expression.charAt(startIndex) == ' ') {
            startIndex++;
        }
        if (startIndex >= expression.length()) return;

        // If it's the first number (no operator before it)
        if (lastOperatorIndex == -1) {
            // If the first character is '-', remove it
            if (expression.charAt(0) == '-') {
                expression.deleteCharAt(0);
            } else {
                // Otherwise, prepend minus to the first number
                expression.insert(0, '-');
            }
        } else {
            // Check if the character before the number is a minus
            if (expression.charAt(startIndex - 1) == '-') {
                // If already negated, remove the minus
                expression.deleteCharAt(startIndex - 1);
            } else {
                // Insert minus sign before the number
                expression.insert(startIndex, '-');
            }
        }

        updateScreen(expression.toString());
    }

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

    private double evaluateExpression(String expr) {
        expr = expr.replaceAll("\\s+", "");
        expr = expr.replace('–', '-'); // Replace en-dash with minus for evaluation

        java.util.List<Double> numbers = new java.util.ArrayList<>();
        java.util.List<Character> operators = new java.util.ArrayList<>();

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
            startNewCalculation = false;
        }

        char lastChar = expression.charAt(expression.length() - 1);
        if ("+–*/".indexOf(lastChar) >= 0) {
            expression.setLength(expression.length() - 1); // Remove old operator
            expression.append(op.trim());
        } else {
            expression.append(op);
        }
        updateScreen(expression.toString());
    }

    private int findLastOperatorIndex() {
        for (int i = expression.length() - 1; i >= 0; i--) {
            char c = expression.charAt(i);
            if (c == '+' || c == '*' || c == '/' || c == '–') {
                return i;
            } else if (c == '-') {
                // Check if negative sign or operator
                if (i == 0 || "+–*/".indexOf(expression.charAt(i - 1)) >= 0) {
                    continue;
                } else {
                    return i;
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
