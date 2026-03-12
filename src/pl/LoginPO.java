package pl;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.util.function.Consumer;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.UIManager;

import bll.ILoginBO;

/**
 * Presentation-layer login form.
 *
 * Displays a simple username / password dialog. On successful authentication
 * the provided {@code onSuccess} callback is invoked so that the caller can
 * open the main editor window.
 */
public class LoginPO extends JFrame {

    private static final long serialVersionUID = 1L;

    private final ILoginBO loginBO;
    private final Consumer<Void> onSuccess;

    private JTextField usernameField;
    private JPasswordField passwordField;
    private JLabel statusLabel;

    public LoginPO(ILoginBO loginBO, Consumer<Void> onSuccess) {
        this.loginBO = loginBO;
        this.onSuccess = onSuccess;

        try {
            UIManager.setLookAndFeel("javax.swing.plaf.nimbus.NimbusLookAndFeel");
        } catch (Exception e) {
            e.printStackTrace();
        }

        setTitle("Login – Arabic Text Editor");
        setSize(400, 280);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);

        buildUI();
        setVisible(true);
    }

    private void buildUI() {
        JPanel root = new JPanel(new BorderLayout());
        root.setBackground(Color.WHITE);

        // Title bar
        JLabel titleLabel = new JLabel("Arabic Text Editor", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 18));
        titleLabel.setBorder(BorderFactory.createEmptyBorder(20, 0, 10, 0));
        root.add(titleLabel, BorderLayout.NORTH);

        // Form
        JPanel form = new JPanel(new GridBagLayout());
        form.setBackground(Color.WHITE);
        form.setBorder(BorderFactory.createEmptyBorder(10, 40, 10, 40));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(6, 6, 6, 6);

        gbc.gridx = 0; gbc.gridy = 0; gbc.weightx = 0;
        form.add(new JLabel("Username:"), gbc);

        gbc.gridx = 1; gbc.weightx = 1;
        usernameField = new JTextField(20);
        form.add(usernameField, gbc);

        gbc.gridx = 0; gbc.gridy = 1; gbc.weightx = 0;
        form.add(new JLabel("Password:"), gbc);

        gbc.gridx = 1; gbc.weightx = 1;
        passwordField = new JPasswordField(20);
        form.add(passwordField, gbc);

        root.add(form, BorderLayout.CENTER);

        // Bottom panel
        JPanel bottom = new JPanel(new BorderLayout());
        bottom.setBackground(Color.WHITE);
        bottom.setBorder(BorderFactory.createEmptyBorder(0, 40, 20, 40));

        statusLabel = new JLabel(" ", SwingConstants.CENTER);
        statusLabel.setForeground(Color.RED);
        bottom.add(statusLabel, BorderLayout.NORTH);

        JButton loginButton = new JButton("Login");
        loginButton.setFont(new Font("Arial", Font.BOLD, 13));
        loginButton.addActionListener((ActionEvent e) -> handleLogin());
        bottom.add(loginButton, BorderLayout.CENTER);

        // Allow pressing Enter in the password field to submit
        passwordField.addActionListener((ActionEvent e) -> handleLogin());

        root.add(bottom, BorderLayout.SOUTH);

        setContentPane(root);
    }

    private void handleLogin() {
        String username = usernameField.getText().trim();
        char[] passwordChars = passwordField.getPassword();

        if (username.isEmpty() || passwordChars.length == 0) {
            statusLabel.setText("Username and password are required.");
            java.util.Arrays.fill(passwordChars, '\0');
            return;
        }

        String password = new String(passwordChars);
        java.util.Arrays.fill(passwordChars, '\0');

        boolean authenticated = loginBO.login(username, password);
        if (authenticated) {
            dispose();
            onSuccess.accept(null);
        } else {
            statusLabel.setText("Invalid username or password.");
            passwordField.setText("");
        }
    }
}
