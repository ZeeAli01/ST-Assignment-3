import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class LoginApp extends JFrame {
    private JTextField emailField;
    private JPasswordField passwordField;
    public static String DB_URL = "jdbc:mysql://localhost:3306/st";
    private static final String DB_USER = "root";
    private static final String DB_PASSWORD = "1234";

    public LoginApp() {
        setTitle("Login Screen");
        setSize(350, 200);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        JPanel panel = new JPanel();
        panel.setLayout(new GridLayout(3, 2, 10, 10));

        panel.add(new JLabel("Email:"));
        emailField = new JTextField();
        panel.add(emailField);

        panel.add(new JLabel("Password:"));
        passwordField = new JPasswordField();
        panel.add(passwordField);

        JButton loginButton = new JButton("Login");
        loginButton.addActionListener(new LoginAction());
        panel.add(loginButton);

        add(panel);
    }

    private class LoginAction implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            String email = emailField.getText();
            String password = new String(passwordField.getPassword());
            try {
                String name = authenticateUser(email, password);
                if (name != null) {
                    JOptionPane.showMessageDialog(null, "Welcome " + name + "!");
                } else {
                    JOptionPane.showMessageDialog(null, "Invalid email or password.");
                }
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(null, "Error: " + ex.getMessage());
            }
        }
    }

    public static String authenticateUser(String email, String password) throws Exception {
        if (DB_URL == null || DB_USER == null || DB_PASSWORD == null) {
            throw new Exception("Database connection parameters are missing.");
        }

        try (Connection connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD)) {
            String query = "SELECT name FROM user WHERE email = ? AND password = ?";
            try (PreparedStatement statement = connection.prepareStatement(query)) {
                statement.setString(1, email);
                statement.setString(2, password);

                try (ResultSet resultSet = statement.executeQuery()) {
                    if (resultSet.next()) {
                        return resultSet.getString("name");
                    }
                }
            }
        } catch (Exception e) {
            throw new Exception("Database error: " + e.getMessage(), e);
        }

        return null;
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            LoginApp app = new LoginApp();
            app.setVisible(true);
        });
    }
}
