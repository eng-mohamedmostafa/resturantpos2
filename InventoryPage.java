package resturantpos2;

import javax.swing.*;
import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.event.*;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class InventoryPage extends JFrame {
    private JButton backButton, addButton, editButton, deleteButton, updateButton;
    private JList<String> productList;
    private DefaultListModel<String> productModel;
    private JTextField nameField, categoryField, priceField;
    private int selectedProductId = -1;

    public InventoryPage() {
        setTitle("Inventory Management");
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        productModel = new DefaultListModel<>();
        productList = new JList<>(productModel);
        updateProductList();

        backButton = new JButton("Back");
        addButton = new JButton("Add");
        editButton = new JButton("Edit");
        deleteButton = new JButton("Delete");
        updateButton = new JButton("Update");

        nameField = new JTextField(15);
        categoryField = new JTextField(15);
        priceField = new JTextField(15);

        JPanel inputPanel = new JPanel();
        inputPanel.setBorder(BorderFactory.createTitledBorder("Product Details"));
        inputPanel.setLayout(new GridLayout(3, 2, 5, 5));
        inputPanel.add(new JLabel("Name:"));
        inputPanel.add(nameField);
        inputPanel.add(new JLabel("Category:"));
        inputPanel.add(categoryField);
        inputPanel.add(new JLabel("Price:"));
        inputPanel.add(priceField);

        addButton.addActionListener(e -> addProduct());
        editButton.addActionListener(e -> loadProductForEdit());
        deleteButton.addActionListener(e -> deleteProduct());
        updateButton.addActionListener(e -> updateProduct());

        backButton.addActionListener(e -> {
            PageFactory.createPage("Dashboard").setVisible(true);
            dispose();
        });

        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new GridLayout(1, 5, 10, 0));
        buttonPanel.add(addButton);
        buttonPanel.add(editButton);
        buttonPanel.add(deleteButton);
        buttonPanel.add(updateButton);
        buttonPanel.add(backButton);

        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        mainPanel.add(inputPanel, BorderLayout.NORTH);
        mainPanel.add(new JScrollPane(productList), BorderLayout.CENTER);
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);

        add(mainPanel);
    }

    private void updateProductList() {
        productModel.clear();
        List<String> products = getProducts();
        for (String product : products) {
            productModel.addElement(product);
        }
    }

    private List<String> getProducts() {
        List<String> products = new ArrayList<>();
        String query = "SELECT name FROM products";
        try (Connection conn = DatabaseHelperProxy.getInstance().getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query);
             ResultSet rs = pstmt.executeQuery()) {

            while (rs.next()) {
                products.add(rs.getString("name"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return products;
    }

    private void addProduct() {
        String name = nameField.getText().trim();
        String category = categoryField.getText().trim();
        double price = Double.parseDouble(priceField.getText().trim());
        if (!name.isEmpty() && !category.isEmpty()) {
            String query = "INSERT INTO products (name, category, price) VALUES (?, ?, ?)";
            try (Connection conn = DatabaseHelperProxy.getInstance().getConnection();
                 PreparedStatement pstmt = conn.prepareStatement(query)) {

                pstmt.setString(1, name);
                pstmt.setString(2, category);
                pstmt.setDouble(3, price);
                pstmt.executeUpdate();
                updateProductList();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    private void loadProductForEdit() {
        String selectedProduct = productList.getSelectedValue();
        if (selectedProduct != null) {
            String query = "SELECT * FROM products WHERE name = ?";
            try (Connection conn = DatabaseHelperProxy.getInstance().getConnection();
                 PreparedStatement pstmt = conn.prepareStatement(query)) {
                pstmt.setString(1, selectedProduct);
                ResultSet rs = pstmt.executeQuery();
                if (rs.next()) {
                    selectedProductId = rs.getInt("id");
                    nameField.setText(rs.getString("name"));
                    categoryField.setText(rs.getString("category"));
                    priceField.setText(String.valueOf(rs.getDouble("price")));
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    private void updateProduct() {
        if (selectedProductId != -1) {
            String name = nameField.getText().trim();
            String category = categoryField.getText().trim();
            double price = Double.parseDouble(priceField.getText().trim());
            if (!name.isEmpty() && !category.isEmpty()) {
                String query = "UPDATE products SET name = ?, category = ?, price = ? WHERE id = ?";
                try (Connection conn = DatabaseHelperProxy.getInstance().getConnection();
                     PreparedStatement pstmt = conn.prepareStatement(query)) {
                    pstmt.setString(1, name);
                    pstmt.setString(2, category);
                    pstmt.setDouble(3, price);
                    pstmt.setInt(4, selectedProductId);
                    pstmt.executeUpdate();
                    updateProductList();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private void deleteProduct() {
        String selectedProduct = productList.getSelectedValue();
        if (selectedProduct != null) {
            int productId = getProductIdByName(selectedProduct);
            if (productId != -1) {
                try (Connection conn = DatabaseHelperProxy.getInstance().getConnection()) {
                    // Delete related sales records
                    String deleteSalesQuery = "DELETE FROM sales WHERE product_id = ?";
                    try (PreparedStatement pstmt = conn.prepareStatement(deleteSalesQuery)) {
                        pstmt.setInt(1, productId);
                        pstmt.executeUpdate();
                    }

                    // Delete product
                    String deleteProductQuery = "DELETE FROM products WHERE id = ?";
                    try (PreparedStatement pstmt = conn.prepareStatement(deleteProductQuery)) {
                        pstmt.setInt(1, productId);
                        pstmt.executeUpdate();
                    }

                    updateProductList();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        } else {
            JOptionPane.showMessageDialog(this, "Please select a product to delete.", "Error", JOptionPane.ERROR_MESSAGE);
        }
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

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new InventoryPage().setVisible(true));
    }
}

