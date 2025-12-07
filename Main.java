package SadSystem;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.DefaultCellEditor;
import javax.swing.DefaultListCellRenderer;
import java.awt.*;

public class Main {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            Login login = new Login();
            login.setVisible(true);
        });
    }
}

/*==================== Shared Components ====================*/

class GradientPanel extends JPanel {
    private final Color start;
    private final Color end;

    GradientPanel(Color start, Color end) {
        this.start = start;
        this.end = end;
        setOpaque(false);
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        int w = getWidth();
        int h = getHeight();
        GradientPaint gp = new GradientPaint(0, 0, start, w, h, end);
        g2.setPaint(gp);
        g2.fillRect(0, 0, w, h);
        g2.dispose();
        super.paintComponent(g);
    }
}

class RoundedPanel extends JPanel {
    private final int radius;

    RoundedPanel(int radius) {
        this.radius = radius;
        setOpaque(false);
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        int w = getWidth();
        int h = getHeight();
        g2.setColor(getBackground());
        g2.fillRoundRect(0, 0, w, h, radius * 2, radius * 2);
        g2.dispose();
        super.paintComponent(g);
    }
}

/*==================== Main Window ====================*/

class MainFrame extends JFrame {
    private final CardLayout cardLayout = new CardLayout();
    private final JPanel contentPanel = new JPanel(cardLayout);
    private HomePanel homePanel;
    private DashboardPanel dashboardPanel;
    private InventoryPanel inventoryPanel;

    MainFrame() {
        super("Inventory Management System");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(1280, 720);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());
        setIconImage(new ImageIcon("logo/Inventory.png").getImage());

        add(createSidebar(), BorderLayout.WEST);

        GradientPanel bg = new GradientPanel(new Color(102, 78, 255), new Color(72, 149, 239));
        bg.setLayout(new BorderLayout());
        add(bg, BorderLayout.CENTER);

        JLabel header = new JLabel("Inventory Management System");
        header.setFont(new Font("Segoe UI", Font.BOLD, 22));
        JPanel topBar = new JPanel(new BorderLayout());
        topBar.setBackground(Color.WHITE);
        topBar.setBorder(new EmptyBorder(10, 20, 10, 20));
        topBar.add(header, BorderLayout.WEST);
        bg.add(topBar, BorderLayout.NORTH);

        contentPanel.setOpaque(false);
        contentPanel.setBorder(new EmptyBorder(10, 20, 20, 20));
        bg.add(contentPanel, BorderLayout.CENTER);

        homePanel = new HomePanel();
        dashboardPanel = new DashboardPanel();
        inventoryPanel = new InventoryPanel(this);
        
        OrdersPanel ordersPanel = new OrdersPanel(this);
        
        contentPanel.add(homePanel, "HOME");
        contentPanel.add(dashboardPanel, "DASH");
        contentPanel.add(inventoryPanel, "INV");
        contentPanel.add(ordersPanel, "ORD");
        contentPanel.add(new ProfilePanel(), "PROF");
    }
    
    public void refreshHomeAndDashboard() {
        if (homePanel != null) homePanel.refresh();
        if (dashboardPanel != null) dashboardPanel.refresh();
    }

    private JPanel createSidebar() {
        JPanel side = new JPanel(new BorderLayout());
        side.setPreferredSize(new Dimension(230, 0));
        side.setBackground(new Color(20, 20, 20));
        side.setBorder(new EmptyBorder(20, 15, 20, 15));

        ImageIcon icon = new ImageIcon("logo/Inventory.png");
        Image scaled = icon.getImage().getScaledInstance(32, 32, Image.SCALE_SMOOTH);
        JLabel logo = new JLabel(new ImageIcon(scaled));
        JLabel title = new JLabel("IMS");
        title.setFont(new Font("Segoe UI", Font.BOLD, 18));
        title.setForeground(Color.WHITE);

        JPanel logoRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        logoRow.setOpaque(false);
        logoRow.add(logo);
        logoRow.add(title);
        side.add(logoRow, BorderLayout.NORTH);

        JPanel nav = new JPanel();
        nav.setOpaque(false);
        nav.setLayout(new BoxLayout(nav, BoxLayout.Y_AXIS));
        nav.setBorder(new EmptyBorder(20, 5, 0, 5));
        nav.add(createNavButton("Home", "HOME", "logo/home.png", () -> {
            if (homePanel != null) homePanel.refresh();
        }));
        nav.add(Box.createVerticalStrut(5));
        nav.add(createNavButton("Dashboard", "DASH", "logo/line-chart.png", () -> {
            if (dashboardPanel != null) dashboardPanel.refresh();
        }));
        nav.add(Box.createVerticalStrut(5));
        nav.add(createNavButton("Inventory", "INV", "logo/box.png"));
        nav.add(Box.createVerticalStrut(5));
        nav.add(createNavButton("Orders", "ORD", "logo/grocery-store (1).png"));
        nav.add(Box.createVerticalStrut(5));
        nav.add(createNavButton("Profile", "PROF", "logo/user.png"));
        side.add(nav, BorderLayout.CENTER);

        JButton logout = createNavButton("Log Out", null, "logo/log-in.png");
        logout.addActionListener(e -> {
            dispose();
            Login login = new Login();
            login.setVisible(true);
        });
        JPanel bottom = new JPanel(new BorderLayout());
        bottom.setOpaque(false);
        bottom.setBorder(new EmptyBorder(0, 10, 10, 10));
        bottom.add(logout, BorderLayout.SOUTH);
        side.add(bottom, BorderLayout.SOUTH);

        return side;
    }

    private JButton createNavButton(String text, String page) {
        return createNavButton(text, page, null, null);
    }

    private JButton createNavButton(String text, String page, String iconPath) {
        return createNavButton(text, page, iconPath, null);
    }
    
    private JButton createNavButton(String text, String page, String iconPath, Runnable onShow) {
        JButton b = new JButton(text);
        b.setFocusPainted(false);
        b.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        b.setForeground(Color.WHITE);
        b.setBackground(new Color(20, 20, 20));
        b.setBorder(BorderFactory.createEmptyBorder(10, 15, 10, 15));
        b.setHorizontalAlignment(SwingConstants.LEFT);
        b.setHorizontalTextPosition(SwingConstants.RIGHT);
        b.setIconTextGap(10);
        b.setAlignmentX(Component.LEFT_ALIGNMENT);
        b.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
        if (iconPath != null) {
            ImageIcon ic = new ImageIcon(iconPath);
            Image img = ic.getImage().getScaledInstance(18, 18, Image.SCALE_SMOOTH);
            b.setIcon(new ImageIcon(img));
        }
        if (page != null) {
            b.addActionListener(e -> {
                cardLayout.show(contentPanel, page);
                if (onShow != null) onShow.run();
            });
        }
        return b;
    }

}

/*==================== Home ====================*/
class HomePanel extends JPanel {
    private BackendService backend;
    private JPanel body;
    private JPanel row1;
    private JPanel row2;
    
    HomePanel() {
        backend = BackendService.getInstance();
        setOpaque(false);
        setLayout(new BorderLayout());

        JLabel welcome = new JLabel("Welcome");
        welcome.setFont(new Font("Segoe UI", Font.BOLD, 26));
        welcome.setBorder(new EmptyBorder(10, 0, 10, 0));
        add(welcome, BorderLayout.NORTH);

        body = new JPanel();
        body.setOpaque(false);
        body.setLayout(new BoxLayout(body, BoxLayout.Y_AXIS));

        RoundedPanel info = new RoundedPanel(24);
        info.setBackground(Color.WHITE);
        info.setBorder(new EmptyBorder(20, 30, 20, 30));
        info.setLayout(new BorderLayout());
        JLabel title = new JLabel("INVENTORY MANAGEMENT SYSTEM");
        title.setFont(new Font("Segoe UI", Font.BOLD, 18));
        JLabel desc = new JLabel("An Inventory Management System helps manage products, track orders, and monitor low stocks.");
        desc.setFont(new Font("Segoe UI", Font.PLAIN, 20));
        info.add(title, BorderLayout.NORTH);
        info.add(desc, BorderLayout.CENTER);

        row1 = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 0));
        row1.setOpaque(false);
        
        row2 = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 15));
        row2.setOpaque(false);

        body.add(info);
        body.add(Box.createVerticalStrut(15));
        body.add(row1);
        body.add(row2);
        add(body, BorderLayout.CENTER);
        
        refresh();
    }
    
    public void refresh() {
        row1.removeAll();
        row1.add(stat(String.valueOf(backend.getTotalProducts()), "Product", "logo/box.png"));
        row1.add(stat(String.valueOf(backend.getTotalOrders()), "Orders", "logo/grocery-store (1).png"));
        
        row2.removeAll();
        row2.add(stat(String.valueOf(backend.getLowStockCount()), "Low Stock", "logo/alert.png"));
        
        row1.revalidate();
        row1.repaint();
        row2.revalidate();
        row2.repaint();
    }

    private JPanel stat(String value, String label, String iconPath) {
        RoundedPanel card = new RoundedPanel(18);
        card.setBackground(new Color(240,240,240));
        card.setBorder(new EmptyBorder(10,14,10,14));
        card.setLayout(new BorderLayout(8,0));

        ImageIcon ic = new ImageIcon(iconPath);
        Image img = ic.getImage().getScaledInstance(20,20,Image.SCALE_SMOOTH);
        JLabel icon = new JLabel(new ImageIcon(img));
        card.add(icon, BorderLayout.WEST);

        JPanel texts = new JPanel();
        texts.setOpaque(false);
        texts.setLayout(new BoxLayout(texts, BoxLayout.Y_AXIS));
        JLabel v = new JLabel(value);
        v.setFont(new Font("Segoe UI", Font.BOLD, 20));
        JLabel l = new JLabel(label);
        l.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        texts.add(v);
        texts.add(l);
        card.add(texts, BorderLayout.CENTER);
        return card;
    }
}

/*==================== Dashboard ====================*/

class DashboardPanel extends JPanel {
    private BackendService backend;
    private JPanel body;
    private JPanel stats;
    private JPanel bottom;
    
    DashboardPanel() {
        backend = BackendService.getInstance();
        setOpaque(false);
        setLayout(new BorderLayout(0, 15));

        JLabel heading = new JLabel("Dashboard");
        heading.setFont(new Font("Segoe UI", Font.BOLD, 26));
        heading.setBorder(new EmptyBorder(0, 0, 10, 0));
        add(heading, BorderLayout.NORTH);

        body = new JPanel();
        body.setOpaque(false);
        body.setLayout(new BoxLayout(body, BoxLayout.Y_AXIS));

        stats = new JPanel(new GridLayout(2, 2, 15, 15));
        stats.setOpaque(false);
        
        bottom = new JPanel(new GridLayout(1, 2, 15, 0));
        bottom.setOpaque(false);
        
        body.add(stats);
        body.add(Box.createVerticalStrut(20));
        body.add(bottom);

        add(body, BorderLayout.CENTER);
        
        refresh();
    }
    
    public void refresh() {
        stats.removeAll();
        stats.add(statCard(String.valueOf(backend.getTotalProducts()), "Total Products", new Color(51, 102, 255), "logo/box.png"));
        stats.add(statCard(String.valueOf(backend.getTotalInStock()), "In Stocks", new Color(0, 170, 0), "logo/check-mark.png"));
        stats.add(statCard(String.valueOf(backend.getTotalOrders()), "Total Orders", new Color(255, 193, 7), "logo/grocery-store (1).png"));
        stats.add(statCard(String.valueOf(backend.getLowStockCount()), "Low Stocks", new Color(220, 53, 69), "logo/alert.png"));
        
        bottom.removeAll();
        bottom.add(recentOrdersCard());
        bottom.add(lowStocksCard());
        
        stats.revalidate();
        stats.repaint();
        bottom.revalidate();
        bottom.repaint();
    }

    /* === helper components === */
    private RoundedPanel statCard(String value, String label, Color color, String iconPath) {
        RoundedPanel card = new RoundedPanel(18);
        card.setBackground(Color.WHITE);
        card.setBorder(new EmptyBorder(12, 16, 12, 16));
        card.setLayout(new BorderLayout(10, 0));

        int icoSize = 39;
        JPanel circle = new JPanel(){
            protected void paintComponent(Graphics g){
                Graphics2D g2=(Graphics2D)g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(color);
                g2.fillOval(0,0,icoSize,icoSize);
                g2.dispose();
            }
        };
        circle.setPreferredSize(new Dimension(icoSize,icoSize));
        circle.setOpaque(false);
        ImageIcon ic = new ImageIcon(iconPath);
        Image img = ic.getImage().getScaledInstance(16,16,Image.SCALE_SMOOTH);
        JLabel iconLab = new JLabel(new ImageIcon(img));
        iconLab.setPreferredSize(new Dimension(icoSize,icoSize));
        circle.setLayout(new BorderLayout());
        circle.add(iconLab, BorderLayout.CENTER);
        card.add(circle, BorderLayout.WEST);

        JPanel texts = new JPanel();
        texts.setOpaque(false);
        texts.setLayout(new BoxLayout(texts, BoxLayout.Y_AXIS));
        JLabel labText = new JLabel(label);
        labText.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        JLabel val = new JLabel(value);
        val.setFont(new Font("Segoe UI", Font.BOLD, 20));
        texts.add(labText);
        texts.add(val);
        card.add(texts, BorderLayout.CENTER);
        return card;
    }

    private RoundedPanel recentOrdersCard() {
        RoundedPanel card = new RoundedPanel(24);
        card.setBackground(Color.WHITE);
        card.setBorder(new EmptyBorder(10, 10, 10, 10));
        card.setLayout(new BorderLayout(0, 10));

        JLabel title = new JLabel("Recent Orders");
        title.setFont(new Font("Segoe UI", Font.BOLD, 14));
        card.add(title, BorderLayout.NORTH);

        String[] cols = {"Order ID", "Customer", "Status"};
        DefaultTableModel model = new DefaultTableModel(cols, 0);
        java.util.List<BackendService.Order> recentOrders = backend.getRecentOrders(5);
        for (BackendService.Order order : recentOrders) {
            model.addRow(new Object[]{
                "#" + String.format("%02d", order.getId()),
                order.getCustomerName(),
                order.getStatus().getDisplayName()
            });
        }
        JTable t = new JTable(model);
        t.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        t.setRowHeight(22);
        t.setEnabled(false);
        t.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 12));
        card.add(new JScrollPane(t), BorderLayout.CENTER);
        return card;
    }

    private RoundedPanel lowStocksCard() {
        RoundedPanel card = new RoundedPanel(24);
        card.setBackground(Color.WHITE);
        card.setBorder(new EmptyBorder(10, 10, 10, 10));
        card.setLayout(new BorderLayout(0, 10));

        JLabel title = new JLabel("Low Stocks Alert");
        title.setFont(new Font("Segoe UI", Font.BOLD, 14));
        card.add(title, BorderLayout.NORTH);

        DefaultListModel<String> m = new DefaultListModel<>();
        java.util.List<BackendService.Product> lowStockProducts = backend.getLowStockProducts();
        for (BackendService.Product product : lowStockProducts) {
            m.addElement(product.getName() + " - Only " + product.getQuantity() + " units left");
        }
        if (m.isEmpty()) {
            m.addElement("No low stock items");
        }
        JList<String> list = new JList<>(m);
        list.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        card.add(new JScrollPane(list), BorderLayout.CENTER);
        return card;
    }
}

/*==================== Inventory ====================*/

class InventoryPanel extends JPanel {
    private BackendService backend;
    private JPanel productsPanel;
    private JTextField searchField;
    private MainFrame mainFrame;
    
    InventoryPanel(MainFrame mainFrame) {
        this.mainFrame = mainFrame;
        backend = BackendService.getInstance();
        setOpaque(false);
        setLayout(new BorderLayout(0, 10));

        JLabel title = new JLabel("Inventory");
        title.setFont(new Font("Segoe UI", Font.BOLD, 24));

        JPanel header = new JPanel(new BorderLayout(10, 0));
        header.setOpaque(false);
        header.add(title, BorderLayout.WEST);

        JButton addBtn = new JButton("+ Add Product");
        stylePrimary(addBtn);
        addBtn.addActionListener(e -> showAddProductDialog());
        JPanel right = new JPanel(new FlowLayout(FlowLayout.RIGHT, 0, 0));
        right.setOpaque(false);
        right.add(addBtn);
        header.add(right, BorderLayout.EAST);

        searchField = new RoundedTextField("Search Products...", 20);
        searchField.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        searchField.addActionListener(e -> refreshProducts());
        searchField.getDocument().addDocumentListener(new javax.swing.event.DocumentListener() {
            public void changedUpdate(javax.swing.event.DocumentEvent e) { refreshProducts(); }
            public void removeUpdate(javax.swing.event.DocumentEvent e) { refreshProducts(); }
            public void insertUpdate(javax.swing.event.DocumentEvent e) { refreshProducts(); }
        });
        JPanel searchP = new JPanel(new BorderLayout());
        searchP.setOpaque(false);
        searchP.add(searchField, BorderLayout.CENTER);

        JPanel top = new JPanel();
        top.setOpaque(false);
        top.setLayout(new BoxLayout(top, BoxLayout.Y_AXIS));
        top.add(header);
        top.add(Box.createVerticalStrut(10));
        top.add(searchP);
        top.setBorder(new EmptyBorder(0, 0, 10, 0));
        add(top, BorderLayout.NORTH);

        productsPanel = new JPanel();
        productsPanel.setOpaque(false);
        productsPanel.setLayout(new BoxLayout(productsPanel, BoxLayout.Y_AXIS));
        
        JScrollPane scrollPane = new JScrollPane(productsPanel);
        scrollPane.setOpaque(false);
        scrollPane.getViewport().setOpaque(false);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        
        refreshProducts();
        add(scrollPane, BorderLayout.CENTER);
    }

    private void refreshProducts() {
        productsPanel.removeAll();
        String query = searchField.getText().trim();
        if (query.equals("Search Products...")) query = "";
        
        java.util.List<BackendService.Product> products;
        if (query.isEmpty()) {
            products = backend.getAllProducts();
        } else {
            products = backend.searchProducts(query);
        }
        
        if (products.isEmpty()) {
            JLabel empty = new JLabel("No products found");
            empty.setFont(new Font("Segoe UI", Font.PLAIN, 14));
            empty.setForeground(new Color(150, 150, 150));
            empty.setAlignmentX(CENTER_ALIGNMENT);
            productsPanel.add(Box.createVerticalGlue());
            productsPanel.add(empty);
            productsPanel.add(Box.createVerticalGlue());
        } else {
            JPanel grid = new JPanel(new GridLayout(0, 2, 15, 15));
            grid.setOpaque(false);
            for (BackendService.Product product : products) {
                grid.add(createProductCard(product));
            }
            productsPanel.add(grid);
        }
        productsPanel.revalidate();
        productsPanel.repaint();
    }

    private RoundedPanel createProductCard(BackendService.Product product) {
        RoundedPanel c = new RoundedPanel(24);
        c.setBackground(Color.WHITE);
        c.setBorder(new EmptyBorder(18, 18, 18, 18));
        c.setLayout(new BorderLayout());

        JPanel statusP = new JPanel(new FlowLayout(FlowLayout.RIGHT, 0, 0));
        statusP.setOpaque(false);
        boolean inStock = !product.isLowStock();
        JLabel status = new JLabel(inStock ? "In Stock" : "Low Stock");
        status.setOpaque(true);
        status.setBackground(inStock ? new Color(0, 180, 80) : new Color(230, 70, 70));
        status.setForeground(Color.WHITE);
        status.setBorder(BorderFactory.createEmptyBorder(3, 10, 3, 10));
        statusP.add(status);

        JPanel info = new JPanel();
        info.setOpaque(false);
        info.setLayout(new BoxLayout(info, BoxLayout.Y_AXIS));
        JLabel nameL = new JLabel(product.getName());
        nameL.setFont(new Font("Segoe UI", Font.BOLD, 14));
        JLabel priceL = new JLabel("Price: ₱" + String.format("%,.2f", product.getPrice()));
        priceL.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        JLabel stockL = new JLabel("Stock: " + product.getQuantity());
        stockL.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        info.add(nameL);
        info.add(Box.createVerticalStrut(5));
        info.add(priceL);
        info.add(stockL);

        JPanel actions = new JPanel(new FlowLayout(FlowLayout.CENTER, 8, 0));
        actions.setOpaque(false);
        JButton editBtn = flatIconButton(new PencilIcon(16, 16));
        editBtn.addActionListener(e -> showEditProductDialog(product));
        JButton delBtn = flatIconButton(new TrashIcon(16, 16));
        delBtn.addActionListener(e -> deleteProduct(product));
        actions.add(editBtn);
        actions.add(delBtn);

        c.add(statusP, BorderLayout.NORTH);
        c.add(info, BorderLayout.CENTER);
        c.add(actions, BorderLayout.SOUTH);
        return c;
    }

    private void showAddProductDialog() {
        JDialog dialog = new JDialog((JFrame) SwingUtilities.getWindowAncestor(this), "Add new product", true);
        dialog.setSize(450, 300);
        dialog.setLocationRelativeTo(this);
        dialog.setLayout(new BorderLayout(20, 20));
        dialog.getContentPane().setBackground(Color.WHITE);
        dialog.setResizable(false);

        JPanel content = new JPanel();
        content.setBackground(Color.WHITE);
        content.setLayout(new BoxLayout(content, BoxLayout.Y_AXIS));
        content.setBorder(new EmptyBorder(20, 30, 20, 30));

        JLabel title = new JLabel("Add new product");
        title.setFont(new Font("Segoe UI", Font.BOLD, 20));
        title.setBorder(new EmptyBorder(0, 0, 20, 0));
        content.add(title);

        JTextField nameField = new JTextField();
        content.add(createFormField("Product Name:", nameField));

        JTextField quantityField = new JTextField();
        content.add(createFormField("Quantity:", quantityField));

        JPanel pricePanel = new JPanel(new BorderLayout(5, 0));
        pricePanel.setBackground(Color.WHITE);
        pricePanel.setBorder(new EmptyBorder(0, 0, 10, 0));
        JLabel priceLabel = new JLabel("Price:");
        priceLabel.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        pricePanel.add(priceLabel, BorderLayout.WEST);
        JPanel priceInputPanel = new JPanel(new BorderLayout());
        priceInputPanel.setBackground(Color.WHITE);
        JLabel pesoLabel = new JLabel("₱");
        pesoLabel.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        pesoLabel.setBorder(new EmptyBorder(0, 0, 0, 5));
        JTextField priceField = new JTextField();
        priceInputPanel.add(pesoLabel, BorderLayout.WEST);
        priceInputPanel.add(priceField, BorderLayout.CENTER);
        pricePanel.add(priceInputPanel, BorderLayout.CENTER);
        content.add(pricePanel);

        JButton addBtn = new JButton("+Add Product");
        stylePrimary(addBtn);
        addBtn.addActionListener(e -> {
            try {
                String name = nameField.getText().trim();
                int quantity = Integer.parseInt(quantityField.getText().trim());
                double price = Double.parseDouble(priceField.getText().trim());
                
                if (name.isEmpty()) {
                    JOptionPane.showMessageDialog(dialog, "Product name cannot be empty.", "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }
                
                if (backend.addProduct(name, price, quantity)) {
                    refreshProducts();
                    if (mainFrame != null) mainFrame.refreshHomeAndDashboard();
                    dialog.dispose();
                } else {
                    JOptionPane.showMessageDialog(dialog, "Failed to add product. Product name may already exist.", "Error", JOptionPane.ERROR_MESSAGE);
                }
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(dialog, "Please enter valid numbers for quantity and price.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        });
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttonPanel.setBackground(Color.WHITE);
        buttonPanel.add(addBtn);
        content.add(buttonPanel);

        dialog.add(content, BorderLayout.CENTER);
        dialog.setVisible(true);
    }

    private void showEditProductDialog(BackendService.Product product) {
        JDialog dialog = new JDialog((JFrame) SwingUtilities.getWindowAncestor(this), "Edit Product", true);
        dialog.setSize(450, 300);
        dialog.setLocationRelativeTo(this);
        dialog.setLayout(new BorderLayout(20, 20));
        dialog.getContentPane().setBackground(Color.WHITE);
        dialog.setResizable(false);

        JPanel content = new JPanel();
        content.setBackground(Color.WHITE);
        content.setLayout(new BoxLayout(content, BoxLayout.Y_AXIS));
        content.setBorder(new EmptyBorder(20, 30, 20, 30));

        JLabel title = new JLabel("Edit Product");
        title.setFont(new Font("Segoe UI", Font.BOLD, 20));
        title.setBorder(new EmptyBorder(0, 0, 20, 0));
        content.add(title);

        JTextField nameField = new JTextField(product.getName());
        content.add(createFormField("Product Name:", nameField));

        JTextField quantityField = new JTextField(String.valueOf(product.getQuantity()));
        content.add(createFormField("Quantity:", quantityField));

        JPanel pricePanel = new JPanel(new BorderLayout(5, 0));
        pricePanel.setBackground(Color.WHITE);
        pricePanel.setBorder(new EmptyBorder(0, 0, 10, 0));
        JLabel priceLabel = new JLabel("Price:");
        priceLabel.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        pricePanel.add(priceLabel, BorderLayout.WEST);
        JPanel priceInputPanel = new JPanel(new BorderLayout());
        priceInputPanel.setBackground(Color.WHITE);
        JLabel pesoLabel = new JLabel("₱");
        pesoLabel.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        pesoLabel.setBorder(new EmptyBorder(0, 0, 0, 5));
        JTextField priceField = new JTextField(String.valueOf(product.getPrice()));
        priceInputPanel.add(pesoLabel, BorderLayout.WEST);
        priceInputPanel.add(priceField, BorderLayout.CENTER);
        pricePanel.add(priceInputPanel, BorderLayout.CENTER);
        content.add(pricePanel);

        JButton saveBtn = new JButton("Save");
        stylePrimary(saveBtn);
        saveBtn.addActionListener(e -> {
            try {
                String name = nameField.getText().trim();
                int quantity = Integer.parseInt(quantityField.getText().trim());
                double price = Double.parseDouble(priceField.getText().trim());
                
                if (name.isEmpty()) {
                    JOptionPane.showMessageDialog(dialog, "Product name cannot be empty.", "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }
                
                if (backend.updateProduct(product.getId(), name, price, quantity)) {
                    refreshProducts();
                    if (mainFrame != null) mainFrame.refreshHomeAndDashboard();
                    dialog.dispose();
                } else {
                    JOptionPane.showMessageDialog(dialog, "Failed to update product.", "Error", JOptionPane.ERROR_MESSAGE);
                }
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(dialog, "Please enter valid numbers for quantity and price.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        });
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttonPanel.setBackground(Color.WHITE);
        buttonPanel.add(saveBtn);
        content.add(buttonPanel);

        dialog.add(content, BorderLayout.CENTER);
        dialog.setVisible(true);
    }

    private void deleteProduct(BackendService.Product product) {
        int confirm = JOptionPane.showConfirmDialog(this, 
            "Are you sure you want to delete \"" + product.getName() + "\"?", 
            "Confirm Delete", 
            JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            if (backend.deleteProduct(product.getId())) {
                refreshProducts();
                if (mainFrame != null) mainFrame.refreshHomeAndDashboard();
            } else {
                JOptionPane.showMessageDialog(this, 
                    "Cannot delete product. It may be used in existing orders.", 
                    "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private JPanel createFormField(String label, JTextField field) {
        JPanel panel = new JPanel(new BorderLayout(0, 5));
        panel.setBackground(Color.WHITE);
        panel.setBorder(new EmptyBorder(0, 0, 10, 0));
        JLabel lab = new JLabel(label);
        lab.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        panel.add(lab, BorderLayout.NORTH);
        field.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        panel.add(field, BorderLayout.CENTER);
        return panel;
    }

    private void stylePrimary(JButton b) {
        b.setBackground(new Color(51, 102, 255));
        b.setForeground(Color.WHITE);
        b.setFocusPainted(false);
        b.setFont(new Font("Segoe UI", Font.BOLD, 13));
        b.setBorder(BorderFactory.createEmptyBorder(8, 18, 8, 18));
    }

    private JButton flatIconButton(Icon icon) {
        JButton b = new JButton(icon);
        b.setContentAreaFilled(false);
        b.setFocusPainted(false);
        b.setBorderPainted(false);
        return b;
    }

    private static class PencilIcon implements Icon {
        private final int w, h;
        PencilIcon(int w, int h) {this.w=w; this.h=h;}
        public int getIconWidth(){return w;}
        public int getIconHeight(){return h;}
        public void paintIcon(Component c, Graphics g, int x, int y){
            Graphics2D g2=(Graphics2D)g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(Color.BLACK);
            int[] xs={x+0,x+w-4,x+w-1,x+3};
            int[] ys={y+h-3,y+0,y+3,y+h};
            g2.fillPolygon(xs,ys,4);
            g2.setColor(Color.WHITE);
            g2.drawLine(x+2,y+h-4,x+w-4,y+2);
            g2.dispose();}
    }
    private static class TrashIcon implements Icon {
        private final int w,h; TrashIcon(int w,int h){this.w=w;this.h=h;}
        public int getIconWidth(){return w;}
        public int getIconHeight(){return h;}
        public void paintIcon(Component c, Graphics g,int x,int y){
            Graphics2D g2=(Graphics2D)g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(Color.BLACK);
            g2.fillRect(x+3,y+4,w-6,h-5);
            g2.fillRect(x+4,y+2,w-8,2);
            g2.dispose();}
    }
}

/*==================== Orders ====================*/

class OrdersPanel extends JPanel {
    private BackendService backend;
    private DefaultTableModel tableModel;
    private JTable table;
    private JTextField searchField;
    private MainFrame mainFrame;
    
    OrdersPanel(MainFrame mainFrame) {
        this.mainFrame = mainFrame;
        backend = BackendService.getInstance();
        setOpaque(false);
        setLayout(new BorderLayout(0, 10));

        JLabel title = new JLabel("Orders");
        title.setFont(new Font("Segoe UI", Font.BOLD, 24));

        JPanel header = new JPanel(new BorderLayout(10, 0));
        header.setOpaque(false);
        header.add(title, BorderLayout.WEST);

        JButton newBtn = new JButton("+ New Order");
        stylePrimary(newBtn);
        newBtn.addActionListener(e -> showNewOrderDialog());
        JPanel right = new JPanel(new FlowLayout(FlowLayout.RIGHT, 0, 0));
        right.setOpaque(false);
        right.add(newBtn);
        header.add(right, BorderLayout.EAST);

        searchField = new RoundedTextField("Search Products...", 20);
        searchField.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        searchField.addActionListener(e -> refreshOrders());
        searchField.getDocument().addDocumentListener(new javax.swing.event.DocumentListener() {
            public void changedUpdate(javax.swing.event.DocumentEvent e) { refreshOrders(); }
            public void removeUpdate(javax.swing.event.DocumentEvent e) { refreshOrders(); }
            public void insertUpdate(javax.swing.event.DocumentEvent e) { refreshOrders(); }
        });
        JPanel searchP = new JPanel(new BorderLayout());
        searchP.setOpaque(false);
        searchP.add(searchField, BorderLayout.CENTER);

        JPanel top = new JPanel();
        top.setOpaque(false);
        top.setLayout(new BoxLayout(top, BoxLayout.Y_AXIS));
        top.add(header);
        top.add(Box.createVerticalStrut(10));
        top.add(searchP);
        top.setBorder(new EmptyBorder(0, 0, 10, 0));
        add(top, BorderLayout.NORTH);

        String[] cols = {"Order ID", "Customer", "Date", "Items", "Total", "Status", "Actions"};
        tableModel = new DefaultTableModel(cols, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return column == 6;
            }
        };

        table = new JTable(tableModel);
        table.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        table.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 13));
        table.setRowHeight(28);
        table.getColumn("Actions").setCellRenderer(new ActionsRenderer());
        table.getColumn("Actions").setCellEditor(new ActionsEditor(new JCheckBox()));

        JScrollPane sp = new JScrollPane(table);

        RoundedPanel card = new RoundedPanel(24);
        card.setBackground(Color.WHITE);
        card.setBorder(new EmptyBorder(10, 10, 10, 10));
        card.setLayout(new BorderLayout());
        card.add(sp, BorderLayout.CENTER);

        add(card, BorderLayout.CENTER);
        refreshOrders();
    }

    private void refreshOrders() {
        tableModel.setRowCount(0);
        String query = searchField.getText().trim();
        if (query.equals("Search Products...")) query = "";
        
        java.util.List<BackendService.Order> orders;
        if (query.isEmpty()) {
            orders = backend.getAllOrders();
        } else {
            orders = backend.searchOrders(query);
        }
        
        java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("yyyy-MM-dd");
        for (BackendService.Order order : orders) {
            Object[] row = {
                "#" + String.format("%02d", order.getId()),
                order.getCustomerName(),
                sdf.format(order.getDate()),
                order.getTotalItems() + " items",
                "₱" + String.format("%,.2f", order.getTotal()),
                order.getStatus().getDisplayName(),
                order.getId() // Store order ID for button actions
            };
            tableModel.addRow(row);
        }
    }

    private void showNewOrderDialog() {
        JDialog dialog = new JDialog((JFrame) SwingUtilities.getWindowAncestor(this), "Make new order", true);
        dialog.setSize(600, 400);
        dialog.setLocationRelativeTo(this);
        dialog.setLayout(new BorderLayout(20, 20));
        dialog.getContentPane().setBackground(Color.WHITE);
        dialog.setResizable(false);

        JPanel content = new JPanel();
        content.setBackground(Color.WHITE);
        content.setLayout(new BorderLayout(10, 10));
        content.setBorder(new EmptyBorder(20, 20, 20, 20));

        JLabel title = new JLabel("Make new order");
        title.setFont(new Font("Segoe UI", Font.BOLD, 18));
        content.add(title, BorderLayout.NORTH);

        String[] cols = {"#", "Item Name", "Quantity", "Total:"};
        DefaultTableModel orderModel = new DefaultTableModel(cols, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return column == 1 || column == 2;
            }
        };
        JTable orderTable = new JTable(orderModel);
        orderTable.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        orderTable.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 12));
        orderTable.setRowHeight(25);
        
        orderTable.getColumn("Item Name").setCellEditor(new ProductComboBoxEditor());
        orderTable.getColumn("Item Name").setCellRenderer(new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                if (value instanceof BackendService.Product) {
                    BackendService.Product p = (BackendService.Product) value;
                    setText(p.getName());
                } else if (value == null) {
                    setText("");
                } else {
                    setText(value.toString());
                }
                return this;
            }
        });
        orderTable.getColumn("Quantity").setCellEditor(new QuantityCellEditor());
        
        JScrollPane tableScroll = new JScrollPane(orderTable);
        content.add(tableScroll, BorderLayout.CENTER);

        JPanel bottomPanel = new JPanel();
        bottomPanel.setBackground(Color.WHITE);
        bottomPanel.setLayout(new BoxLayout(bottomPanel, BoxLayout.Y_AXIS));
        
        JPanel customerPanel = new JPanel(new BorderLayout(5, 0));
        customerPanel.setBackground(Color.WHITE);
        customerPanel.setBorder(new EmptyBorder(10, 0, 10, 0));
        JLabel customerLabel = new JLabel("Customer Name:");
        customerLabel.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        JTextField customerField = new JTextField();
        customerPanel.add(customerLabel, BorderLayout.WEST);
        customerPanel.add(customerField, BorderLayout.CENTER);
        bottomPanel.add(customerPanel);
        
        JLabel totalLabel = new JLabel("Total: ₱0");
        totalLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        totalLabel.setBorder(new EmptyBorder(0, 0, 10, 0));
        bottomPanel.add(totalLabel);

        final boolean[] isUpdating = {false};

        orderModel.addTableModelListener(e -> {
            if (isUpdating[0]) return;

            try {
                isUpdating[0] = true;
                double total = 0;
                for (int i = 0; i < orderModel.getRowCount(); i++) {
                    Object itemObj = orderModel.getValueAt(i, 1);
                    Object qtyObj = orderModel.getValueAt(i, 2);
                    if (itemObj != null && qtyObj != null) {
                        try {
                            BackendService.Product product = (BackendService.Product) itemObj;
                            int qty = Integer.parseInt(qtyObj.toString());
                            double lineTotal = product.getPrice() * qty;
                            total += lineTotal;

                            Object current = orderModel.getValueAt(i, 3);
                            String newValue = "₱" + String.format("%,.2f", lineTotal);
                            if (!(current instanceof String) || !newValue.equals(current)) {
                                orderModel.setValueAt(newValue, i, 3);
                            }
                        } catch (Exception ex) {
                        }
                    }
                }
                totalLabel.setText("Total: ₱" + String.format("%,.2f", total));
            } finally {
                isUpdating[0] = false;
            }
        });
        
        JButton addOrderBtn = new JButton("+Add Order");
        stylePrimary(addOrderBtn);
        addOrderBtn.addActionListener(e -> {
            System.out.println("Add Order button clicked");
            
            if (orderTable.isEditing()) {
                orderTable.getCellEditor().stopCellEditing();
            }
            
            String customerName = customerField.getText().trim();
            if (customerName.isEmpty()) {
                JOptionPane.showMessageDialog(dialog, "Please enter customer name.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            boolean hasValidItem = false;
            for (int i = 0; i < orderModel.getRowCount(); i++) {
                Object itemObj = orderModel.getValueAt(i, 1);
                Object qtyObj = orderModel.getValueAt(i, 2);
                
                if (itemObj instanceof BackendService.Product && qtyObj != null) {
                    try {
                        int qty = Integer.parseInt(qtyObj.toString().trim());
                        if (qty > 0) {
                            hasValidItem = true;
                            break;
                        }
                    } catch (NumberFormatException ex) {
                    }
                }
            }
            
            if (!hasValidItem) {
                JOptionPane.showMessageDialog(dialog, "Please select a product and enter a valid quantity.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            java.util.List<BackendService.OrderItem> items = new java.util.ArrayList<>();
            for (int i = 0; i < orderModel.getRowCount(); i++) {
                Object itemObj = orderModel.getValueAt(i, 1);
                Object qtyObj = orderModel.getValueAt(i, 2);
                
                if (itemObj instanceof BackendService.Product && qtyObj != null) {
                    try {
                        BackendService.Product product = (BackendService.Product) itemObj;
                        int qty = Integer.parseInt(qtyObj.toString().trim());
                        if (qty > 0) {
                            items.add(new BackendService.OrderItem(product.getId(), qty));
                        }
                    } catch (NumberFormatException ex) {
                        // Skip invalid quantity
                    }
                }
            }
            
            if (items.isEmpty()) {
                JOptionPane.showMessageDialog(dialog, "No valid items in order.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            try {
                BackendService.Order order = backend.createOrder(customerName, items);
                if (order != null) {
                    JOptionPane.showMessageDialog(dialog, "Order created successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
                    dialog.dispose();
                    refreshOrders(); 
                    if (mainFrame != null) {
                        mainFrame.refreshHomeAndDashboard();
                    }
                } else {
                    JOptionPane.showMessageDialog(dialog, "Failed to create order. Please check stock availability.", "Error", JOptionPane.ERROR_MESSAGE);
                }
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(dialog, "Error creating order: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                ex.printStackTrace();
            }
        });
        
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        buttonPanel.setBackground(Color.WHITE);
        buttonPanel.add(addOrderBtn);
        bottomPanel.add(buttonPanel);
        
        content.add(bottomPanel, BorderLayout.SOUTH);
        
        orderModel.addRow(new Object[]{0, null, "", "₱0"});
        
        dialog.add(content, BorderLayout.CENTER);
        dialog.setVisible(true);
    }

    private JComboBox<BackendService.Product> createProductComboBox() {
        JComboBox<BackendService.Product> combo = new JComboBox<>();
        combo.setRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
                super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                if (value instanceof BackendService.Product) {
                    BackendService.Product p = (BackendService.Product) value;
                    setText(p.getName() + " (₱" + String.format("%,.2f", p.getPrice()) + ")");
                }
                return this;
            }
        });
        for (BackendService.Product product : backend.getAllProducts()) {
            combo.addItem(product);
        }
        return combo;
    }
    
    class ProductComboBoxEditor extends DefaultCellEditor {
        private JComboBox<BackendService.Product> combo;
        
        public ProductComboBoxEditor() {
            super(new JComboBox<BackendService.Product>());
            setClickCountToStart(1);
        }
        
        @Override
        public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column) {
            combo = createProductComboBox();
            if (value instanceof BackendService.Product) {
                combo.setSelectedItem(value);
            }
            combo.setEditable(false);
            for (java.awt.event.ActionListener al : combo.getActionListeners()) {
                combo.removeActionListener(al);
            }
            combo.addActionListener(e -> {
                SwingUtilities.invokeLater(() -> {
                    fireEditingStopped();
                });
            });
            return combo;
        }
        
        @Override
        public Object getCellEditorValue() {
            if (combo != null && combo.getSelectedItem() != null) {
                return combo.getSelectedItem();
            }
            return null;
        }
        
        @Override
        public boolean stopCellEditing() {
            if (combo != null) {
                Object selected = combo.getSelectedItem();
                if (selected != null) {
                    return super.stopCellEditing();
                }
            }
            return super.stopCellEditing();
        }
    }
    
    class QuantityCellEditor extends DefaultCellEditor {
        private JTextField textField;
        
        public QuantityCellEditor() {
            super(new JTextField());
            setClickCountToStart(1);
            textField = (JTextField) getComponent();
        }
        
        @Override
        public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column) {
            Component comp = super.getTableCellEditorComponent(table, value, isSelected, row, column);
            textField = (JTextField) comp;
            if (value != null) {
                textField.setText(value.toString());
            } else {
                textField.setText("");
            }
            textField.selectAll();
            
            textField.addKeyListener(new java.awt.event.KeyAdapter() {
                @Override
                public void keyPressed(java.awt.event.KeyEvent e) {
                    if (e.getKeyCode() == java.awt.event.KeyEvent.VK_ENTER) {
                        stopCellEditing();
                        int nextRow = row + 1;
                        if (nextRow < table.getRowCount()) {
                            table.editCellAt(nextRow, 1);
                        } else {
                            table.editCellAt(0, 1);
                        }
                    }
                }
            });
            
            return comp;
        }
        
        @Override
        public Object getCellEditorValue() {
            if (textField != null) {
                return textField.getText().trim();
            }
            return "";
        }
        
        @Override
        public boolean stopCellEditing() {
            if (textField != null) {
                String text = textField.getText().trim();
                try {
                    if (!text.isEmpty()) {
                        Integer.parseInt(text);
                    }
                } catch (NumberFormatException e) {
                    if (!text.isEmpty()) {
                        JOptionPane.showMessageDialog(textField, 
                            "Please enter a valid number for quantity", 
                            "Invalid Input", JOptionPane.ERROR_MESSAGE);
                        return false;
                    }
                }
            }
            return super.stopCellEditing();
        }
    }

    private void stylePrimary(JButton b) {
        b.setBackground(new Color(51, 102, 255));
        b.setForeground(Color.WHITE);
        b.setFocusPainted(false);
        b.setFont(new Font("Segoe UI", Font.BOLD, 13));
        b.setBorder(BorderFactory.createEmptyBorder(8, 18, 8, 18));
    }

    // Button renderer and editor for Actions column
    class ActionsRenderer extends JPanel implements javax.swing.table.TableCellRenderer {
        private JButton editBtn;
        private JButton deleteBtn;
        
        public ActionsRenderer() {
            setLayout(new FlowLayout(FlowLayout.CENTER, 5, 0));
            setOpaque(true);
            
            editBtn = new JButton("Edit");
            editBtn.setBackground(new Color(51, 102, 255));
            editBtn.setForeground(Color.WHITE);
            editBtn.setFont(new Font("Segoe UI", Font.PLAIN, 11));
            editBtn.setFocusPainted(false);
            editBtn.setBorderPainted(false);
            editBtn.setPreferredSize(new Dimension(60, 22));
            
            deleteBtn = new JButton("Delete");
            deleteBtn.setBackground(new Color(220, 53, 69));
            deleteBtn.setForeground(Color.WHITE);
            deleteBtn.setFont(new Font("Segoe UI", Font.PLAIN, 11));
            deleteBtn.setFocusPainted(false);
            deleteBtn.setBorderPainted(false);
            deleteBtn.setPreferredSize(new Dimension(60, 22));
            
            add(editBtn);
            add(deleteBtn);
        }
        
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
            if (isSelected) {
                setBackground(table.getSelectionBackground());
            } else {
                setBackground(table.getBackground());
            }
            return this;
        }
    }

    class ActionsEditor extends DefaultCellEditor {
        private JPanel panel;
        private JButton editBtn;
        private JButton deleteBtn;
        private int orderId;

        public ActionsEditor(JCheckBox checkBox) {
            super(checkBox);
            panel = new JPanel(new FlowLayout(FlowLayout.CENTER, 5, 0));
            panel.setOpaque(true);
            
            editBtn = new JButton("Edit");
            editBtn.setBackground(new Color(51, 102, 255));
            editBtn.setForeground(Color.WHITE);
            editBtn.setFont(new Font("Segoe UI", Font.PLAIN, 11));
            editBtn.setFocusPainted(false);
            editBtn.setBorderPainted(false);
            editBtn.setPreferredSize(new Dimension(60, 22));
            editBtn.addActionListener(e -> {
                showEditStatusDialog(orderId);
                fireEditingStopped();
            });
            
            deleteBtn = new JButton("Delete");
            deleteBtn.setBackground(new Color(220, 53, 69));
            deleteBtn.setForeground(Color.WHITE);
            deleteBtn.setFont(new Font("Segoe UI", Font.PLAIN, 11));
            deleteBtn.setFocusPainted(false);
            deleteBtn.setBorderPainted(false);
            deleteBtn.setPreferredSize(new Dimension(60, 22));
            deleteBtn.addActionListener(e -> {
                deleteOrder(orderId);
                fireEditingStopped();
            });
            
            panel.add(editBtn);
            panel.add(deleteBtn);
        }

        public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column) {
            orderId = (Integer) value;
            if (isSelected) {
                panel.setBackground(table.getSelectionBackground());
            } else {
                panel.setBackground(table.getBackground());
            }
            return panel;
        }

        public Object getCellEditorValue() {
            return orderId;
        }
    }
    
    private void showEditStatusDialog(int orderId) {
        BackendService.Order order = backend.getOrderById(orderId);
        if (order == null) {
            JOptionPane.showMessageDialog(this, "Order not found.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        JDialog dialog = new JDialog((JFrame) SwingUtilities.getWindowAncestor(this), "Edit Order Status", true);
        dialog.setSize(400, 250);
        dialog.setLocationRelativeTo(this);
        dialog.setLayout(new BorderLayout(20, 20));
        dialog.getContentPane().setBackground(Color.WHITE);
        dialog.setResizable(false);

        JPanel content = new JPanel();
        content.setBackground(Color.WHITE);
        content.setLayout(new BoxLayout(content, BoxLayout.Y_AXIS));
        content.setBorder(new EmptyBorder(20, 30, 20, 30));

        JLabel title = new JLabel("Edit Order Status");
        title.setFont(new Font("Segoe UI", Font.BOLD, 18));
        title.setBorder(new EmptyBorder(0, 0, 20, 0));
        content.add(title);

        JPanel infoPanel = new JPanel(new BorderLayout(0, 10));
        infoPanel.setBackground(Color.WHITE);
        JLabel orderLabel = new JLabel("Order ID: #" + String.format("%02d", order.getId()));
        orderLabel.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        JLabel customerLabel = new JLabel("Customer: " + order.getCustomerName());
        customerLabel.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        infoPanel.add(orderLabel, BorderLayout.NORTH);
        infoPanel.add(customerLabel, BorderLayout.CENTER);
        content.add(infoPanel);
        content.add(Box.createVerticalStrut(10));

        JPanel statusPanel = new JPanel(new BorderLayout(5, 0));
        statusPanel.setBackground(Color.WHITE);
        statusPanel.setBorder(new EmptyBorder(10, 0, 10, 0));
        
        JLabel statusLabel = new JLabel("Status:");
        statusLabel.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        statusPanel.add(statusLabel, BorderLayout.WEST);
        
        BackendService.OrderStatus currentStatus = order.getStatus();
        ButtonGroup statusGroup = new ButtonGroup();
        JPanel statusButtonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        statusButtonPanel.setBackground(Color.WHITE);
        statusButtonPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        for (BackendService.OrderStatus status : BackendService.OrderStatus.values()) {
            JRadioButton radioBtn = new JRadioButton(status.getDisplayName());
            radioBtn.setFont(new Font("Segoe UI", Font.PLAIN, 12));
            radioBtn.setBackground(Color.WHITE);
            radioBtn.setAlignmentY(Component.CENTER_ALIGNMENT);
            if (status == currentStatus) {
                radioBtn.setSelected(true);
            }
            statusGroup.add(radioBtn);
            statusButtonPanel.add(radioBtn);
            
            radioBtn.addActionListener(e -> {
                if (status == currentStatus) {
                    return;
                }
                
                int confirm = JOptionPane.showConfirmDialog(dialog,
                    "Are you sure you want to change the status from \"" + currentStatus.getDisplayName() + "\" to \"" + status.getDisplayName() + "\"?",
                    "Confirm Status Change",
                    JOptionPane.YES_NO_OPTION);
                
                if (confirm == JOptionPane.YES_OPTION) {
                    if (backend.updateOrderStatus(orderId, status)) {
                        refreshOrders();
                        if (mainFrame != null) mainFrame.refreshHomeAndDashboard();
                        dialog.dispose();
                    } else {
                        JOptionPane.showMessageDialog(dialog, "Failed to update order status.", "Error", JOptionPane.ERROR_MESSAGE);
                        radioBtn.setSelected(false);
                        for (AbstractButton btn : java.util.Collections.list(statusGroup.getElements())) {
                            if (btn.getText().equals(currentStatus.getDisplayName())) {
                                btn.setSelected(true);
                                break;
                            }
                        }
                    }
                } else {
                    // Revert selection if user cancels
                    radioBtn.setSelected(false);
                    for (AbstractButton btn : java.util.Collections.list(statusGroup.getElements())) {
                        if (btn.getText().equals(currentStatus.getDisplayName())) {
                            btn.setSelected(true);
                            break;
                        }
                    }
                }
            });
        }
        
        statusPanel.add(statusButtonPanel, BorderLayout.CENTER);
        content.add(statusPanel);

        JButton confirmBtn = new JButton("Close");
        confirmBtn.setBackground(new Color(200, 200, 200));
        confirmBtn.setForeground(Color.BLACK);
        confirmBtn.setFocusPainted(false);
        confirmBtn.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        confirmBtn.setBorder(BorderFactory.createEmptyBorder(6, 15, 6, 15));
        confirmBtn.addActionListener(e -> dialog.dispose());
        
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttonPanel.setBackground(Color.WHITE);
        buttonPanel.add(confirmBtn);
        content.add(buttonPanel);

        dialog.add(content, BorderLayout.CENTER);
        dialog.setVisible(true);
    }
    
    private void deleteOrder(int orderId) {
        int confirm = JOptionPane.showConfirmDialog(this,
            "Are you sure you want to delete this order?",
            "Confirm Delete",
            JOptionPane.YES_NO_OPTION);
            if (confirm == JOptionPane.YES_OPTION) {
                if (backend.deleteOrder(orderId)) {
                    refreshOrders();
                    if (mainFrame != null) mainFrame.refreshHomeAndDashboard();
                } else {
                JOptionPane.showMessageDialog(this,
                    "Failed to delete order.",
                    "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
}

/*==================== Profile ====================*/

class ProfilePanel extends JPanel {
    private BackendService backend;
    private JTextField usernameField;
    private JTextField phoneField;
    private JTextField emailField;
    private JTextField passwordField;
    private RoundedPanel card;
    
    ProfilePanel() {
        backend = BackendService.getInstance();
        setOpaque(false);
        setLayout(new BorderLayout(0, 10));

        JLabel title = new JLabel("Profile");
        title.setFont(new Font("Segoe UI", Font.BOLD, 24));
        add(title, BorderLayout.NORTH);

        card = new RoundedPanel(24);
        card.setBackground(Color.WHITE);
        card.setBorder(new EmptyBorder(20, 30, 20, 30));
        card.setLayout(new BorderLayout(0, 15));

        JLabel section = new JLabel("Personal Information");
        section.setFont(new Font("Segoe UI", Font.BOLD, 14));
        JButton edit = createLinkButton("Edit Information");
        edit.addActionListener(e -> showEditDialog());
        JPanel header = new JPanel(new BorderLayout());
        header.setOpaque(false);
        header.add(section, BorderLayout.WEST);
        header.add(edit, BorderLayout.EAST);
        card.add(header, BorderLayout.NORTH);

        GridBagLayout gbl = new GridBagLayout();
        JPanel form = new JPanel(gbl);
        form.setOpaque(false);
        GridBagConstraints gc = new GridBagConstraints();
        gc.insets = new Insets(8, 8, 8, 8);
        gc.fill = GridBagConstraints.NONE;
        gc.anchor = GridBagConstraints.WEST;

        gc.gridx = 0; gc.gridy = 0;
        form.add(createField("Username", usernameField = new JTextField()), gc);
        gc.gridx = 1; gc.gridy = 0;
        form.add(createField("Phone No.", phoneField = new JTextField()), gc);
        gc.gridx = 0; gc.gridy = 1;
        form.add(createField("Email", emailField = new JTextField()), gc);
        gc.gridx = 1; gc.gridy = 1;
        form.add(createField("Password", passwordField = new JPasswordField()), gc);

        card.add(form, BorderLayout.CENTER);

        add(card, BorderLayout.CENTER);
        
        refreshProfile();
    }
    
    private void refreshProfile() {
        BackendService.User user = backend.getCurrentUser();
        if (user != null) {
            usernameField.setText(user.getUsername());
            phoneField.setText(user.getPhone() != null ? user.getPhone() : "");
            emailField.setText(user.getEmail());
            passwordField.setText("********");
        } else {
            usernameField.setText("");
            phoneField.setText("");
            emailField.setText("");
            passwordField.setText("");
        }
    }

    private JPanel createField(String labelText, JTextField field) {
        JPanel p = new JPanel(new BorderLayout(0, 5));
        p.setOpaque(false);
        JLabel lab = new JLabel(labelText);
        lab.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        field.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        field.setEditable(false);
        field.setPreferredSize(new Dimension(200, 28));
        field.setBackground(new Color(245, 245, 245));
        p.add(lab, BorderLayout.NORTH);
        p.add(field, BorderLayout.CENTER);
        return p;
    }
    
    private void showEditDialog() {
        BackendService.User user = backend.getCurrentUser();
        if (user == null) {
            JOptionPane.showMessageDialog(this, "No user logged in.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        JDialog dialog = new JDialog((JFrame) SwingUtilities.getWindowAncestor(this), "Edit Information", true);
        dialog.setLocationRelativeTo(this);
        dialog.setLayout(new BorderLayout(0, 0));
        dialog.getContentPane().setBackground(Color.WHITE);
        dialog.setResizable(false);

        JPanel content = new JPanel();
        content.setBackground(Color.WHITE);
        content.setLayout(new BoxLayout(content, BoxLayout.Y_AXIS));
        content.setBorder(new EmptyBorder(20, 30, 20, 30));

        JLabel title = new JLabel("Edit Information");
        title.setFont(new Font("Segoe UI", Font.BOLD, 18));
        title.setBorder(new EmptyBorder(0, 0, 15, 0));
        content.add(title);

        // Two-column layout for fields
        JPanel fieldsPanel = new JPanel();
        fieldsPanel.setBackground(Color.WHITE);
        fieldsPanel.setLayout(new GridBagLayout());
        GridBagConstraints gc = new GridBagConstraints();
        gc.insets = new Insets(0, 0, 10, 15);
        gc.anchor = GridBagConstraints.WEST;
        gc.fill = GridBagConstraints.HORIZONTAL;
        gc.weightx = 1.0;

        JTextField usernameEdit = new JTextField(user.getUsername());
        usernameEdit.setPreferredSize(new Dimension(200, 28));
        gc.gridx = 0; gc.gridy = 0;
        fieldsPanel.add(createFormField("Username:", usernameEdit), gc);

        JTextField phoneEdit = new JTextField(user.getPhone() != null ? user.getPhone() : "");
        phoneEdit.setPreferredSize(new Dimension(200, 28));
        gc.gridx = 1; gc.gridy = 0;
        fieldsPanel.add(createFormField("Phone no.:", phoneEdit), gc);

        JTextField emailEdit = new JTextField(user.getEmail());
        emailEdit.setPreferredSize(new Dimension(200, 28));
        gc.gridx = 0; gc.gridy = 1;
        fieldsPanel.add(createFormField("Email:", emailEdit), gc);

        JPasswordField oldPasswordEdit = new JPasswordField();
        oldPasswordEdit.setPreferredSize(new Dimension(200, 28));
        gc.gridx = 1; gc.gridy = 1;
        fieldsPanel.add(createFormField("Old password:", oldPasswordEdit), gc);

        JPasswordField newPasswordEdit = new JPasswordField();
        newPasswordEdit.setPreferredSize(new Dimension(200, 28));
        gc.gridx = 0; gc.gridy = 2;
        fieldsPanel.add(createFormField("New password:", newPasswordEdit), gc);

        JPasswordField confirmPasswordEdit = new JPasswordField();
        confirmPasswordEdit.setPreferredSize(new Dimension(200, 28));
        gc.gridx = 1; gc.gridy = 2;
        fieldsPanel.add(createFormField("Confirm password:", confirmPasswordEdit), gc);

        content.add(fieldsPanel);

        JButton saveBtn = new JButton("Save");
        saveBtn.setBackground(new Color(51, 102, 255));
        saveBtn.setForeground(Color.WHITE);
        saveBtn.setFocusPainted(false);
        saveBtn.setFont(new Font("Segoe UI", Font.BOLD, 13));
        saveBtn.setBorder(BorderFactory.createEmptyBorder(8, 18, 8, 18));
        saveBtn.addActionListener(e -> {
            String username = usernameEdit.getText().trim();
            String phone = phoneEdit.getText().trim();
            String email = emailEdit.getText().trim();
            String oldPassword = new String(oldPasswordEdit.getPassword());
            String newPassword = new String(newPasswordEdit.getPassword());
            String confirmPassword = new String(confirmPasswordEdit.getPassword());
            
            // Validate new password if provided
            if (!newPassword.isEmpty()) {
                if (!newPassword.equals(confirmPassword)) {
                    JOptionPane.showMessageDialog(dialog, "New password and confirm password do not match.", "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }
            }
            
            if (backend.updateUserProfile(username, phone, email, 
                    oldPassword.isEmpty() ? null : oldPassword, 
                    newPassword.isEmpty() ? null : newPassword)) {
                refreshProfile();
                dialog.dispose();
                JOptionPane.showMessageDialog(this, "Profile updated successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(dialog, "Failed to update profile. Please check your inputs.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        });
        
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 0, 0));
        buttonPanel.setBackground(Color.WHITE);
        buttonPanel.setBorder(new EmptyBorder(10, 0, 0, 0));
        buttonPanel.add(saveBtn);
        content.add(buttonPanel);

        dialog.add(content, BorderLayout.CENTER);
        dialog.pack();
        dialog.setLocationRelativeTo(this);
        dialog.setVisible(true);
    }
    
    private JPanel createFormField(String label, JComponent input) {
        JPanel panel = new JPanel(new BorderLayout(0, 5));
        panel.setBackground(Color.WHITE);
        JLabel lab = new JLabel(label);
        lab.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        input.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        panel.add(lab, BorderLayout.NORTH);
        panel.add(input, BorderLayout.CENTER);
        return panel;
    }

    private JButton createLinkButton(String text) {
        JButton b = new JButton(text);
        b.setBorderPainted(false);
        b.setContentAreaFilled(false);
        b.setFocusPainted(false);
        b.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        b.setForeground(new Color(70, 70, 160));
        return b;
    }
}
