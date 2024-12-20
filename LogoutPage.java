package resturantpos2;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class LogoutPage extends JFrame {
    private JButton loginButton;

    public LogoutPage() {
        setTitle("Logout");
        setSize(300, 200);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        JLabel logoutLabel = new JLabel("You have been logged out.", SwingConstants.CENTER);
        loginButton = new JButton("Login Again");

        loginButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                PageFactory.createPage("LoginForm").setVisible(true);
                dispose();
            }
        });

        JPanel panel = new JPanel(new BorderLayout());
        panel.add(logoutLabel, BorderLayout.CENTER);
        panel.add(loginButton, BorderLayout.SOUTH);

        add(panel);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new LogoutPage().setVisible(true));
    }
}
