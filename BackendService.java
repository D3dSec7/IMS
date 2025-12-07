package SadSystem;

import java.util.*;
import java.util.stream.Collectors;

public class BackendService {

    private static BackendService instance;

    private final List<User> users;
    private final List<Product> products;
    private final List<Order> orders;
    private User currentUser;
    private static final int LOW_STOCK_THRESHOLD = 5;

    private BackendService() {
        users = new ArrayList<>();
        products = new ArrayList<>();
        orders = new ArrayList<>();
        currentUser = null;
        initializeSampleData();
    }

    public static BackendService getInstance() {
        if (instance == null) {
            instance = new BackendService();
        }
        return instance;
    }

    public boolean registerUser(String username, String password, String email) {
        if (username == null || username.trim().isEmpty()) {
            return false;
        }
        if (password == null || password.length() < 3) {
            return false;
        }
        if (email == null || !isValidEmail(email)) {
            return false;
        }

        if (users.stream().anyMatch(u -> u.getUsername().equalsIgnoreCase(username))) {
            return false;
        }
        if (users.stream().anyMatch(u -> u.getEmail().equalsIgnoreCase(email))) {
            return false;
        }

        User newUser = new User(username, password, email);
        users.add(newUser);
        return true;
    }

    public boolean login(String username, String password) {
        if (username == null || password == null) {
            return false;
        }

        Optional<User> userOpt = users.stream()
            .filter(u -> u.getUsername().equalsIgnoreCase(username) && u.getPassword().equals(password))
            .findFirst();

        if (userOpt.isPresent()) {
            currentUser = userOpt.get();
            return true;
        }
        return false;
    }

    public void logout() {
        currentUser = null;
    }

    public User getCurrentUser() {
        return currentUser;
    }

    public boolean requestPasswordReset(String email) {
        if (email == null || !isValidEmail(email)) {
            return false;
        }

        return users.stream().anyMatch(u -> u.getEmail().equalsIgnoreCase(email));
    }

    public boolean updateUserProfile(String username, String phone, String email, String oldPassword, String newPassword) {
        if (currentUser == null) {
            return false;
        }
        if (newPassword != null && !newPassword.isEmpty()) {
            if (oldPassword == null || !currentUser.getPassword().equals(oldPassword)) {
                return false;
            }
            if (newPassword.length() < 3) {
                return false;
            }
        }
        if (username != null && !username.trim().isEmpty()) {
            if (users.stream().anyMatch(u -> !u.equals(currentUser) && u.getUsername().equalsIgnoreCase(username))) {
                return false;
            }
            currentUser.setUsername(username);
        }

        if (phone != null) {
            currentUser.setPhone(phone);
        }

        if (email != null && isValidEmail(email)) {
            if (users.stream().anyMatch(u -> !u.equals(currentUser) && u.getEmail().equalsIgnoreCase(email))) {
                return false;
            }
            currentUser.setEmail(email);
        }

        if (newPassword != null && !newPassword.isEmpty()) {
            currentUser.setPassword(newPassword);
        }
        return true;
    }

    public User getUserByUsername(String username) {
        return users.stream()
            .filter(u -> u.getUsername().equalsIgnoreCase(username))
            .findFirst()
            .orElse(null);
    }

    public boolean addProduct(String name, double price, int quantity) {
        if (name == null || name.trim().isEmpty()) {
            return false;
        }
        if (price < 0) {
            return false;
        }
        if (quantity < 0) {
            return false;
        }

        if (products.stream().anyMatch(p -> p.getName().equalsIgnoreCase(name))) {
            return false;
        }

        Product product = new Product(name, price, quantity);
        products.add(product);
        return true;
    }

    public boolean updateProduct(int productId, String name, Double price, Integer quantity) {
        Product product = getProductById(productId);
        if (product == null) {
            return false;
        }

        if (name != null && !name.trim().isEmpty()) {
            if (products.stream().anyMatch(p -> p.getId() != productId && p.getName().equalsIgnoreCase(name))) {
                return false;
            }
            product.setName(name);
        }

        if (price != null && price >= 0) {
            product.setPrice(price);
        }

        if (quantity != null && quantity >= 0) {
            product.setQuantity(quantity);
        }
        return true;
    }

    public boolean deleteProduct(int productId) {
        Product product = getProductById(productId);
        if (product == null) {
            return false;
        }
        boolean hasOrders = orders.stream()
            .anyMatch(o -> o.getItems().stream()
                .anyMatch(oi -> oi.getProductId() == productId));

        if (hasOrders) {
            return false;
        }
        products.remove(product);
        return true;
    }

    public Product getProductById(int id) {
        return products.stream()
            .filter(p -> p.getId() == id)
            .findFirst()
            .orElse(null);
    }

    public List<Product> getAllProducts() {
        return new ArrayList<>(products);
    }

    public List<Product> searchProducts(String query) {
        if (query == null || query.trim().isEmpty()) {
            return getAllProducts();
        }

        String lowerQuery = query.toLowerCase();
        return products.stream()
            .filter(p -> p.getName().toLowerCase().contains(lowerQuery))
            .collect(Collectors.toList());
    }

    public List<Product> getLowStockProducts() {
        return products.stream()
            .filter(p -> p.getQuantity() <= LOW_STOCK_THRESHOLD)
            .collect(Collectors.toList());
    }

    public boolean isProductInStock(int productId, int quantity) {
        Product product = getProductById(productId);
        return product != null && product.getQuantity() >= quantity;
    }

    public boolean reduceProductStock(int productId, int quantity) {
        Product product = getProductById(productId);
        if (product == null || product.getQuantity() < quantity) {
            return false;
        }

        product.setQuantity(product.getQuantity() - quantity);
        return true;
    }

    public Order createOrder(String customerName, List<OrderItem> items) {
        if (customerName == null || customerName.trim().isEmpty()) {
            return null;
        }
        if (items == null || items.isEmpty()) {
            return null;
        }

        for (OrderItem item : items) {
            if (!isProductInStock(item.getProductId(), item.getQuantity())) {
                return null;
            }
        }

        Order order = new Order(customerName, items);
        orders.add(order);

        for (OrderItem item : items) {
            reduceProductStock(item.getProductId(), item.getQuantity());
        }
        return order;
    }

    public Order getOrderById(int id) {
        return orders.stream()
            .filter(o -> o.getId() == id)
            .findFirst()
            .orElse(null);
    }

    public List<Order> getAllOrders() {
        return new ArrayList<>(orders);
    }

    public List<Order> searchOrders(String query) {
        if (query == null || query.trim().isEmpty()) {
            return getAllOrders();
        }

        String lowerQuery = query.toLowerCase();
        return orders.stream()
            .filter(o -> o.getCustomerName().toLowerCase().contains(lowerQuery))
            .collect(Collectors.toList());
    }

    public boolean updateOrderStatus(int orderId, OrderStatus status) {
        Order order = getOrderById(orderId);
        if (order == null) {
            return false;
        }

        order.setStatus(status);
        return true;
    }

    public boolean deleteOrder(int orderId) {
        Order order = getOrderById(orderId);
        if (order == null) {
            return false;
        }
        for (OrderItem item : order.getItems()) {
            Product product = getProductById(item.getProductId());
            if (product != null) {
                product.setQuantity(product.getQuantity() + item.getQuantity());
            }
        }
        orders.remove(order);
        return true;
    }

    public int getTotalProducts() {
        return products.size();
    }

    public int getTotalInStock() {
        return products.stream()
            .mapToInt(Product::getQuantity)
            .sum();
    }

    public int getTotalOrders() {
        return orders.size();
    }

    public int getLowStockCount() {
        return (int) products.stream()
            .filter(p -> p.getQuantity() <= LOW_STOCK_THRESHOLD)
            .count();
    }

    public List<Order> getRecentOrders(int count) {
        return orders.stream()
            .sorted((o1, o2) -> o2.getDate().compareTo(o1.getDate()))
            .limit(count)
            .collect(Collectors.toList());
    }

    private boolean isValidEmail(String email) {
        if (email == null || email.trim().isEmpty()) {
            return false;
        }
        return email.matches("^[A-Za-z0-9+_.-]+@(.+)$");
    }

    private void initializeSampleData() {
        User admin = new User("admin", "admin", "admin@inventory.com");
        admin.setPhone("09123456789");
        users.add(admin);

        User cardo = new User("Cardo Dalisay", "password123", "cardodalisay@gmail.com");
        cardo.setPhone("09123456789");
        users.add(cardo);

        products.add(new Product("AMD Ryzen 7 7800X3D", 21999.0, 7));
        products.add(new Product("RM750x Fully Modular Power Supply", 7937.0, 11));
        products.add(new Product("Nvidia RTX 5090", 99999.0, 3));
        products.add(new Product("Intel Core Ultra 9 Processor 285K", 36600.0, 8));

        List<OrderItem> items1 = new ArrayList<>();
        items1.add(new OrderItem(1, 2));
        items1.add(new OrderItem(3, 1));
        orders.add(new Order("Johnler", items1));
        orders.get(0).setStatus(OrderStatus.COMPLETED);

        List<OrderItem> items2 = new ArrayList<>();
        items2.add(new OrderItem(2, 1));
        orders.add(new Order("James", items2));
        orders.get(1).setStatus(OrderStatus.PENDING);

        List<OrderItem> items3 = new ArrayList<>();
        items3.add(new OrderItem(4, 1));
        items3.add(new OrderItem(1, 1));
        orders.add(new Order("Johnson", items3));
        orders.get(2).setStatus(OrderStatus.PROCESSING);
    }

    public static class User {
        private String username;
        private String password;
        private String email;
        private String phone;

        public User(String username, String password, String email) {
            this.username = username;
            this.password = password;
            this.email = email;
            this.phone = "";
        }

        public String getUsername() { return username; }
        public void setUsername(String username) { this.username = username; }

        public String getPassword() { return password; }
        public void setPassword(String password) { this.password = password; }

        public String getEmail() { return email; }
        public void setEmail(String email) { this.email = email; }

        public String getPhone() { return phone; }
        public void setPhone(String phone) { this.phone = phone; }
    }

    public static class Product {
        private static int nextId = 1;
        private int id;
        private String name;
        private double price;
        private int quantity;

        public Product(String name, double price, int quantity) {
            this.id = nextId++;
            this.name = name;
            this.price = price;
            this.quantity = quantity;
        }

        public int getId() { return id; }

        public String getName() { return name; }
        public void setName(String name) { this.name = name; }

        public double getPrice() { return price; }
        public void setPrice(double price) { this.price = price; }

        public int getQuantity() { return quantity; }
        public void setQuantity(int quantity) { this.quantity = quantity; }

        public boolean isLowStock() {
            return quantity <= LOW_STOCK_THRESHOLD;
        }
    }

    public static class Order {
        private static int nextId = 1;
        private int id;
        private String customerName;
        private List<OrderItem> items;
        private Date date;
        private OrderStatus status;

        public Order(String customerName, List<OrderItem> items) {
            this.id = nextId++;
            this.customerName = customerName;
            this.items = new ArrayList<>(items);
            this.date = new Date();
            this.status = OrderStatus.PENDING;
        }

        public int getId() { return id; }

        public String getCustomerName() { return customerName; }
        public void setCustomerName(String customerName) { this.customerName = customerName; }

        public List<OrderItem> getItems() { return new ArrayList<>(items); }

        public Date getDate() { return date; }
        public void setDate(Date date) { this.date = date; }

        public OrderStatus getStatus() { return status; }
        public void setStatus(OrderStatus status) { this.status = status; }

        public double getTotal() {
            BackendService service = BackendService.getInstance();
            double total = 0.0;
            for (OrderItem item : items) {
                Product product = service.getProductById(item.getProductId());
                if (product != null) {
                    total += product.getPrice() * item.getQuantity();
                }
            }
            return total;
        }

        public int getTotalItems() {
            return items.stream().mapToInt(OrderItem::getQuantity).sum();
        }
    }

    public static class OrderItem {
        private int productId;
        private int quantity;

        public OrderItem(int productId, int quantity) {
            this.productId = productId;
            this.quantity = quantity;
        }

        public int getProductId() { return productId; }
        public void setProductId(int productId) { this.productId = productId; }

        public int getQuantity() { return quantity; }
        public void setQuantity(int quantity) { this.quantity = quantity; }
    }

    public enum OrderStatus {
        PENDING("Pending"),
        PROCESSING("Processing"),
        COMPLETED("Completed"),
        CANCELLED("Cancelled");

        private final String displayName;

        OrderStatus(String displayName) {
            this.displayName = displayName;
        }

        public String getDisplayName() {
            return displayName;
        }

        public static OrderStatus fromString(String status) {
            for (OrderStatus s : values()) {
                if (s.displayName.equalsIgnoreCase(status)) {
                    return s;
                }
            }
            return PENDING;
        }
    }
}

