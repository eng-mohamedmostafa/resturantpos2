package resturantpos2;

import javax.swing.*;
import java.awt.BorderLayout;
import java.awt.event.*;
import java.sql.*;
import java.text.SimpleDateFormat;
import java.util.*;

public class ReportPage extends JFrame {
    private JButton backButton, searchButton;
    private JTextArea reportArea;
    private JTextField dateField;

    public ReportPage() {
        setTitle("Sales Report");
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        reportArea = new JTextArea(30, 70);
        reportArea.setEditable(false);
        JScrollPane scrollPane = new JScrollPane(reportArea);

        backButton = new JButton("Back");
        backButton.addActionListener(e -> {
            PageFactory.createPage("Dashboard").setVisible(true);
            dispose();
        });

        searchButton = new JButton("Search");
        searchButton.addActionListener(e -> generateReport(dateField.getText().trim()));

        dateField = new JTextField(15);

        JPanel topPanel = new JPanel();
        topPanel.add(new JLabel("Enter Date (DD-MM-YYYY):"));
        topPanel.add(dateField);
        topPanel.add(searchButton);

        JPanel bottomPanel = new JPanel();
        bottomPanel.add(backButton);

        add(topPanel, BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);
        add(bottomPanel, BorderLayout.SOUTH);
    }

    private void generateReport(String date) {
        StringBuilder report = new StringBuilder();
        report.append("Sales Report for ").append(date).append(":\n\n");

        // Convert the input date from DD-MM-YYYY to YYYY-MM-DD
        String formattedDate = "";
        try {
            SimpleDateFormat inputFormat = new SimpleDateFormat("dd-MM-yyyy");
            SimpleDateFormat dbFormat = new SimpleDateFormat("yyyy-MM-dd");
            java.util.Date parsedDate = inputFormat.parse(date);
            formattedDate = dbFormat.format(parsedDate);
        } catch (Exception e) {
            e.printStackTrace();
        }

        // Retrieve sales transactions for the specified date
        List<SalesTransaction> transactions = getSalesTransactions(formattedDate);
        int total = 0;

        SimpleDateFormat sourceDateFormat = new SimpleDateFormat("yyyy-MM-dd");
        SimpleDateFormat targetDateFormat = new SimpleDateFormat("dd-MM-yyyy");

        for (SalesTransaction transaction : transactions) {
            String displayDate = "";
            try {
                java.util.Date parsedDate = sourceDateFormat.parse(transaction.getDate());
                displayDate = targetDateFormat.format(parsedDate);
            } catch (Exception e) {
                e.printStackTrace();
            }

            report.append("Product: ").append(transaction.getProduct())
                    .append(", Quantity: ").append(transaction.getQuantity())
                    .append(", Price: ").append(transaction.getPrice())
                    .append(", Date: ").append(displayDate).append("\n");

            total += transaction.getPrice() * transaction.getQuantity();
        }

        if (transactions.isEmpty()) {
            report.append("No sales transactions found for this date.");
        } else {
            report.append("\nTotal Sales: ").append(total).append(" EGP");
        }

        reportArea.setText(report.toString());
    }

    private List<SalesTransaction> getSalesTransactions(String date) {
        List<SalesTransaction> transactions = new ArrayList<>();
        String query = "SELECT p.name, s.quantity, p.price, s.sale_date FROM sales s " +
                       "JOIN products p ON s.product_id = p.id WHERE s.sale_date = ?";
        try (Connection conn = DatabaseHelperProxy.getInstance().getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setString(1, date);
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                String product = rs.getString("name");
                int quantity = rs.getInt("quantity");
                int price = rs.getInt("price");
                String saleDate = rs.getString("sale_date");
                transactions.add(new SalesTransaction(product, quantity, price, saleDate));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return transactions;
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new ReportPage().setVisible(true));
    }
}

class SalesTransaction {
    private String product;
    private int quantity;
    private int price;
    private String date;

    public SalesTransaction(String product, int quantity, int price, String date) {
        this.product = product;
        this.quantity = quantity;
        this.price = price;
        this.date = date;
    }

    public String getProduct() {
        return product;
    }

    public int getQuantity() {
        return quantity;
    }

    public int getPrice() {
        return price;
    }

    public String getDate() {
        return date;
    }
}
