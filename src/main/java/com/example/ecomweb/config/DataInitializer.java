package com.example.ecomweb.config;

import com.example.ecomweb.entity.Product;
import com.example.ecomweb.entity.User;
import com.example.ecomweb.service.ProductService;
import com.example.ecomweb.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import java.math.BigDecimal;

@Component
public class DataInitializer implements CommandLineRunner {
    
    @Autowired
    private ProductService productService;
    
    @Autowired
    private UserService userService;
    
    @Override
    public void run(String... args) throws Exception {
        // Initialize sample data if database is empty
        if (productService.getAllProducts().isEmpty()) {
            initializeSampleProducts();
        }
        
        if (userService.getAllUsers().isEmpty()) {
            initializeSampleUsers();
        }
    }
    
    private void initializeSampleProducts() {
        System.out.println("Initializing sample products...");
        
        // Electronics
        Product laptop = new Product("Gaming Laptop", 
            "High-performance gaming laptop with RTX 4060 graphics card, 16GB RAM, 1TB SSD", 
            new BigDecimal("1299.99"), 10, "Electronics");
        laptop.setBrand("TechBrand");
        laptop.setImageUrl("https://via.placeholder.com/300x250/0066cc/ffffff?text=Gaming+Laptop");
        productService.saveProduct(laptop);
        
        Product smartphone = new Product("Smartphone Pro", 
            "Latest smartphone with advanced camera system and 5G connectivity", 
            new BigDecimal("899.99"), 25, "Electronics");
        smartphone.setBrand("PhoneBrand");
        smartphone.setImageUrl("https://via.placeholder.com/300x250/ff6600/ffffff?text=Smartphone");
        productService.saveProduct(smartphone);
        
        Product headphones = new Product("Wireless Headphones", 
            "Premium noise-cancelling wireless headphones with 30-hour battery life", 
            new BigDecimal("199.99"), 50, "Electronics");
        headphones.setBrand("AudioBrand");
        headphones.setImageUrl("https://via.placeholder.com/300x250/9900cc/ffffff?text=Headphones");
        productService.saveProduct(headphones);
        
        Product tablet = new Product("Tablet Pro", 
            "Professional tablet with stylus support and 12-inch display", 
            new BigDecimal("649.99"), 20, "Electronics");
        tablet.setBrand("TechBrand");
        tablet.setImageUrl("https://via.placeholder.com/300x250/00ccff/ffffff?text=Tablet");
        productService.saveProduct(tablet);
        
        // Clothing
        Product tshirt = new Product("Cotton T-Shirt", 
            "Comfortable 100% cotton t-shirt available in multiple colors", 
            new BigDecimal("29.99"), 100, "Clothing");
        tshirt.setBrand("FashionBrand");
        tshirt.setImageUrl("https://via.placeholder.com/300x250/00cc66/ffffff?text=T-Shirt");
        productService.saveProduct(tshirt);
        
        Product jeans = new Product("Denim Jeans", 
            "Classic fit denim jeans made from premium cotton blend", 
            new BigDecimal("79.99"), 75, "Clothing");
        jeans.setBrand("DenimBrand");
        jeans.setImageUrl("https://via.placeholder.com/300x250/003366/ffffff?text=Jeans");
        productService.saveProduct(jeans);
        
        Product jacket = new Product("Winter Jacket", 
            "Warm winter jacket with water-resistant coating", 
            new BigDecimal("149.99"), 30, "Clothing");
        jacket.setBrand("OutdoorBrand");
        jacket.setImageUrl("https://via.placeholder.com/300x250/cc3300/ffffff?text=Jacket");
        productService.saveProduct(jacket);
        
        // Home & Garden
        Product coffeeMaker = new Product("Coffee Maker", 
            "Programmable coffee maker with timer and auto-shutoff feature", 
            new BigDecimal("89.99"), 30, "Home & Garden");
        coffeeMaker.setBrand("KitchenBrand");
        coffeeMaker.setImageUrl("https://via.placeholder.com/300x250/cc6600/ffffff?text=Coffee+Maker");
        productService.saveProduct(coffeeMaker);
        
        Product plant = new Product("Indoor Plant", 
            "Beautiful low-maintenance indoor plant perfect for home decoration", 
            new BigDecimal("24.99"), 40, "Home & Garden");
        plant.setBrand("GreenBrand");
        plant.setImageUrl("https://via.placeholder.com/300x250/009900/ffffff?text=Plant");
        productService.saveProduct(plant);
        
        Product lamp = new Product("LED Desk Lamp", 
            "Adjustable LED desk lamp with multiple brightness levels", 
            new BigDecimal("59.99"), 35, "Home & Garden");
        lamp.setBrand("LightBrand");
        lamp.setImageUrl("https://via.placeholder.com/300x250/ffcc00/ffffff?text=Lamp");
        productService.saveProduct(lamp);
        
        // Books
        Product book1 = new Product("Programming Guide", 
            "Complete guide to modern programming languages and best practices", 
            new BigDecimal("49.99"), 60, "Books");
        book1.setBrand("TechPublisher");
        book1.setImageUrl("https://via.placeholder.com/300x250/cc0066/ffffff?text=Programming+Book");
        productService.saveProduct(book1);
        
        Product book2 = new Product("Business Strategy", 
            "Essential business strategy handbook for entrepreneurs", 
            new BigDecimal("39.99"), 45, "Books");
        book2.setBrand("BusinessPublisher");
        book2.setImageUrl("https://via.placeholder.com/300x250/6600cc/ffffff?text=Business+Book");
        productService.saveProduct(book2);
        
        // Sports
        Product basketball = new Product("Basketball", 
            "Official size basketball suitable for indoor and outdoor play", 
            new BigDecimal("34.99"), 80, "Sports");
        basketball.setBrand("SportsBrand");
        basketball.setImageUrl("https://via.placeholder.com/300x250/ff3300/ffffff?text=Basketball");
        productService.saveProduct(basketball);
        
        Product yogaMat = new Product("Yoga Mat", 
            "Non-slip yoga mat with extra cushioning for comfort", 
            new BigDecimal("29.99"), 50, "Sports");
        yogaMat.setBrand("FitnessBrand");
        yogaMat.setImageUrl("https://via.placeholder.com/300x250/ff6699/ffffff?text=Yoga+Mat");
        productService.saveProduct(yogaMat);
        
        System.out.println("Sample products initialized successfully!");
    }
    
    private void initializeSampleUsers() {
        System.out.println("Initializing sample users...");
        
        // Create admin user
        User admin = new User("admin", "admin@ecommerceai.com", "admin123", "Administrator");
        admin.setRole(User.Role.ADMIN);
        admin.setAddress("123 Admin Street, Admin City, AC 12345");
        admin.setPhoneNumber("555-0001");
        
        try {
            userService.registerUser(admin);
            System.out.println("Admin user created successfully!");
        } catch (RuntimeException e) {
            System.out.println("Admin user already exists or error creating: " + e.getMessage());
        }
        
        // Create sample regular user
        User user = new User("john_doe", "john@example.com", "password123", "John Doe");
        user.setAddress("456 User Avenue, User City, UC 67890");
        user.setPhoneNumber("555-0002");
        
        try {
            userService.registerUser(user);
            System.out.println("Sample user created successfully!");
        } catch (RuntimeException e) {
            System.out.println("Sample user already exists or error creating: " + e.getMessage());
        }
        
        // Create another sample user
        User user2 = new User("jane_smith", "jane@example.com", "password123", "Jane Smith");
        user2.setAddress("789 Customer Lane, Customer City, CC 11111");
        user2.setPhoneNumber("555-0003");
        
        try {
            userService.registerUser(user2);
            System.out.println("Second sample user created successfully!");
        } catch (RuntimeException e) {
            System.out.println("Second sample user already exists or error creating: " + e.getMessage());
        }
        
        System.out.println("Sample users initialized successfully!");
    }
}