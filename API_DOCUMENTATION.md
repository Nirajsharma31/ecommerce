# E-Commerce API Documentation

## Base URL
```
http://localhost:8080/api
```

## Authentication Endpoints

### 1. User Registration
**POST** `/users/register`

**Request Body:**
```json
{
  "username": "string",
  "email": "string",
  "password": "string",
  "fullName": "string",
  "address": "string",
  "phoneNumber": "string"
}
```

**Response (Success):**
```json
{
  "message": "User registered successfully",
  "user": {
    "id": 1,
    "username": "john_doe",
    "email": "john@example.com",
    "fullName": "John Doe",
    "address": "123 Main St",
    "phoneNumber": "+91-9876543210",
    "role": "USER",
    "createdAt": "2024-01-01T10:00:00"
  }
}
```

**Response (Error):**
```json
{
  "error": "Username already exists"
}
```

### 2. User Login
**POST** `/users/login`

**Request Body:**
```json
{
  "username": "string",
  "password": "string"
}
```

**Response (Success):**
```json
{
  "message": "Login successful",
  "user": {
    "id": 1,
    "username": "john_doe",
    "email": "john@example.com",
    "fullName": "John Doe",
    "role": "USER"
  }
}
```

**Response (Error):**
```json
{
  "error": "Invalid credentials"
}
```

## Product Endpoints

### 3. Get All Products
**GET** `/products`

**Response:**
```json
[
  {
    "id": 1,
    "name": "Product Name",
    "description": "Product description",
    "price": 999.99,
    "stockQuantity": 50,
    "category": "Electronics",
    "brand": "Brand Name",
    "imageUrl": "http://example.com/image.jpg",
    "hasImage": true,
    "createdAt": "2024-01-01T10:00:00",
    "updatedAt": "2024-01-01T10:00:00"
  }
]
```

### 4. Get Product by ID
**GET** `/products/{id}`

**Response:**
```json
{
  "id": 1,
  "name": "Product Name",
  "description": "Product description",
  "price": 999.99,
  "stockQuantity": 50,
  "category": "Electronics",
  "brand": "Brand Name",
  "imageUrl": "http://example.com/image.jpg",
  "hasImage": true,
  "createdAt": "2024-01-01T10:00:00",
  "updatedAt": "2024-01-01T10:00:00"
}
```

### 5. Get Products by Category
**GET** `/products/category/{category}`

**Response:**
```json
[
  {
    "id": 1,
    "name": "Product Name",
    "description": "Product description",
    "price": 999.99,
    "stockQuantity": 50,
    "category": "Electronics",
    "brand": "Brand Name",
    "hasImage": true
  }
]
```

### 6. Search Products
**GET** `/products/search?keyword={keyword}`

**Query Parameters:**
- `keyword` (string): Search term

**Response:**
```json
[
  {
    "id": 1,
    "name": "Product Name",
    "description": "Product description",
    "price": 999.99,
    "stockQuantity": 50,
    "category": "Electronics",
    "brand": "Brand Name",
    "hasImage": true
  }
]
```

### 7. Get All Categories
**GET** `/products/categories`

**Response:**
```json
[
  "Electronics",
  "Clothing",
  "Home & Garden",
  "Books",
  "Sports"
]
```

### 8. Get Available Products
**GET** `/products/available`

**Response:**
```json
[
  {
    "id": 1,
    "name": "Product Name",
    "description": "Product description",
    "price": 999.99,
    "stockQuantity": 50,
    "category": "Electronics",
    "hasImage": true
  }
]
```

### 9. Create Product (JSON)
**POST** `/products`

**Request Body:**
```json
{
  "name": "Product Name",
  "description": "Product description",
  "price": 999.99,
  "stockQuantity": 50,
  "category": "Electronics",
  "brand": "Brand Name",
  "imageUrl": "http://example.com/image.jpg"
}
```

**Response (Success):**
```json
{
  "message": "Product created successfully",
  "product": {
    "id": 1,
    "name": "Product Name",
    "description": "Product description",
    "price": 999.99,
    "stockQuantity": 50,
    "category": "Electronics",
    "brand": "Brand Name",
    "hasImage": false,
    "createdAt": "2024-01-01T10:00:00"
  }
}
```

### 10. Create Product with Image Upload
**POST** `/products/with-image`

**Content-Type:** `multipart/form-data`

**Form Data:**
- `name` (string): Product name
- `description` (string): Product description
- `price` (decimal): Product price
- `stockQuantity` (integer): Stock quantity
- `category` (string): Product category
- `brand` (string): Product brand (optional)
- `image` (file): Image file (optional, max 5MB)

**Response (Success):**
```json
{
  "message": "Product created successfully",
  "product": {
    "id": 1,
    "name": "Product Name",
    "description": "Product description",
    "price": 999.99,
    "stockQuantity": 50,
    "category": "Electronics",
    "brand": "Brand Name",
    "hasImage": true,
    "createdAt": "2024-01-01T10:00:00"
  }
}
```

**Response (Error):**
```json
{
  "error": "Only image files are allowed"
}
```

### 11. Update Product
**PUT** `/products/{id}`

**Request Body:**
```json
{
  "name": "Updated Product Name",
  "description": "Updated description",
  "price": 1299.99,
  "stockQuantity": 30,
  "category": "Electronics",
  "brand": "Updated Brand"
}
```

**Response (Success):**
```json
{
  "message": "Product updated successfully",
  "product": {
    "id": 1,
    "name": "Updated Product Name",
    "description": "Updated description",
    "price": 1299.99,
    "stockQuantity": 30,
    "category": "Electronics",
    "brand": "Updated Brand",
    "updatedAt": "2024-01-01T11:00:00"
  }
}
```

### 12. Delete Product
**DELETE** `/products/{id}`

**Response (Success):**
```json
{
  "message": "Product deleted successfully"
}
```

**Response (Error):**
```json
{
  "error": "Product not found"
}
```

## Cart Endpoints

### 13. Add to Cart
**POST** `/cart/add`

**Request Body:**
```json
{
  "userId": 1,
  "productId": 1,
  "quantity": 2
}
```

**Response (Success):**
```json
{
  "message": "Product added to cart successfully",
  "cartItem": {
    "id": 1,
    "quantity": 2,
    "product": {
      "id": 1,
      "name": "Product Name",
      "price": 999.99,
      "hasImage": true
    }
  }
}
```

**Response (Error):**
```json
{
  "error": "Insufficient stock"
}
```

### 14. Get Cart Items
**GET** `/cart/user/{userId}`

**Response:**
```json
[
  {
    "id": 1,
    "quantity": 2,
    "product": {
      "id": 1,
      "name": "Product Name",
      "description": "Product description",
      "price": 999.99,
      "stockQuantity": 50,
      "category": "Electronics",
      "hasImage": true
    }
  }
]
```

### 15. Update Cart Item
**PUT** `/cart/update/{cartItemId}`

**Request Body:**
```json
{
  "quantity": 3
}
```

**Response (Success):**
```json
{
  "message": "Cart item updated successfully",
  "cartItem": {
    "id": 1,
    "quantity": 3,
    "product": {
      "id": 1,
      "name": "Product Name",
      "price": 999.99
    }
  }
}
```

### 16. Remove from Cart
**DELETE** `/cart/remove/{cartItemId}`

**Response (Success):**
```json
{
  "message": "Item removed from cart successfully"
}
```

### 17. Get Cart Total
**GET** `/cart/total/{userId}`

**Response:**
```json
{
  "total": 1999.98,
  "itemCount": 2
}
```

### 18. Clear Cart
**DELETE** `/cart/clear/{userId}`

**Response (Success):**
```json
{
  "message": "Cart cleared successfully"
}
```

## Order Endpoints

### 19. Create Order
**POST** `/orders/create`

**Request Body:**
```json
{
  "userId": 1,
  "shippingAddress": "123 Main St, City, State, 12345",
  "paymentMethod": "Credit Card"
}
```

**Response (Success):**
```json
{
  "message": "Order created successfully",
  "orderId": 1,
  "order": {
    "id": 1,
    "totalAmount": 1999.98,
    "status": "PENDING",
    "shippingAddress": "123 Main St, City, State, 12345",
    "paymentMethod": "Credit Card",
    "orderDate": "2024-01-01T10:00:00",
    "orderItems": [
      {
        "id": 1,
        "quantity": 2,
        "price": 999.99,
        "product": {
          "id": 1,
          "name": "Product Name"
        }
      }
    ]
  }
}
```

**Response (Error):**
```json
{
  "error": "Cart is empty"
}
```

### 20. Get User Orders
**GET** `/orders/user/{userId}`

**Response:**
```json
[
  {
    "id": 1,
    "totalAmount": 1999.98,
    "status": "PENDING",
    "shippingAddress": "123 Main St, City, State, 12345",
    "paymentMethod": "Credit Card",
    "orderDate": "2024-01-01T10:00:00",
    "orderItems": [
      {
        "id": 1,
        "quantity": 2,
        "price": 999.99,
        "product": {
          "id": 1,
          "name": "Product Name"
        }
      }
    ]
  }
]
```

### 21. Get Order by ID
**GET** `/orders/{orderId}`

**Response:**
```json
{
  "id": 1,
  "totalAmount": 1999.98,
  "status": "PENDING",
  "shippingAddress": "123 Main St, City, State, 12345",
  "paymentMethod": "Credit Card",
  "orderDate": "2024-01-01T10:00:00",
  "user": {
    "id": 1,
    "fullName": "John Doe",
    "email": "john@example.com"
  },
  "orderItems": [
    {
      "id": 1,
      "quantity": 2,
      "price": 999.99,
      "product": {
        "id": 1,
        "name": "Product Name"
      }
    }
  ]
}
```

### 22. Get All Orders (Admin)
**GET** `/orders/admin/all`

**Response:**
```json
[
  {
    "id": 1,
    "totalAmount": 1999.98,
    "status": "PENDING",
    "shippingAddress": "123 Main St, City, State, 12345",
    "paymentMethod": "Credit Card",
    "orderDate": "2024-01-01T10:00:00",
    "user": {
      "id": 1,
      "fullName": "John Doe",
      "email": "john@example.com"
    }
  }
]
```

### 23. Update Order Status (Admin)
**PUT** `/orders/admin/status/{orderId}`

**Request Body:**
```json
{
  "status": "CONFIRMED"
}
```

**Response (Success):**
```json
{
  "message": "Order status updated successfully",
  "order": {
    "id": 1,
    "status": "CONFIRMED",
    "totalAmount": 1999.98,
    "orderDate": "2024-01-01T10:00:00"
  }
}
```

## Image Endpoints

### 24. Upload Image
**POST** `/images/upload`

**Content-Type:** `multipart/form-data`

**Form Data:**
- `file` (file): Image file (max 5MB)

**Response (Success):**
```json
{
  "message": "Image uploaded successfully",
  "fileName": "image.jpg",
  "fileSize": 1024000,
  "contentType": "image/jpeg"
}
```

**Response (Error):**
```json
{
  "error": "Only image files are allowed"
}
```

### 25. Get Product Image
**GET** `/images/product/{productId}`

**Response:** Binary image data with appropriate Content-Type header

### 26. Delete Product Image
**DELETE** `/images/product/{productId}`

**Response (Success):**
```json
{
  "message": "Product image deleted successfully"
}
```

## Dashboard Endpoints (Admin)

### 27. Get Dashboard Statistics
**GET** `/dashboard/stats`

**Response:**
```json
{
  "totalUsers": 150,
  "totalProducts": 45,
  "totalOrders": 89,
  "totalRevenue": 125000.50,
  "pendingOrders": 12,
  "lowStockProducts": 5
}
```

## Error Responses

### Common Error Codes:
- **400 Bad Request**: Invalid request data
- **401 Unauthorized**: Authentication required
- **403 Forbidden**: Access denied
- **404 Not Found**: Resource not found
- **500 Internal Server Error**: Server error

### Error Response Format:
```json
{
  "error": "Error message description",
  "timestamp": "2024-01-01T10:00:00",
  "status": 400
}
```

## Order Status Values:
- `PENDING`: Order placed, awaiting confirmation
- `CONFIRMED`: Order confirmed by admin
- `SHIPPED`: Order shipped
- `DELIVERED`: Order delivered to customer
- `CANCELLED`: Order cancelled

## User Roles:
- `USER`: Regular customer
- `ADMIN`: Administrator with full access

## File Upload Constraints:
- **Maximum file size**: 5MB
- **Supported formats**: JPG, JPEG, PNG, GIF, WEBP
- **Storage**: Database (LONGBLOB) or File System

## Notes:
1. All timestamps are in ISO 8601 format
2. Prices are in decimal format (e.g., 999.99)
3. Image uploads require multipart/form-data content type
4. Admin endpoints require ADMIN role
5. Cart operations require valid user authentication
6. Product images are served with appropriate caching headers