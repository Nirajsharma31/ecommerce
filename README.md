# EcommerceAI - Full Stack E-commerce Application

A complete full-stack e-commerce web application built with Spring Boot 3.5.3, Java 17, and MySQL 8.0.41.

## 🚀 Features

### 👤 User Features
- User registration and login with validation
- Browse products by category
- Search products with keyword matching
- Add products to shopping cart
- Manage cart (update quantities, remove items)
- Complete checkout process with order placement
- View order history and status tracking

### 🔧 Admin Features
- Admin panel with dashboard
- Add new products with full details
- Manage existing products (edit/delete)
- View and manage all orders
- Update order status (Pending → Confirmed → Shipped → Delivered)
- Order statistics and analytics

## 🛠 Technology Stack

### Backend
- **Spring Boot 3.5.3** - Main framework
- **Java 17** - Programming language
- **MySQL 8.0.41** - Database
- **Spring Data JPA** - Data persistence
- **Spring Security** - Authentication and authorization
- **Hibernate** - ORM framework
- **Gradle** - Build tool
- **Bean Validation** - Input validation

### Frontend
- **HTML5** - Structure and markup
- **CSS3** - Styling with responsive design
- **JavaScript (ES6+)** - Dynamic functionality
- **Fetch API** - HTTP requests to backend
- **Single Page Application** - Seamless navigation

## 📋 Prerequisites

- **Java 17** or higher
- **MySQL 8.0.41** or higher
- **Gradle** (or use the included Gradle wrapper)

## 🔧 Setup Instructions

### 1. Database Setup
```sql
-- Create database
CREATE DATABASE ecommerceai;

-- Optional: Create dedicated user
CREATE USER 'ecommerce_user'@'localhost' IDENTIFIED BY 'your_password';
GRANT ALL PRIVILEGES ON ecommerceai.* TO 'ecommerce_user'@'localhost';
FLUSH PRIVILEGES;
```

### 2. Configuration
Update `src/main/resources/application.properties` if needed:
```properties
# Update these values according to your MySQL setup
spring.datasource.username=root
spring.datasource.password=your_password
```

### 3. Run the Application
```bash
# Navigate to project directory
cd FirstEcomWeb

# Using Gradle wrapper (recommended)
./gradlew bootRun

# Or on Windows
gradlew.bat bootRun
```

### 4. Access the Application
- **Frontend:** `http://localhost:8080`
- **API Base:** `http://localhost:8080/api`
- The application will automatically create sample data on first run

## 👥 Default Users

The application creates default users on startup:

### Admin User
- **Username:** `admin`
- **Password:** `admin123`
- **Email:** `admin@ecommerceai.com`
- **Role:** Administrator

### Regular Users
- **User 1:** username: `john_doe`, password: `password123`
- **User 2:** username: `jane_smith`, password: `password123`

## 📚 API Documentation

### Base URL
```
http://localhost:8080/api
```

### 🔐 Authentication Endpoints

#### Register User
```http
POST /api/users/register
Content-Type: application/json

{
  "username": "newuser",
  "email": "user@example.com",
  "password": "password123",
  "fullName": "New User",
  "address": "123 Main St",
  "phoneNumber": "555-0123"
}
```

#### Login User
```http
POST /api/users/login
Content-Type: application/json

{
  "username": "admin",
  "password": "admin123"
}
```

### 📦 Product Endpoints

#### Get All Products
```http
GET /api/products
```

#### Search Products
```http
GET /api/products/search?keyword=laptop
```

#### Get Products by Category
```http
GET /api/products/category/Electronics
```

#### Create Product (Admin)
```http
POST /api/products
Content-Type: application/json

{
  "name": "New Product",
  "description": "Product description",
  "price": 99.99,
  "stockQuantity": 50,
  "category": "Electronics",
  "brand": "BrandName",
  "imageUrl": "https://example.com/image.jpg"
}
```

### 🛒 Cart Endpoints

#### Add to Cart
```http
POST /api/cart/add
Content-Type: application/json

{
  "userId": 1,
  "productId": 1,
  "quantity": 2
}
```

#### Get Cart Items
```http
GET /api/cart/user/{userId}
```

#### Update Cart Item
```http
PUT /api/cart/update/{cartItemId}
Content-Type: application/json

{
  "quantity": 3
}
```

### 📋 Order Endpoints

#### Create Order
```http
POST /api/orders/create
Content-Type: application/json

{
  "userId": 1,
  "shippingAddress": "123 Main St, City, State 12345",
  "paymentMethod": "CREDIT_CARD"
}
```

#### Get User Orders
```http
GET /api/orders/user/{userId}
```

#### Update Order Status (Admin)
```http
PUT /api/orders/admin/status/{orderId}
Content-Type: application/json

{
  "status": "SHIPPED"
}
```

## 🗂 Project Structure

```
FirstEcomWeb/
├── src/
│   ├── main/
│   │   ├── java/com/example/ecomweb/
│   │   │   ├── config/          # Configuration classes
│   │   │   │   ├── DataInitializer.java
│   │   │   │   └── SecurityConfig.java
│   │   │   ├── controller/      # REST controllers
│   │   │   │   ├── CartController.java
│   │   │   │   ├── OrderController.java
│   │   │   │   ├── ProductController.java
│   │   │   │   └── UserController.java
│   │   │   ├── entity/          # JPA entities
│   │   │   │   ├── CartItem.java
│   │   │   │   ├── Order.java
│   │   │   │   ├── OrderItem.java
│   │   │   │   ├── Product.java
│   │   │   │   └── User.java
│   │   │   ├── repository/      # Data repositories
│   │   │   │   ├── CartItemRepository.java
│   │   │   │   ├── OrderRepository.java
│   │   │   │   ├── ProductRepository.java
│   │   │   │   └── UserRepository.java
│   │   │   ├── service/         # Business logic
│   │   │   │   ├── CartService.java
│   │   │   │   ├── OrderService.java
│   │   │   │   ├── ProductService.java
│   │   │   │   └── UserService.java
│   │   │   └── FirstEcomWebApplication.java
│   │   └── resources/
│   │       ├── static/          # Frontend files
│   │       │   ├── css/style.css
│   │       │   ├── js/app.js
│   │       │   └── index.html
│   │       └── application.properties
│   └── test/                    # Test files
├── build.gradle                # Gradle build configuration
└── README.md
```

## 🎯 Sample Data

### Products (14 items across 5 categories)
- **Electronics:** Gaming Laptop, Smartphone Pro, Wireless Headphones, Tablet Pro
- **Clothing:** Cotton T-Shirt, Denim Jeans, Winter Jacket
- **Home & Garden:** Coffee Maker, Indoor Plant, LED Desk Lamp
- **Books:** Programming Guide, Business Strategy
- **Sports:** Basketball, Yoga Mat

### Users
- 1 Admin user with full privileges
- 2 Regular users for testing

## 🔒 Security Features

- **Password Encryption:** BCrypt hashing
- **CORS Configuration:** Cross-origin requests enabled
- **Input Validation:** Bean validation on all inputs
- **SQL Injection Protection:** JPA/Hibernate parameterized queries
- **Role-based Access:** Admin vs User permissions

## 🚦 Order Status Flow

1. **PENDING** - Order created, awaiting confirmation
2. **CONFIRMED** - Order confirmed, preparing for shipment
3. **SHIPPED** - Order shipped, in transit
4. **DELIVERED** - Order delivered successfully
5. **CANCELLED** - Order cancelled (stock restored automatically)

## 🧪 Testing the Application

### Frontend Testing
1. Open `http://localhost:8080`
2. Register a new user or login with existing credentials
3. Browse products and add to cart
4. Complete checkout process
5. Login as admin to manage products and orders

### API Testing with cURL

#### Login as admin:
```bash
curl -X POST http://localhost:8080/api/users/login \
  -H "Content-Type: application/json" \
  -d '{"username": "admin", "password": "admin123"}'
```

#### Get all products:
```bash
curl -X GET http://localhost:8080/api/products
```

#### Add product to cart:
```bash
curl -X POST http://localhost:8080/api/cart/add \
  -H "Content-Type: application/json" \
  -d '{"userId": 1, "productId": 1, "quantity": 2}'
```

## 🐛 Troubleshooting

### Common Issues

1. **Database Connection Error**
   - Ensure MySQL is running: `sudo service mysql start`
   - Check credentials in `application.properties`
   - Verify database `ecommerceai` exists

2. **Port Already in Use**
   - Change port in `application.properties`: `server.port=8081`
   - Or kill process using port 8080: `sudo lsof -t -i:8080 | xargs kill -9`

3. **Build Errors**
   - Ensure Java 17 is installed: `java -version`
   - Clean and rebuild: `./gradlew clean build`

4. **Frontend Not Loading**
   - Check if static resources are in correct path
   - Verify security configuration allows static content

### Logs
Check application logs for detailed error information:
```bash
./gradlew bootRun --info
```

## 🚀 Deployment

### Production Configuration
Update `application.properties` for production:
```properties
# Production database
spring.datasource.url=jdbc:mysql://prod-server:3306/ecommerceai
spring.jpa.hibernate.ddl-auto=validate

# Security
spring.security.require-ssl=true

# Logging
logging.level.root=WARN
logging.level.com.example.ecomweb=INFO
```

### Docker Deployment
Create `Dockerfile`:
```dockerfile
FROM openjdk:17-jdk-slim
COPY build/libs/*.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "/app.jar"]
```

Build and run:
```bash
./gradlew build
docker build -t ecommerce-app .
docker run -p 8080:8080 ecommerce-app
```

## 📈 Performance Considerations

- **Lazy Loading:** Entity relationships use lazy loading
- **Database Indexing:** Indexes on frequently queried fields
- **Connection Pooling:** HikariCP connection pool (default)
- **Caching:** Consider adding Redis for session management
- **Static Resources:** Served efficiently by Spring Boot

## 🔄 Development

### Adding New Features
1. Create entity classes in `entity/` package
2. Add repository interfaces in `repository/` package
3. Implement business logic in `service/` package
4. Create REST endpoints in `controller/` package
5. Update frontend JavaScript for new functionality

### Database Schema
- Tables are created automatically using Hibernate DDL
- Use `spring.jpa.hibernate.ddl-auto=validate` in production
- Consider using Flyway or Liquibase for production migrations

## 🎯 Future Enhancements

- **Payment Integration:** Stripe, PayPal integration
- **Email Notifications:** Order confirmations, shipping updates
- **Product Reviews:** Rating and review system
- **Wishlist:** Save products for later
- **Advanced Search:** Filters, sorting, pagination
- **Real-time Updates:** WebSocket for live notifications
- **Mobile App:** React Native or Flutter app
- **Analytics:** Sales reports, user behavior tracking

## 📄 License

This project is for educational purposes.

---

**🎉 Your complete full-stack e-commerce application is ready!**

Start the application with `./gradlew bootRun` and visit `http://localhost:8080` to see your e-commerce platform in action!