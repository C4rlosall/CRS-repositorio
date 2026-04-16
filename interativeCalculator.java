import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.math.BigDecimal;
import java.math.RoundingMode;

public final class interativeCalculator extends JFrame implements ActionListener {
    private final JTextField display;
    private BigDecimal currentValue = BigDecimal.ZERO;
    private String pendingOperator = "";
    private boolean startNewNumber = true;

    public interativeCalculator() {

        super("Simple Calculator (Java 26)");
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setSize(360, 500);
        setMinimumSize(new Dimension(320, 440));
        setLocationRelativeTo(null);
        setLayout(new BorderLayout(10, 10));

        JPanel root = new JPanel(new BorderLayout(10, 10));
        root.setBorder(BorderFactory.createEmptyBorder(12, 12, 12, 12));
        root.setBackground(new Color(245, 245, 245));

        display = new JTextField("0");
        display.setEditable(false);
        display.setHorizontalAlignment(JTextField.RIGHT);
        display.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 28));
        display.setBackground(Color.WHITE);
        display.setBorder(BorderFactory.createCompoundBorder(

                BorderFactory.createLineBorder(new Color(210, 210, 210)),
                BorderFactory.createEmptyBorder(10, 10, 10, 10)
        ));
        root.add(display, BorderLayout.NORTH);

        JPanel buttonPanel = new JPanel(new GridLayout(5, 4, 8, 8));
        buttonPanel.setBackground(new Color(245, 245, 245));

             String[] buttons = {
                    "C", "MMC", "⌫", "/",
                    "7", "8", "9", "*",
                    "4", "5", "6", "-",
                    "1", "2", "3", "+",
                    "0", ".", "=", ""
             };

        for (String text : buttons) {
            if (text.isEmpty()) {
                buttonPanel.add(new JPanel());
                continue;
            }
                JButton button = new JButton(text);
                button.setFocusable(false);
                button.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 20));
                button.setBackground(Color.WHITE);
                button.addActionListener(this);
                buttonPanel.add(button);
        }

        root.add(buttonPanel, BorderLayout.CENTER);
        add(root, BorderLayout.CENTER);
    }

    @Override
    public void actionPerformed(ActionEvent event) {
        String cmd = event.getActionCommand();

        if (isDigitOrDot(cmd)) {
            appendDigitOrDot(cmd);
            return;
        }

            switch (cmd) {
                case "C" -> clearAll();
                case "⌫" -> backspace();
                case "+" -> applyOperator("+");
                case "-" -> applyOperator("-");
                case "*" -> applyOperator("*");
                case "/" -> applyOperator("/");
                case "=" -> calculateResult();
                case "MMC" -> calculateMMC();
                default -> {

            }
        }
    }

    private boolean isDigitOrDot(String value) {
        return value.length() == 1 && ("0123456789.".contains(value));
    }

    private void appendDigitOrDot(String text) {
        String currentText = display.getText();

        if (startNewNumber) {
            display.setText(text.equals(".") ? "0." : text);
            startNewNumber = false;
            return;
        }

        if (text.equals(".") && currentText.contains(".")) {
            return;
        }

        if (currentText.equals("0") && !text.equals(".")) {
            display.setText(text);
        } else {
            display.setText(currentText + text);
        }
    }

    private void clearAll() {
        currentValue = BigDecimal.ZERO;
        pendingOperator = "";
        startNewNumber = true;
        display.setText("0");
    }

    private void backspace() {
        if (startNewNumber) {
            return;
        }

        String currentText = display.getText();
        if (currentText.length() <= 1) {
            display.setText("0");
            startNewNumber = true;
            return;
        }
        display.setText(currentText.substring(0, currentText.length() - 1));
    }

    private void applyOperator(String operator) {
        if (!pendingOperator.isEmpty() && !startNewNumber) {
            calculateResult();
        } else {
            currentValue = parseDisplayAsBigDecimal();
        }
        pendingOperator = operator;
        startNewNumber = true;
    }

    private void calculateResult() {
        if (pendingOperator.isEmpty() || startNewNumber) {
            return;
        }

        BigDecimal nextValue = parseDisplayAsBigDecimal();
        BigDecimal result;

        if (pendingOperator.equals("+")) {

            int n1 = currentValue.intValue();
            int n2 = nextValue.intValue();

            if (n1 <= 5 && n2 <= 5) {

                String[] mensagens = {
                        "Seriously? 😐",
                        "Sério isso? 😂",
                        "You can do better...",
                        "Matemática básica hein 👀",
                        "Really? 🤨"
                };

                int random = (int)(Math.random() * mensagens.length);

                JOptionPane.showMessageDialog(this, mensagens[random]);
            }
        }

        try {
            result = switch (pendingOperator) {
                case "+" -> currentValue.add(nextValue);
                case "-" -> currentValue.subtract(nextValue);
                case "*" -> currentValue.multiply(nextValue);
                case "/" -> {
                    if (nextValue.compareTo(BigDecimal.ZERO) == 0) {
                        throw new ArithmeticException("Division by zero");
                    }
                    yield currentValue.divide(nextValue, 12, RoundingMode.HALF_UP);
                }
                default -> nextValue;
            };
        } catch (ArithmeticException exception) {
            JOptionPane.showMessageDialog(
                    this,
                    "Error: " + exception.getMessage(),
                    "Calculation Error",
                    JOptionPane.ERROR_MESSAGE
            );
            clearAll();
            return;
        }

        currentValue = result.stripTrailingZeros();
        display.setText(formatBigDecimal(currentValue));
        pendingOperator = "";
        startNewNumber = true;
    }

    private void calculateMMC() {
        String first = JOptionPane.showInputDialog(this, "First integer:");
        if (first == null) {
            return;
        }
        String second = JOptionPane.showInputDialog(this, "Second integer:");
        if (second == null) {
            return;
        }

        try {
            long a = Long.parseLong(first.trim());
            long b = Long.parseLong(second.trim());

            if (a == 0 || b == 0) {
                throw new IllegalArgumentException("MMC with zero is not defined here.");
            }

                long mmc = lcm(Math.abs(a), Math.abs(b));
                display.setText(Long.toString(mmc));
                currentValue = BigDecimal.valueOf(mmc);
                pendingOperator = "";
                startNewNumber = true;
        } catch (NumberFormatException exception) {
            JOptionPane.showMessageDialog(
                    this,
                    "Please enter valid integers.",
                    "Invalid Input",
                    JOptionPane.WARNING_MESSAGE
            );
        } catch (IllegalArgumentException exception) {
            JOptionPane.showMessageDialog(
                    this,
                    exception.getMessage(),
                    "MMC Error",
                    JOptionPane.WARNING_MESSAGE
            );
        }
    }

    private long gcd(long a, long b) {
        while (b != 0) {
            long temp = b;
            b = a % b;
            a = temp;
        }
        return a;
    }

    private long lcm(long a, long b) {
        return (a / gcd(a, b)) * b;
    }

    private BigDecimal parseDisplayAsBigDecimal() {
        return new BigDecimal(display.getText());
    }

    private String formatBigDecimal(BigDecimal value) {
        String plain = value.stripTrailingZeros().toPlainString();
        return plain.equals("-0") ? "0" : plain;
        }

    public static void main(String[] args) {
             SwingUtilities.invokeLater(() -> {
                    try {
                        UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
                    } catch (Exception ignored) {

                    }
                    new interativeCalculator().setVisible(true);
        });
    }
}
//code by CRS.