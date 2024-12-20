package resturantpos2;

import javax.swing.*;
import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.event.*;
import java.sql.*;
import java.util.*;

public class SalesPage extends JFrame {
    private JButton backButton, submitButton;
    private JComboBox<String> categoryBox;
    private JList<String> productList;
    private DefaultListModel<String> productModel;
    private JTextArea cartArea;
    private Map<String, Integer> cart;
    private JSlider quantitySlider;

    public SalesPage() {
        setTitle("Sales");
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        cart = new HashMap<>();

        categoryBox = new JComboBox<>(getCategories().toArray(new String[0]));
        productModel = new DefaultListModel<>();
        productList = new JList<>(productModel);
        cartArea = new JTextArea(15, 30);
        cartArea.setEditable(false);
        backButton = new JButton("Back");
        submitButton = new JButton("Submit Order");
        quantitySlider = new JSlider(1, 10, 1);

        // Customize quantity slider
        quantitySlider.setMajorTickSpacing(1);
        quantitySlider.setPaintTicks(true);
        quantitySlider.setPaintLabels(true);

        categoryBox.addActionListener(e -> {
            String category = (String) categoryBox.getSelectedItem();
            productModel.clear();
            if (category != null) {
                for (String product : getProductsByCategory(category)) {
                    productModel.addElement(product);
                }
            }
        });

        productList.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 1) {
                    String selectedProduct = productList.getSelectedValue();
                    int quantity = quantitySlider.getValue();
                    cart.put(selectedProduct, cart.getOrDefault(selectedProduct, 0) + quantity);
                    updateCart();
                }
            }
        });

        backButton.addActionListener(e -> {
            PageFactory.createPage("Dashboard").setVisible(true);
            dispose();
        });

        submitButton.addActionListener(e -> {
            submitOrder();
        });

        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.add(new JLabel("Sales Page", SwingConstants.CENTER), BorderLayout.NORTH);
        topPanel.add(categoryBox, BorderLayout.CENTER);

        JPanel centerPanel = new JPanel(new GridLayout(1, 3));
        centerPanel.add(new JScrollPane(productList));
        centerPanel.add(new JScrollPane(cartArea));

        JPanel sliderPanel = new JPanel(new BorderLayout());
        sliderPanel.add(new JLabel("Quantity"), BorderLayout.NORTH);
        sliderPanel.add(quantitySlider, BorderLayout.CENTER);

        centerPanel.add(sliderPanel);

        JPanel bottomPanel = new JPanel(new GridLayout(1, 2));
        bottomPanel.add(backButton);
        bottomPanel.add(submitButton);

        add(topPanel, BorderLayout.NORTH);
        add(centerPanel, BorderLayout.CENTER);
        add(bottomPanel, BorderLayout.SOUTH);
    }

    private void updateCart() {
        StringBuilder sb = new StringBuilder();
        for (Map.Entry<String, Integer> entry : cart.entrySet()) {
            sb.append(entry.getKey()).append(" x ").append(entry.getValue()).append("\n");
        }
        cartArea.setText(sb.toString());
    }

    private List<String> getCategories() {
        List<String> categories = new ArrayList<>();
        String query = "SELECT DISTINCT category FROM products";
        try (Connection conn = DatabaseHelperProxy.getInstance().getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query);
             ResultSet rs = pstmt.executeQuery()) {
            while (rs.next()) {
                categories.add(rs.getString("category"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return categories;
    }

    private List<String> getProductsByCategory(String category) {
        List<String> products = new ArrayList<>();
        String query = "SELECT name FROM products WHERE category = ?";
        try (Connection conn = DatabaseHelperProxy.getInstance().getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setString(1, category);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    products.add(rs.getString("name"));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return products;
    }

    private void submitOrder() {
        java.util.Date now = new java.util.Date();
        int total = 0;
        StringBuilder receipt = new StringBuilder();
        receipt.append("Receipt\n");
        receipt.append("--------\n");
        for (Map.Entry<String, Integer> entry : cart.entrySet()) {
            String productName = entry.getKey();
            int quantity = entry.getValue();
            int productId = getProductIdByName(productName);
            if (productId != -1) {
                int price = getProductPriceById(productId);
                int cost = price * quantity;
                total += cost;
                receipt.append(productName).append(" x ").append(quantity).append(" @ ").append(price).append(" = ").append(cost).append("\n");
                addSale(productId, quantity, new java.sql.Date(now.getTime()));
            }
        }
        receipt.append("--------\n");
        receipt.append("Total: ").append(total).append(" EGP");

        cart.clear();
        updateCart();

        // Display the receipt in a pop-up
        JTextArea receiptArea = new JTextArea(receipt.toString());
        receiptArea.setEditable(false);
        JScrollPane scrollPane = new JScrollPane(receiptArea);
        JFrame receiptFrame = new JFrame("Receipt");
        receiptFrame.setSize(400, 300);
        receiptFrame.add(scrollPane);
        receiptFrame.setVisible(true);
    }

    private int getProductIdByName(String name) {
        String query = "SELECT id FROM products WHERE name = ?";
        try (Connection conn = DatabaseHelperProxy.getInstance().getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setString(1, name);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return rs.getInt("id");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return -1; // Return -1 if product not found
    }

    private int getProductPriceById(int id) {
        String query = "SELECT price FROM products WHERE id = ?";
        try (Connection conn = DatabaseHelperProxy.getInstance().getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setInt(1, id);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return rs.getInt("price");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0; // Return 0 if product not found
    }

    private void addSale(int productId, int quantity, java.sql.Date saleDate) {
        String query = "INSERT INTO sales (product_id, quantity, sale_date) VALUES (?, ?, ?)";
        try (Connection conn = DatabaseHelperProxy.getInstance().getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setInt(1, productId);
            pstmt.setInt(2, quantity);
            pstmt.setDate(3, saleDate);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new SalesPage().setVisible(true));
    }
}


