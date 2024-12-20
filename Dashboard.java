package resturantpos2;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class Dashboard extends JFrame {
    private JButton salesButton, inventoryButton, reportButton, logoutButton;

    public Dashboard() {
        setTitle("Dashboard");
        setSize(400, 300);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        salesButton = new JButton("Sales");
        inventoryButton = new JButton("Inventory");
        reportButton = new JButton("Sales Report");
        logoutButton = new JButton("Logout");

        salesButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                PageFactory.createPage("SalesPage").setVisible(true);
                dispose();
            }
        });

        inventoryButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                PageFactory.createPage("InventoryPage").setVisible(true);
                dispose();
            }
        });

        reportButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                PageFactory.createPage("ReportPage").setVisible(true);
                dispose();
            }
        });

        logoutButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                PageFactory.createPage("LoginForm").setVisible(true);
                dispose();
            }
        });
        


        JPanel panel = new JPanel();
        panel.setLayout(new GridLayout(4, 1, 10, 10));
        panel.add(salesButton);
        panel.add(inventoryButton);
        panel.add(reportButton);
        panel.add(logoutButton);

        add(panel);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new Dashboard().setVisible(true));
    }
}
